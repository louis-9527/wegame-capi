package com.wegame.capi.biz.module.base.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletJackpotResultDo;
import com.wegame.capi.biz.domain.biz.jackpot.*;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardAmountTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardGrantStatusEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class AbsJackpotHandler {
    @Autowired
    private DistributeLock distributeLock;

    @Autowired
    private JackpotRewardPoolService jackpotRewardPoolService;
    @Autowired
    private JackpotUserRewardRecordService jackpotUserRewardRecordService;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;

    public long generateId() {
        return snowflakeIdGenerator.nextId();
    }

    @Autowired
    private SingleWalletService singleWalletService;


    public abstract AbstractJackpotManager jackpotManager();

    public abstract JackpotTypeEnum getJackpotType();

    public abstract Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommission(BetPromotionTypeEnum betPromotionTypeEnum, BetTypeEnum betTypeEnum, Long betAmount);


    public abstract JackpotWinPrizeDo calculateWinPrize(JackpotDrawAndAwardDo reqDo);


    protected Long getRewardAmountAndSingleAward(JackpotDrawAndAwardDo drawAndAwardDo, JackpotRewardPoolTypeEnum jackpotRewardPoolType, Long guaranteed, Long betGameId) {
        String lockKey = LockNameUtil.getJackpotRewardLock(getJackpotType().getGameType());

        //todo 分布式锁等待
        return distributeLock.tryLockThenCall(lockKey, 5, 10, () -> {
            // 获取奖池金额
            JackpotRewardPoolDo rewardPoolDo = jackpotRewardPoolService.selectOneByType(jackpotRewardPoolType, getJackpotType());
            AssertUtil.assertNotNull(rewardPoolDo, ErrorCode.ERR_PARAM);
            // 再次判断奖池是否能抽奖
            AssertUtil.assertTrue(rewardPoolDo.canDrawJackpot(), ErrorCode.ERR_PARAM, "jackpot amount not enough");

            // 保底金额 > 当前jackpot金额，使用预留池，否则使用jackpot奖池
            JackpotRewardAmountTypeEnum rewardAmountTypeEnum = guaranteed > rewardPoolDo.getAmount() ?
                    JackpotRewardAmountTypeEnum.RESERVED_POOL_AMOUNT : JackpotRewardAmountTypeEnum.JACKPOT_POOL_AMOUNT;
            // 派奖金额： 当前jackpot金额大于保底金额，使用
            Long rewardAmount = Math.max(guaranteed, rewardPoolDo.getAmount()); // 中奖金额默认使用保底
            // 添加使用记录并更新奖池金额
            JackpotUserRewardRecordDo buildRecord = JackpotUserRewardRecordDo.build(drawAndAwardDo, betGameId, rewardAmount,
                    rewardPoolDo.getId(), jackpotRewardPoolType, rewardAmountTypeEnum, getJackpotType());
            buildRecord.setId(generateId());
            this.jackpotManager().winReward(buildRecord);
            // 清除缓存
            jackpotRewardPoolService.evictJackpotRewardPoolListCache(this.getJackpotType());
            jackpotUserRewardRecordService.evictHighestUserRewardRecordCache(this.getJackpotType());
            // 调用单钱包 并更新状态
            this.singleAward(JackpotAwardDo.build(drawAndAwardDo, buildRecord));
            return rewardAmount;
        }, "error");
    }

    /**
     * 调用单钱包派奖
     *
     * @param jackpotAwardDo
     */
    public void singleAward(JackpotAwardDo jackpotAwardDo) {
        // 获取当前奖池类型所有金额
        List<JackpotRewardPoolDo> rewardPoolDos = jackpotRewardPoolService.listAll(this.getJackpotType());
        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> rewardPoolMap = rewardPoolDos.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        SingleWalletJackpotResultDo jackpot = singleWalletService.jackpot(jackpotAwardDo, rewardPoolMap);
        if (jackpot.getSuccess()) {
            // 成功后更新状态
            jackpotUserRewardRecordService.update(JackpotUserRewardRecordDo.builder()
                            .id(jackpotAwardDo.getJackpotUserRewardRecordDo().getId())
                            .rewardGrantStatus(JackpotRewardGrantStatusEnum.SUCCESS)
                            .build(),
                    getJackpotType());
        }

    }

    private Map<JackpotRewardPoolTypeEnum, Long> buildCommissionResultMap(TreeMap<String, Long> betAmountCommissionConfig) {
        Map<JackpotRewardPoolTypeEnum, Long> commissionResultMap = new HashMap<>();
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            Long commissionAmount = betAmountCommissionConfig.get(poolType.name());
            commissionResultMap.put(poolType, Objects.isNull(commissionAmount) ? 0L : commissionAmount);
        }
        return commissionResultMap;
    }

    protected Map<JackpotRewardPoolTypeEnum, Long> buildDefaultCommissionResultMap() {
        Map<JackpotRewardPoolTypeEnum, Long> commissionResultMap = new HashMap<>();
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            commissionResultMap.put(poolType, 0L);
        }
        return commissionResultMap;
    }

    /**
     * 按照金额提成
     *
     * @param betAmount        投注金额
     * @param commissionConfig 提成配置
     * @return TreeMap<JackpotRewardPoolTypeEnum, Long>
     */
    protected Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommissionByAmount(Long betAmount, LamangConfig.CommissionConfig commissionConfig) {
        TreeMap<Long, TreeMap<String, Long>> amountCommissionConfig = commissionConfig.getAmountCommissionConfig();
        TreeMap<String, Long> betAmountCommissionConfig = amountCommissionConfig.get(betAmount);
        return Objects.nonNull(betAmountCommissionConfig) ? this.buildCommissionResultMap(betAmountCommissionConfig) : null;
    }

    /**
     * 按比例提成
     *
     * @param betAmount        投注金额
     * @param commissionConfig 提成配置
     * @return TreeMap<JackpotRewardPoolTypeEnum, Long>
     */
    protected Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommissionByProportion(Long betAmount, LamangConfig.CommissionConfig commissionConfig) {
        Map<JackpotRewardPoolTypeEnum, Long> commissionResultMap = new HashMap<>();
        TreeMap<String, BigDecimal> proportionCommissionConfig = commissionConfig.getProportionCommissionConfig();
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            BigDecimal rate = proportionCommissionConfig.get(poolType.name());
            if (Objects.isNull(rate)) {
                commissionResultMap.put(poolType, 0L);
            } else {
                rate = rate.divide(new BigDecimal(100));
                // betAmount -> commissionAmount 需要*10，增加一位小数精度
                long commissionAmount = new BigDecimal(betAmount).multiply(rate).multiply(BigDecimal.TEN).longValue();
                commissionResultMap.put(poolType, commissionAmount);
            }
        }
        return commissionResultMap;
    }

}

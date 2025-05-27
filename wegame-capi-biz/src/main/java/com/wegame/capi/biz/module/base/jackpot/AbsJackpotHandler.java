package com.wegame.capi.biz.module.base.jackpot;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeReqDo;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardGrantStatusEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
@Slf4j
@Component
public abstract class AbsJackpotHandler {
    @Autowired
    private LamangConfig lamangConfig;
    @Autowired
    private RandomService randomService;

    @Autowired
    private DistributeLock distributeLock;

    @Autowired
    private JackpotRewardPoolService jackpotRewardPoolService;
    @Autowired
    private JackpotUserRewardRecordService jackpotUserRewardRecordService;


    public abstract AbstractJackpotManager jackpotManager();

    public abstract JackpotTypeEnum getJackpotType();

    /**
     * 计算奖池提成
     *
     * @param betTypeEnum 投注类型
     * @param betAmount   投注金额
     * @return Map<奖池类型, 提成金额>
     * 只要方法正常返回，一定会包含所有的 JackpotRewardPoolTypeEnum类型，没有提成，则对应type value为0L
     */
    public Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommission(BetTypeEnum betTypeEnum, Long betAmount) {
        AssertUtil.assertNotNull(betTypeEnum, ErrorCode.ERR_PARAM, "betTypeEnum is null");
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM, "betAmount is null");
        AssertUtil.assertTrue(betAmount >= 0, ErrorCode.ERR_PARAM, "betAmount is low than 0");
        // 如果投注金额为0,则提成0
        if (betAmount == 0) {
            log.warn("JackpotHandler#calculateRewardPoolCommission betAmount is 0,commission return 0");
            return this.buildDefaultCommissionResultMap();
        }

        LamangConfig.JackPotConfig jackPotConfig = lamangConfig.getJackPotConfig();
        switch (betTypeEnum) {
            case NORMAL_BET:
                // 先匹配按照金额提成
                Map<JackpotRewardPoolTypeEnum, Long> normalAmountCommissionMap = this.calculateRewardPoolCommissionByAmount(betAmount, jackPotConfig.getCommonCommissionConfig());
                if (CollectionUtil.isNotEmpty(normalAmountCommissionMap)) {
                    return normalAmountCommissionMap;
                } else {
                    // 按照比例提成
                    return this.calculateRewardPoolCommissionByProportion(betAmount, jackPotConfig.getCommonCommissionConfig());
                }
            case BUY_BET:
                // 先匹配按照金额提成
                Map<JackpotRewardPoolTypeEnum, Long> buyAmountCommissionMap = this.calculateRewardPoolCommissionByAmount(betAmount, jackPotConfig.getBuyCommissionConfig());
                if (CollectionUtil.isNotEmpty(buyAmountCommissionMap)) {
                    return buyAmountCommissionMap;
                } else {
                    // 按照比例提成
                    return this.calculateRewardPoolCommissionByProportion(betAmount, jackPotConfig.getBuyCommissionConfig());
                }
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "JackpotHandler#calculateRewardPoolCommission, not support betType");

        }
    }



    public JackpotWinPrizeDo calculateWinPrize(Long betRecordId,String betId, Long amount,List<JackpotWinPrizeReqDo> prizeReqDos) {
        LinkedHashMap<Long, TreeMap<String, BigDecimal>> jackpotTriggerBetAmountConfig = lamangConfig.getJackPotConfig().getJackpotTriggerBetAmountConfig();
        JackpotWinPrizeDo jackpotWinPrizeDo = this.checkWinPrize(amount, prizeReqDos, jackpotTriggerBetAmountConfig);
        if (jackpotWinPrizeDo == null){
            // 未中奖
            return null;
        }
        // 获取奖池保底金额
        TreeMap<String, Long> guaranteedAmount = lamangConfig.getJackPotConfig().getGuaranteedAmount();
        // 添加分布式锁 进行奖池扣减
        JackpotRewardPoolTypeEnum jackpotRewardPoolType = jackpotWinPrizeDo.getJackpotRewardPoolType();
        String lockKey = "jackpot:lock:poolType:" + jackpotRewardPoolType.getCode();
       Long winAmount =  distributeLock.lockThenCall(lockKey,  () -> {
            // 获取奖池金额
            JackpotRewardPoolDo rewardPoolDo = jackpotRewardPoolService.getByType(this.getJackpotType(), jackpotRewardPoolType);
            AssertUtil.assertNotNull(rewardPoolDo, ErrorCode.ERR_PARAM);
            Long guaranteed= guaranteedAmount.get(jackpotRewardPoolType.getName());
            Long pireAmount = guaranteed; // 中奖金额默认使用保底
            if (rewardPoolDo.getAmount() != null && rewardPoolDo.getAmount() > guaranteed) {
                pireAmount = rewardPoolDo.getAmount();
            }
            // 添加使用记录并更新奖池金额
            JackpotUserRewardRecordDo buildRecord = JackpotUserRewardRecordDo.builder()
                    .betRecordId(betRecordId)
                    .gameRecordId(jackpotWinPrizeDo.getBetGameId())
                    .rewardAmount(pireAmount)
                    .rewardPoolId(rewardPoolDo.getId())
                    .rewardPoolType(jackpotRewardPoolType)
                    .rewardGrantSerialNo(String.valueOf(betRecordId))
                    .rewardGrantStatus(JackpotRewardGrantStatusEnum.NONE).build();
            Long recordId =  this.jackpotManager().winReward(buildRecord);
            // todo 调用单钱包派奖
            // 成功后更新状态
            JackpotUserRewardRecordDo build = JackpotUserRewardRecordDo.builder().id(recordId).rewardGrantStatus(JackpotRewardGrantStatusEnum.SUCCESS).build();
            jackpotUserRewardRecordService.update(JackpotTypeEnum.LAM_ANG,build);
            return pireAmount;
        },"error");
        jackpotWinPrizeDo.setAmount(winAmount);
        return jackpotWinPrizeDo;
 }

    private JackpotWinPrizeDo checkWinPrize(Long amount,List<JackpotWinPrizeReqDo> prizeReqDos, LinkedHashMap<Long, TreeMap<String, BigDecimal>> jackpotTriggerBetAmountConfig){
        JackpotRewardPoolTypeEnum poolTypeEnum = null;
        for (JackpotWinPrizeReqDo reqDo : prizeReqDos){
            for (Long poolAmount : jackpotTriggerBetAmountConfig.keySet()){
                if(amount>=poolAmount){
                    TreeMap<String, BigDecimal> poolConfig = jackpotTriggerBetAmountConfig.get(poolAmount);
                    String rewardPoolType = GameUtil.randomKey(poolConfig, randomService.randomInt(1, 1000000));
                    poolTypeEnum = JackpotRewardPoolTypeEnum.valueOfName(rewardPoolType);
                    if(poolTypeEnum !=null){
                        return JackpotWinPrizeDo.builder().betGameId(reqDo.getBetGameId())
                                .jackpotRewardPoolType(poolTypeEnum).build();
                    }
                }
            }
        }
        return null;
    }


    private Map<JackpotRewardPoolTypeEnum, Long> buildCommissionResultMap(TreeMap<String, Long> betAmountCommissionConfig) {
        Map<JackpotRewardPoolTypeEnum, Long> commissionResultMap = new HashMap<>();
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            Long commissionAmount = betAmountCommissionConfig.get(poolType.name());
            commissionResultMap.put(poolType, Objects.isNull(commissionAmount) ? 0L : commissionAmount);
        }
        return commissionResultMap;
    }

    private Map<JackpotRewardPoolTypeEnum, Long> buildDefaultCommissionResultMap() {
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
    private Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommissionByAmount(Long betAmount, LamangConfig.CommissionConfig commissionConfig) {
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
    private Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommissionByProportion(Long betAmount, LamangConfig.CommissionConfig commissionConfig) {
        Map<JackpotRewardPoolTypeEnum, Long> commissionResultMap = new HashMap<>();
        TreeMap<String, BigDecimal> proportionCommissionConfig = commissionConfig.getProportionCommissionConfig();
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            BigDecimal rate = proportionCommissionConfig.get(poolType.name());
            if (Objects.isNull(rate)) {
                commissionResultMap.put(poolType, 0L);
            } else {
                rate = rate.divide(new BigDecimal(100));
                commissionResultMap.put(poolType, new BigDecimal(betAmount).multiply(rate).longValue());
            }
        }
        return commissionResultMap;
    }

}

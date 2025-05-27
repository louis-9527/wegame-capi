package com.wegame.capi.biz.module.base.jackpot;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.jackpot.*;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardGrantStatusEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.CONST_10000000;

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
    @Autowired
    private SingleWalletService singleWalletService;
    private static final String jackpotKey ="jackpot:lock:poolType:";


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


    /**
     * 判断是否中奖
     * @return null:未中奖
     */
    public JackpotWinPrizeDo calculateWinPrize(JackpotDrawAndAwardDo reqDo) {
        try {
            LinkedHashMap<Long, TreeMap<String, BigDecimal>> jackpotTriggerBetAmountConfig = lamangConfig.getJackPotConfig().getJackpotTriggerBetAmountConfig();
            JackpotWinPrizeDo jackpotWinPrizeDo = this.checkReward(reqDo.getBetAmount(), reqDo.getGameCount(), jackpotTriggerBetAmountConfig);
            if (jackpotWinPrizeDo == null){
                // 未中奖
                return null;
            }
            // 获取奖池保底金额
            TreeMap<String, Long> guaranteedAmount = lamangConfig.getJackPotConfig().getGuaranteedAmount();
            // 添加分布式锁 进行奖池扣减
            JackpotRewardPoolTypeEnum jackpotRewardPoolType = jackpotWinPrizeDo.getJackpotRewardPoolType();
            Long winAmount = this.buildRewardAmount(reqDo,jackpotRewardPoolType, guaranteedAmount.get(jackpotRewardPoolType.name()),jackpotWinPrizeDo.getBetGameId());
            jackpotWinPrizeDo.setAmount(winAmount);
            return jackpotWinPrizeDo;
        }catch (Exception e){
            log.error("calculateWinPrize error",e);
            return null;
        }

 }

    private JackpotWinPrizeDo checkReward(Long amount, Map<Long,Integer> gameCountMap, LinkedHashMap<Long, TreeMap<String, BigDecimal>> jackpotTriggerBetAmountConfig){
        JackpotRewardPoolTypeEnum poolTypeEnum = null;
        for (Map.Entry<Long, Integer> entry : gameCountMap.entrySet()) {
            Long gameId = entry.getKey();   // 获取游戏 ID
            Integer count = entry.getValue(); // 获取对应的游戏计数
            for (int i=0;i<count;i++){
                for (Long poolAmount : jackpotTriggerBetAmountConfig.keySet()){
                    if(amount>=poolAmount){
                        TreeMap<String, BigDecimal> poolConfig = jackpotTriggerBetAmountConfig.get(poolAmount);
                        String rewardPoolType = GameUtil.randomKey(poolConfig,CONST_10000000, randomService.randomInt(1, CONST_10000000));
                        poolTypeEnum = JackpotRewardPoolTypeEnum.valueOfName(rewardPoolType);
                        if(poolTypeEnum !=null){
                            return JackpotWinPrizeDo.builder().betGameId(gameId)
                                    .jackpotRewardPoolType(poolTypeEnum).build();
                        }
                    }
                }
            }
        }

        return null;
    }

    private Long buildRewardAmount(JackpotDrawAndAwardDo drawAndAwardDo, JackpotRewardPoolTypeEnum jackpotRewardPoolType, Long guaranteed, Long betGameId){
        String lockKey = jackpotKey + jackpotRewardPoolType.getCode();

       return distributeLock.lockThenCall(lockKey,  () -> {
            // 获取奖池金额
            JackpotRewardPoolDo rewardPoolDo = jackpotRewardPoolService.getByType(this.getJackpotType(), jackpotRewardPoolType);
            AssertUtil.assertNotNull(rewardPoolDo, ErrorCode.ERR_PARAM);
            Long rewardAmount = guaranteed; // 中奖金额默认使用保底
            if (rewardPoolDo.getAmount() != null && rewardPoolDo.getAmount() > guaranteed) {
                rewardAmount = rewardPoolDo.getAmount();
            }
            // 添加使用记录并更新奖池金额
            JackpotUserRewardRecordDo buildRecord = JackpotUserRewardRecordDo.build(drawAndAwardDo,betGameId,rewardAmount,rewardPoolDo.getId(),jackpotRewardPoolType);
            Long recordId =  this.jackpotManager().winReward(buildRecord);
           buildRecord.setId(recordId);
           // 调用单钱包 并更新状态
           this.singlePayAmount(JackpotAwardDo.build(drawAndAwardDo,buildRecord));
            return rewardAmount;
        },"error");
    }

    /**
     * 调用单钱包派奖
     * @param jackpotAwardDo
     */
    public void singlePayAmount(JackpotAwardDo jackpotAwardDo){
        // 获取当前奖池类型所有金额
        List<JackpotRewardPoolDo> rewardPoolDos = jackpotRewardPoolService.listAll(this.getJackpotType());
        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> rewardPoolMap = rewardPoolDos.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        singleWalletService.jackpot(jackpotAwardDo,rewardPoolMap);
        // 成功后更新状态
        JackpotUserRewardRecordDo build = JackpotUserRewardRecordDo.builder().id(jackpotAwardDo.getJackpotUserRewardRecordDo().getId())
                .rewardGrantStatus(JackpotRewardGrantStatusEnum.SUCCESS).build();
        jackpotUserRewardRecordService.update(JackpotTypeEnum.LAM_ANG,build);
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

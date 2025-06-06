package com.wegame.capi.biz.module.jackpot;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotDrawAndAwardDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeDo;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.biz.manager.jackpot.LamangJackpotManager;
import com.wegame.capi.biz.module.base.jackpot.AbsJackpotHandler;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.CONST_10000000;

@Slf4j
@Service
public class LamangJackpotHandler extends AbsJackpotHandler {
    @Autowired
    private LamangConfig lamangConfig;
    @Autowired
    private LamangJackpotManager lamangJackpotManager;

    @Autowired
    private RandomService randomService;

    @Override
    public AbstractJackpotManager jackpotManager() {
        return lamangJackpotManager;
    }

    @Override
    public JackpotTypeEnum getJackpotType() {
        return JackpotTypeEnum.LAM_ANG;
    }

    /**
     * 计算奖池提成
     *
     * @param betTypeEnum 投注类型
     * @param betAmount   投注金额
     * @return Map<奖池类型, 提成金额>
     * 只要方法正常返回，一定会包含所有的 JackpotRewardPoolTypeEnum类型，没有提成，则对应type value为0L
     */
    @Override
    public Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommission(BetTypeEnum betTypeEnum, Long betAmount) {
        AssertUtil.assertNotNull(betTypeEnum, ErrorCode.ERR_PARAM, "betTypeEnum is null");
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM, "betAmount is null");
        AssertUtil.assertTrue(betAmount >= 0, ErrorCode.ERR_PARAM, "betAmount is low than 0");
        // 如果投注金额为0,则提成0
        if (betAmount == 0) {
            log.warn("JackpotHandler#calculateRewardPoolCommission betAmount is 0,commission return 0");
            return super.buildDefaultCommissionResultMap();
        }

        LamangConfig.JackPotConfig jackPotConfig = lamangConfig.getJackPotConfig();
        switch (betTypeEnum) {
            case NORMAL_BET:
                // 先匹配按照金额提成
                Map<JackpotRewardPoolTypeEnum, Long> normalAmountCommissionMap = super.calculateRewardPoolCommissionByAmount(betAmount, jackPotConfig.getCommonCommissionConfig());
                return CollectionUtil.isNotEmpty(normalAmountCommissionMap) ? normalAmountCommissionMap
                        // 按照比例提成
                        : super.calculateRewardPoolCommissionByProportion(betAmount, jackPotConfig.getCommonCommissionConfig());
            case BUY_BET:
                // 先匹配按照金额提成
                Map<JackpotRewardPoolTypeEnum, Long> buyAmountCommissionMap = super.calculateRewardPoolCommissionByAmount(betAmount, jackPotConfig.getBuyCommissionConfig());
                return CollectionUtil.isNotEmpty(buyAmountCommissionMap) ? buyAmountCommissionMap
                        // 按照比例提成
                        : super.calculateRewardPoolCommissionByProportion(betAmount, jackPotConfig.getBuyCommissionConfig());
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "JackpotHandler#calculateRewardPoolCommission, not support betType");

        }
    }

    /**
     * 判断是否中奖
     *
     * @return null:未中奖
     */
    @Override
    public JackpotWinPrizeDo calculateWinPrize(JackpotDrawAndAwardDo reqDo) {
        try {
            //todo 根据betPromotionType决定是否抽奖
            JackpotWinPrizeDo jackpotWinPrizeDo = this.checkReward(reqDo.getBetAmount(), reqDo.getGameCount());
            if (jackpotWinPrizeDo == null) {
                // 未中奖
                return null;
            }
            // 获取奖池保底金额
            TreeMap<String, Long> guaranteedAmount = lamangConfig.getJackPotConfig().getGuaranteedAmount();
            // 添加分布式锁 进行奖池扣减
            JackpotRewardPoolTypeEnum jackpotRewardPoolType = jackpotWinPrizeDo.getJackpotRewardPoolType();
            Long winAmount = this.buildRewardAmount(reqDo, jackpotRewardPoolType, guaranteedAmount.get(jackpotRewardPoolType.name()), jackpotWinPrizeDo.getBetGameId());
            jackpotWinPrizeDo.setAmount(winAmount);
            return jackpotWinPrizeDo;
        } catch (Exception e) {
            log.error("calculateWinPrize error", e);
            return null;
        }

    }

    private TreeMap<String, BigDecimal> getJackpotTriggerBetAmountConfig(Long amount) {
        LinkedHashMap<Long, TreeMap<String, BigDecimal>> jackpotTriggerBetAmountConfig = lamangConfig.getJackPotConfig().getJackpotTriggerBetAmountConfig();
        for (Long poolAmount : jackpotTriggerBetAmountConfig.keySet()) {
            if (amount >= poolAmount) {
                return jackpotTriggerBetAmountConfig.get(poolAmount);
            }
        }
        return null;
    }


    private JackpotWinPrizeDo checkReward(Long amount, Map<Long, Integer> gameCountMap) {
        JackpotRewardPoolTypeEnum poolTypeEnum = null;
        TreeMap<String, BigDecimal> jackpotTriggerBetAmountConfig = this.getJackpotTriggerBetAmountConfig(amount);
        for (Map.Entry<Long, Integer> entry : gameCountMap.entrySet()) {
            Long gameId = entry.getKey();   // 获取游戏 ID
            Integer count = entry.getValue(); // 获取对应的游戏计数
            for (int i = 0; i < count; i++) {
                String rewardPoolType = GameUtil.randomKey(jackpotTriggerBetAmountConfig, CONST_10000000, randomService.randomInt(1, CONST_10000000));
                poolTypeEnum = JackpotRewardPoolTypeEnum.valueOfName(rewardPoolType);
                if (poolTypeEnum != null) {
                    return JackpotWinPrizeDo.builder().betGameId(gameId)
                            .jackpotRewardPoolType(poolTypeEnum).build();
                }
            }
        }

        return null;
    }


}

package com.wegame.capi.biz.module.base.jackpot;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeReqDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class JackpotHandler {

    @Autowired
    LamangConfig lamangConfig;

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
                commissionResultMap.put(poolType, new BigDecimal(betAmount).multiply(rate).longValue());
            }
        }
        return commissionResultMap;
    }

    private List<JackpotWinPrizeDo> calculateWinPrize(Long betRecordId, List<JackpotWinPrizeReqDo> prizeReqDos) {
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

}

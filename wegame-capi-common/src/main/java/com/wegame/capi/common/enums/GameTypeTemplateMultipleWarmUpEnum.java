package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 投注类型模板预热倍率枚举
 *
 * @author apple
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GameTypeTemplateMultipleWarmUpEnum {

    IBONG_ADARNA(GameTypeEnum.IBONG_ADARNA, createWarmUpValidatorMap_ibongAdarna(), "IBONG_ADARNA"),

    ;
    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;
    /**
     * 预热校验器map
     */
    private final Map<BetSubTypeEnum, WarmUpValidator> warmUpValidatorMap;
    /**
     * 描述信息.
     */
    private final String desc;

    public static GameTypeTemplateMultipleWarmUpEnum valueOfCode(GameTypeEnum gameType) {
        for (GameTypeTemplateMultipleWarmUpEnum item : GameTypeTemplateMultipleWarmUpEnum.values()) {
            if (item.getGameType().equals(gameType)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 是否可预热
     *
     * @param betSubType         betSubType
     * @param currentEndMultiple currentEndMultiple
     * @return true/false
     */
    public boolean canWarmUp(BetSubTypeEnum betSubType, BigDecimal currentEndMultiple) {
        WarmUpValidator warmUpValidator = this.warmUpValidatorMap.get(betSubType);
        AssertUtil.assertNotNull(warmUpValidator, ErrorCode.ERR_PARAM, "no can use warmUpValidator");
        return warmUpValidator.canWarmUp(currentEndMultiple);
    }


    private static Map<BetSubTypeEnum, WarmUpValidator> createWarmUpValidatorMap_ibongAdarna() {
        Map<BetSubTypeEnum, WarmUpValidator> warmUpSubMap = new HashMap<>();
        warmUpSubMap.put(BetSubTypeEnum.NORMAL_BET, new WarmUpValidator(new BigDecimal("30")));
        warmUpSubMap.put(BetSubTypeEnum.EX_BET, new WarmUpValidator(new BigDecimal("30")));
        return warmUpSubMap;
    }

    /**
     * 预热计算
     */
    @Data
    @ToString
    @EqualsAndHashCode(callSuper = false)
    private static class WarmUpValidator {
        /**
         * 限制的倍率
         * 如：此值为1，则上限为1
         */
        private BigDecimal upMultipleLimit;

        public WarmUpValidator(BigDecimal upMultipleLimit) {
            AssertUtil.assertNotNull(upMultipleLimit, ErrorCode.ERR_PARAM, "upMultipleLimit is null");
            this.upMultipleLimit = upMultipleLimit;
        }

        /**
         * 是否可预热的倍率
         *
         * @param currentEndMultiple 当前的倍率的范围结束倍率
         * @return true/false
         */
        public boolean canWarmUp(BigDecimal currentEndMultiple) {
            AssertUtil.assertNotNull(currentEndMultiple, ErrorCode.ERR_PARAM, "currentEndMultiple is null");
            return currentEndMultiple.compareTo(this.upMultipleLimit) <= 0;
        }

    }

}

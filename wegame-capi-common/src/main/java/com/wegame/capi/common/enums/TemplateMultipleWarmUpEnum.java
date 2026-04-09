package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.*;

import java.math.BigDecimal;
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
public enum TemplateMultipleWarmUpEnum {

    MANGKUKULAM(GameTypeEnum.MANGKUKULAM,
            new ImmutableMap.Builder<String, WarmUpValidator>()
                    .put(MangkukulamTagEnum.NORMAL_BET_INITIAL_STAGE.getCode(), new WarmUpValidator(new BigDecimal("2")))
                    .put(MangkukulamTagEnum.BUY_BET_INITIAL_STAGE.getCode(), new WarmUpValidator(new BigDecimal("5")))
                    .put(MangkukulamTagEnum.FREE_GAME_STAGE.getCode(), new WarmUpValidator(new BigDecimal("-1")))
                    .build(),
            "MANGKUKULAM"),

    SIRENA(GameTypeEnum.SIRENA,
            new ImmutableMap.Builder<String, WarmUpValidator>()
                    .put(SirenaTagEnum.SIRENA_FIRST_ROUND.getCode(), new WarmUpValidator(new BigDecimal("5")))
                    .put(SirenaTagEnum.SIRENA_NEXT_ROUND.getCode(), new WarmUpValidator(new BigDecimal("-1")))
                    .put(SirenaTagEnum.SIRENA_BUY_BET.getCode(), new WarmUpValidator(new BigDecimal("-1")))
                    .build(),
            "SIRENA"),

    ;

    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;
    /**
     * 预热校验器map
     */
    private final Map<String, WarmUpValidator> warmUpValidatorMap;
    /**
     * 描述信息.
     */
    private final String desc;

    public static TemplateMultipleWarmUpEnum valueOfCode(GameTypeEnum gameType) {
        for (TemplateMultipleWarmUpEnum item : TemplateMultipleWarmUpEnum.values()) {
            if (item.getGameType().equals(gameType)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 是否可预热
     *
     * @param tag         betSubType
     * @param currentEndMultiple currentEndMultiple
     * @return true/false
     */
    public boolean canWarmUp(String tag, BigDecimal currentEndMultiple) {
        WarmUpValidator warmUpValidator = this.warmUpValidatorMap.get(tag);
        AssertUtil.assertNotNull(warmUpValidator, ErrorCode.ERR_PARAM, "no can use warmUpValidator");
        return warmUpValidator.canWarmUp(currentEndMultiple);
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

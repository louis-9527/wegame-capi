package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

/**
 * 投注子类型
 *
 * @author apple
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BetSubTypeEnum {

    // 正常投注
    NORMAL_BET("NORMAL_BET",
            "正常投注",
            BetTypeEnum.NORMAL_BET),
    FREE_SPINS_NORMAL_BET("FREE_SPINS_NORMAL_BET",
            "免费轮查询-正常投注", // 用于查询投注记录
            BetTypeEnum.NORMAL_BET),
    FREE_WHEEL_NORMAL_BET("FREE_WHEEL_NORMAL_BET",
            "免费轮正常投注", // 支付金额为0并投注派彩
            BetTypeEnum.NORMAL_BET),
    EX_BET("EX_BET",
            "正常投注EX投注",
            BetTypeEnum.NORMAL_BET),
    // 免费轮投注
    BUY_BET("BUY_BET",
            "购买投注",
            BetTypeEnum.BUY_BET),
    FREE_WHEEL_BUY_BET("FREE_WHEEL_BUY_BET",
            "免费轮购买投注", // 支付金额为0并投注派彩
            BetTypeEnum.BUY_BET),
    FREE_SPINS_BUY_BET("FREE_SPINS_BUY_BET",
            "免费轮查询-购买投注", // 用于查询投注记录
            BetTypeEnum.BUY_BET),
    ;

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述信息.
     */
    private final String desc;
    /**
     * 投注类型
     */
    private final BetTypeEnum betType;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    BetSubTypeEnum(String code, String desc, BetTypeEnum betType) {
        this.code = code;
        this.desc = desc;
        this.betType = betType;
    }

    public static BetSubTypeEnum valueOfCode(String code) {
        for (BetSubTypeEnum item : BetSubTypeEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Boolean isFreeWheel(String betSubType) {
        List<String> freeWheelCodeList = Arrays.asList(FREE_SPINS_NORMAL_BET.getCode(), FREE_SPINS_BUY_BET.getCode());
        return freeWheelCodeList.contains(betSubType);
    }

    public static Boolean isFirstStep(String betSubType) {
        List<String> freeWheelCodeList = Arrays.asList(NORMAL_BET.getCode(), BUY_BET.getCode());
        return freeWheelCodeList.contains(betSubType);
    }

    public static Boolean isSecondStep(String betSubType) {
        List<String> freeWheelCodeList = Arrays.asList(FREE_WHEEL_NORMAL_BET.getCode(), FREE_WHEEL_BUY_BET.getCode());
        return freeWheelCodeList.contains(betSubType);
    }
}

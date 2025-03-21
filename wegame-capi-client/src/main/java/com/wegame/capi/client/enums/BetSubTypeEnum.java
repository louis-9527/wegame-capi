package com.wegame.capi.client.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 投注子类型
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BetSubTypeEnum {

    NORMAL_BET("NORMAL_BET",
            "正常投注",
            BetTypeEnum.NORMAL_BET),
    FREE_SPINS_NORMAL_BET("FREE_SPINS_NORMAL_BET",
            "免费轮正常投注",
            BetTypeEnum.NORMAL_BET),
    BUY_BET("BUY_BET",
            "购买投注",
            BetTypeEnum.BUY_BET),
    FREE_SPINS_BUY_BET("FREE_SPINS_BUY_BET",
            "免费轮购买投注",
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

    //    public static BetSubTypeEnum valueOfFreeSpinsBetSubType(BetTypeEnum betType) {
    //        if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
    //            return FREE_SPINS_BUY_BET;
    //        } else if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
    //            return FREE_SPINS_NORMAL_BET;
    //        } else {
    //            return null;
    //        }
    //    }

    public static BetSubTypeEnum valueOfCode(String code) {
        for (BetSubTypeEnum item : BetSubTypeEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public BetTypeEnum getBetType() {
        return betType;
    }

    public static Boolean isFreeWheel(String betSubType) {
        List<String> freeWheelCodeList = Arrays.asList(FREE_SPINS_NORMAL_BET.getCode(), FREE_SPINS_BUY_BET.getCode());
        return freeWheelCodeList.contains(betSubType);
    }
}

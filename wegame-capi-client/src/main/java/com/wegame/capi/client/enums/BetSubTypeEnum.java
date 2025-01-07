package com.wegame.capi.client.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

/**
 * 投注子类型
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BetSubTypeEnum {

    NORMAL_BET("NORMAL_BET", "正常投注", BetTypeEnum.NORMAL_BET),
    FREE_SPINS_NORMAL_BET("NON_BUY_FREE_SPINS_BET", "免费轮正常投注", BetTypeEnum.NORMAL_BET),
    BUY_BET("BUY_BET", "购买投注", BetTypeEnum.BUY_BET),
    FREE_SPINS_BUY_BET("FREE_SPINS_BUY_BET", "免费轮购买投注", BetTypeEnum.BUY_BET),

    ;

    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;

    private final BetTypeEnum betType;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    private BetSubTypeEnum(String code, String desc, BetTypeEnum betType) {
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

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public BetTypeEnum getBetType() {
        return betType;
    }

    public static Boolean isFreeSpins(String betSubType) {
        List<String> freeCodeList =Arrays.asList(FREE_SPINS_NORMAL_BET.getCode(), FREE_SPINS_BUY_BET.getCode());
        return freeCodeList.contains(betSubType);
    }
}

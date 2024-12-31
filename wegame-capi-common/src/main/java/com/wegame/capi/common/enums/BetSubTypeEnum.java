package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

/**
 * 投注子类型
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BetSubTypeEnum {

    NORMAL_BET("NORMAL_BET", "正常投注", BetTypeEnum.NORMAL_BET),
    BUY_FREE_SPINS_BET("BUY_FREE_SPINS_BET", "购买免费轮投注", BetTypeEnum.FREE_SPINS_BET),
    NON_BUY_FREE_SPINS_BET("NON_BUY_FREE_SPINS_BET", "非购买免费轮投注", BetTypeEnum.FREE_SPINS_BET),

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
}

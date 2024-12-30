package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

/**
 * 游戏投注类型
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameBetTypeEnum {

    NORMAL_BET("NORMAL_BET", "正常投注"),
    NON_BUY_FREE_SPINS_BET("NON_BUY_FREE_SPINS_BET", "非购买免费轮投注"),
    BUY_FREE_SPINS_BET("BUY_FREE_SPINS_BET", "购买免费轮投注"),

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
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    private GameBetTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static GameBetTypeEnum valueOfCode(String code) {
        for (GameBetTypeEnum item : GameBetTypeEnum.values()) {
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
}

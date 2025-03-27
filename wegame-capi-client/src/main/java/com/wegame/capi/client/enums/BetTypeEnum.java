package com.wegame.capi.client.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

/**
 * 投注类型
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BetTypeEnum {

    NORMAL_BET("NORMAL_BET", "正常投注"),
    BUY_BET("BUY_BET", "购买投注"),

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
    BetTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BetTypeEnum valueOfCode(String code) {
        for (BetTypeEnum item : BetTypeEnum.values()) {
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

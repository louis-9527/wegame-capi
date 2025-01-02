package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Objects;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameBetSubTypeEnum {

    NORMAL(11, "正常下注"),
    NON_BUY_FREE(21, "非购买免费轮"),
    BUY_FREE(22, "购买免费轮");

    /**
     * 枚举码.
     */
    private final Integer code;

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
    private GameBetSubTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static GameBetSubTypeEnum valueOfCode(Integer code) {
        for (GameBetSubTypeEnum item : GameBetSubTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

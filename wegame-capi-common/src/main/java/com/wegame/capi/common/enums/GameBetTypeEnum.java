package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Objects;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameBetTypeEnum {

    NORMAL(1, "正常下注"),
    FREE(2, "免费轮");

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
    private GameBetTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static GameBetTypeEnum valueOfCode(Integer code) {
        for (GameBetTypeEnum item : GameBetTypeEnum.values()) {
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

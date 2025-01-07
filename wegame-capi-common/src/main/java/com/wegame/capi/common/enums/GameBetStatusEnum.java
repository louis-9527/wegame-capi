package com.wegame.capi.common.enums;

import java.util.Objects;

public enum GameBetStatusEnum {

    ING(0, "进行中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

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
    private GameBetStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static GameBetStatusEnum valueOfCode(Integer code) {
        for (GameBetStatusEnum item : GameBetStatusEnum.values()) {
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

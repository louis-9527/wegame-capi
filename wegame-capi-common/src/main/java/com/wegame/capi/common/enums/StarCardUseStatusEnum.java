package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StarCardUseStatusEnum {

    ING(0, "使用中"),
    SUCCESS(1, "核销成功"),
    FAIL(2, "核销失败");

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
    private StarCardUseStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StarCardUseStatusEnum valueOfCode(Integer code) {
        for (StarCardUseStatusEnum item : StarCardUseStatusEnum.values()) {
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

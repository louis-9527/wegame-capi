package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum JackpotAccFlagEnum {

    NO(0, "未累计"),
    YES(1, "已累计");

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
    JackpotAccFlagEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static JackpotAccFlagEnum valueOfCode(Integer code) {
        for (JackpotAccFlagEnum item : JackpotAccFlagEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

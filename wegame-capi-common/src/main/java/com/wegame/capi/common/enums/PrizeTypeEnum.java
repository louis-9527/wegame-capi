package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PrizeTypeEnum {

    NONE_PRIZE(-1, "未中大奖"),
    NICE_PRIZE(0, "nice"),
    MEGA_PRIZE(1, "mega"),
    SUPER_PRIZE(2, "super"),
    SENSATIONAL_PRIZE(3, "sensational");

    /**
     * 枚举码.
     */
    private final Integer code;

    /**
     * 描述信息.
     */
    private final String desc;

    public static PrizeTypeEnum valueOfCode(Integer code) {
        for (PrizeTypeEnum item : PrizeTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

}

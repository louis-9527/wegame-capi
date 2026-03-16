package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 业务干预类型
 */
@Getter
@AllArgsConstructor
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BizInterfereTypeEnum {

    NONE(0, "无"),
    MULTIPLE_CONTROL(1, "倍率控制"),
    ;

    /**
     * 枚举码.
     */
    private final Integer code;

    /**
     * 描述信息.
     */
    private final String desc;


    public static BizInterfereTypeEnum valueOfCode(Integer code) {
        for (BizInterfereTypeEnum item : BizInterfereTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

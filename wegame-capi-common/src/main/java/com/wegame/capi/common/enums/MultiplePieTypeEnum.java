package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 倍率盘类型枚举
 *
 * @author apple
 */
@Getter
@AllArgsConstructor
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MultiplePieTypeEnum {

    NORMAL("NORMAL", "正常倍率"),
    BAKUNAWA("BAKUNAWA", "Bakunawa倍率"),
    ;

    /**
     * 元素名称.
     */
    private final String code;

    /**
     * 元素名称.
     */
    private final String desc;

    public static MultiplePieTypeEnum valueOfCode(String code) {
        for (MultiplePieTypeEnum item : MultiplePieTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}
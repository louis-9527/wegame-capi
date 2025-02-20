package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 倍率类型枚举
 *
 * @author apple
 */
@Getter
@AllArgsConstructor
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MultipleTypeEnum {

    NORMAL("NORMAL", "正常倍率"),
    EX("EX", "ex倍率"),
    ;

    /**
     * 元素名称.
     */
    private final String code;

    /**
     * 元素名称.
     */
    private final String desc;

    public static MultipleTypeEnum valueOfCode(String code) {
        for (MultipleTypeEnum item : MultipleTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

}

package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 元素分类类型枚举
 *
 * @author apple
 */
@Getter
@AllArgsConstructor
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SymbolBreakUpEnum {

    NO_BREAK_UP(1, "NO_BREAK_UP"),
    TWO_BREAK_UP(2, "TWO_BREAK_UP"),
    THREE_BREAK_UP(3, "THREE_BREAK_UP"),
    ;

    /**
     * 元素分裂倍数.
     */
    private final Integer times;

    /**
     * 元素名称.
     */
    private final String code;

    public static SymbolBreakUpEnum valueOfCode(String code) {
        for (SymbolBreakUpEnum item : SymbolBreakUpEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

}

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

    NO_BREAK_UP(1, "不分裂"),
    TWO_BREAK_UP(2, "两倍分裂"),
    THREE_BREAK_UP(3, "三倍分裂"),
    ;

    /**
     * 元素id.
     */
    private final Integer times;

    /**
     * 元素名称.
     */
    private final String desc;

    public static SymbolBreakUpEnum valueOfTimes(Integer times) {
        for (SymbolBreakUpEnum item : SymbolBreakUpEnum.values()) {
            if (Objects.equals(item.getTimes(), times)) {
                return item;
            }
        }
        return null;
    }

}

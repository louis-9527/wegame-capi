package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 元素重复类型枚举
 *
 * @author apple
 */
@ToString
@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SymbolRepeatTypeEnum {

    ONE(1, "ONE"),
    TWO(2, "TWO"),
    ;
    /**
     * 枚举码.
     */
    private final Integer times;

    /**
     * 描述信息.
     */
    private final String code;

    @JsonCreator
    public static SymbolRepeatTypeEnum valueOfCode(String code) {
        for (SymbolRepeatTypeEnum item : SymbolRepeatTypeEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

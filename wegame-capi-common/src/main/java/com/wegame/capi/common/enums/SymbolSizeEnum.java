package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 元素大小枚举
 *
 * @author apple
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SymbolSizeEnum {

    S("S", "小", 1, 1),
    M("M", "中", 2, 1),
    L("L", "大", 3, 1),
    XL("XL", "超大", 4, 1),


    ;

    /**
     * 元素id.
     */
    private final String code;
    /**
     * 元素大小名称.
     */
    private final String name;
    /**
     * 跨行数量
     */
    private final int rowSpan;
    /**
     * 跨列数量
     */
    private final int columnSpan;

    public static SymbolSizeEnum valueOfCode(String code) {
        for (SymbolSizeEnum item : SymbolSizeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static int getSymbolClearTimes(SymbolSizeEnum symbolSize) {
        switch (symbolSize) {
            case S:
                return 1;
            case M:
                return 2;
            case L:
                return 3;
            case XL:
                return 4;
        }
        return 0;
    }
}

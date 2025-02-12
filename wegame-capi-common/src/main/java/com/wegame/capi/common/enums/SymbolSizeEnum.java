package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 元素大小枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SymbolSizeEnum {

    S("S", "小", 1),
    M("M", "中", 2),
    L("L", "大", 3),
    XL("XL", "超大", 4),


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
     * 元素跨行
     */
    private final int rowSpan;


    /**
     * 私有构造方法.
     *
     * @param code      code
     * @param name      名称
     * @param rowSpan 跨行
     */
    SymbolSizeEnum(String code, String name, int rowSpan) {
        this.code = code;
        this.name = name;
        this.rowSpan = rowSpan;
    }

    public static SymbolSizeEnum valueOfCode(String code) {
        for (SymbolSizeEnum item : SymbolSizeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getRowSpan() {
        return rowSpan;
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

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
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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
    private final int columSpan;


    /**
     * 私有构造方法.
     *
     * @param code code
     * @param name 名称
     * @param columSpan 跨行
     */
    private SymbolSizeEnum(String code, String name, int columSpan) {
        this.code = code;
        this.name = name;
        this.columSpan = columSpan;
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

    public int getColumSpan() {
        return columSpan;
    }
}

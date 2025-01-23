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

    S("S", "小"),
    M("M", "中"),
    L("L", "大"),
    XL("XL", "超大"),


    ;

    /**
     * 元素id.
     */
    private final String code;

    /**
     * 元素名称.
     */
    private final String name;

    /**
     * 私有构造方法.
     *
     * @param id   id
     * @param name 名称
     */
    private SymbolSizeEnum(String code, String name) {
        this.code = code;
        this.name = name;
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
}

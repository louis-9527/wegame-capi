package com.wegame.capi.common.enums.lamang;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * lamang wild size enum
 *
 * @author apple
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum LamangSymbolSizeEnum {
    S("S", "小"),
    L("L", "大"),
    XL("XL", "超大"),

    ;

    /**
     * 元素id.
     */
    private final String code;
    /**
     * 元素大小名称.
     */
    private final String name;

    public static LamangSymbolSizeEnum valueOfCode(String code) {
        for (LamangSymbolSizeEnum item : LamangSymbolSizeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

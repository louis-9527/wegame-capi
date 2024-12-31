package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

/**
 * 元素类型枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SymbolTypeEnum {

    COMMON("COMMON", "普通元素"),
    SPECIAL("SPECIAL", "特殊元素"),

    ;

    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    private SymbolTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SymbolTypeEnum valueOfCode(String code) {
        for (SymbolTypeEnum item : SymbolTypeEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

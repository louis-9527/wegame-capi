package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wegame.capi.common.deserializer.EnumDeserializerInterface;
import com.wegame.capi.common.deserializer.SymbolTypeEnumDeserializer;
import lombok.ToString;

/**
 * 元素类型枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
//@JsonDeserialize(using = SymbolTypeEnumDeserializer.class)
public enum SymbolTypeEnum implements EnumDeserializerInterface<SymbolTypeEnum> {

    COMMON("COMMON", "普通元素"),
    WILD("WILD", "WILD元素"),
    SCATTER("SCATTER", "SCATTER元素");

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

    @Override
    public String jsonValue(){
        return code;
    }

    @Override
    public SymbolTypeEnum jsonCreator(String code) {
        return valueOfCode(code);
    }

    @JsonCreator
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

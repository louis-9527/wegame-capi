package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.common.deserializer.EnumDeserializerInterface;
import lombok.ToString;

/**
 * 元素规则类型枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SymbolRuleTypeEnum implements EnumDeserializerInterface<SymbolRuleTypeEnum> {

    COMMON("COMMON", "普通"),
    MULTIPLE("MULTIPLE", "倍数"),

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
    private SymbolRuleTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SymbolRuleTypeEnum valueOfCode(String code) {
        for (SymbolRuleTypeEnum item : SymbolRuleTypeEnum.values()) {
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

    @Override
    public String jsonValue() {
        return code;
    }

    @Override
    public SymbolRuleTypeEnum jsonCreator(String jsonValue) {
        return valueOfCode(jsonValue);
    }
}

package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.common.deserializer.EnumDeserializerInterface;
import lombok.ToString;

/**
 * 中奖线子类型
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RewardLineSubTypeEnum implements EnumDeserializerInterface<RewardLineSubTypeEnum> {

    COMMON("COMMON", "普通"),
    SCATTER("SCATTER", "SCATTER"),

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
    private RewardLineSubTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RewardLineSubTypeEnum valueOfCode(String code) {
        for (RewardLineSubTypeEnum item : RewardLineSubTypeEnum.values()) {
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
    public RewardLineSubTypeEnum jsonCreator(String jsonValue) {
        return valueOfCode(jsonValue);
    }
}

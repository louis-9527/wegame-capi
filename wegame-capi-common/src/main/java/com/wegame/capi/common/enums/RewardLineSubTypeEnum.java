package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * 中奖线子类型
 *
 * @author apple
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RewardLineSubTypeEnum {

    COMMON("COMMON", "普通"),
    SCATTER("SCATTER", "SCATTER"),
    CASH("CASH", "现金"),

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
    RewardLineSubTypeEnum(String code, String desc) {
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
}

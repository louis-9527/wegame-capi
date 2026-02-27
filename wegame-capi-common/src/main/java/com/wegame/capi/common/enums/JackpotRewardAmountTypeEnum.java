package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 奖池内金额类型枚举
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum JackpotRewardAmountTypeEnum {

    JACKPOT_POOL_AMOUNT(1, "JACKPOT_POOL_AMOUNT","jackpot池金额"),
    RESERVED_POOL_AMOUNT(2, "RESERVED_POOL_AMOUNT","预留池金额");

    /**
     * 枚举码.
     */
    private final Integer code;

    private final String name;

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
    JackpotRewardAmountTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static JackpotRewardAmountTypeEnum valueOfCode(Integer code) {
        for (JackpotRewardAmountTypeEnum item : JackpotRewardAmountTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static JackpotRewardAmountTypeEnum valueOfName(String name) {
        for (JackpotRewardAmountTypeEnum item : JackpotRewardAmountTypeEnum.values()) {
            if (Objects.equals(item.getName(), name)) {
                return item;
            }
        }
        return null;
    }
}

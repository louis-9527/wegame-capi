package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 血池种类枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BloodPoolLevelEnum {

    level_0(0, "level_0"),
    level_1(1, "level_1"),
    level_2(2, "level_2"),
    level_3(3, "level_3"),
    level_4(4, "level_4"),
    level_5(5, "level_5"),
    level_6(6, "level_6"),
    level_7(7, "level_7"),
    level_8(8, "level_8"),
    level_9(9, "level_9"),

    ;

    /**
     * 枚举码.
     */
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    public static BloodPoolLevelEnum valueOfCode(int code) {
        for (BloodPoolLevelEnum item : BloodPoolLevelEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}

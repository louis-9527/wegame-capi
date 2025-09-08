package com.wegame.capi.client.enums.innerdos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 大盘内部干预类型枚举
 */
@AllArgsConstructor
@Getter
public enum GameInnerDosTypeEnum {
    TOO_LOW_DOS("too_low_dos", "过低干预"),
    TOO_HIGH_DOS("too_high_dos", "过高干预"),
    NORMAL_DOS("normal_dos", "正常干预"),
    SPECIAL_DOS("special_dos", "特殊干预"),
    ;

    private final String code;
    private final String desc;

    public static GameInnerDosTypeEnum valueOfCode(String code) {
        for (GameInnerDosTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }


}

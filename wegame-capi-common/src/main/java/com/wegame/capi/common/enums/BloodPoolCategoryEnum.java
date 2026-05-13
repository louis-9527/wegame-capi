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
public enum BloodPoolCategoryEnum {

    NORMAL_BET("NORMAL_BET", "NORMAL_BET"),
    BUY_BET("BUY_BET", "BUY_BET"),
    ALL("ALL", "ALL")    // ALL (NORMAL_BET + BUY_BET)

    ;

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    public static BloodPoolCategoryEnum valueOfCode(String code) {
        for (BloodPoolCategoryEnum item : BloodPoolCategoryEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

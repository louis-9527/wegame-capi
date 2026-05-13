package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * Es模板类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum EsTemplateTypeEnum {

    star("star", "星卡"),
    freeSpin("freeSpin", "freeSpin"),
    defaultA("defaultA", "defaultA"),
    defaultB("defaultB", "defaultA");

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    public static EsTemplateTypeEnum valueOfCode(String code) {
        for (EsTemplateTypeEnum item : EsTemplateTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

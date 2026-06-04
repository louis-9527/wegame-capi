package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * Ab Test类型枚举
 *
 * @author apple
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AtTypeEnum {

    /**
     * 数值化
     */
    NORMAL("normal", "normal"),
    /**
     * 模拟盘面/模版
     */
    TEMPLATE("template", "template"),
    ;

    /**
     * 枚举码.
     */
    private final String id;
    /**
     * 描述信息.
     */
    private final String desc;

    public static AtTypeEnum valueOfId(String id) {
        for (AtTypeEnum item : AtTypeEnum.values()) {
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }
}

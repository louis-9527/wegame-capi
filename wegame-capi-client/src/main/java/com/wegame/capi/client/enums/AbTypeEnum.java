package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;


/**
 * AB类型枚举
 *
 * @author apple
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AbTypeEnum {

    A("afe", "A"),
    B("aff", "B"),

    ;

    /**
     * 枚举码.
     */
    private final String id;
    /**
     * 描述信息.
     */
    private final String desc;

    public static AbTypeEnum valueOfId(String id) {
        for (AbTypeEnum item : AbTypeEnum.values()) {
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }
}

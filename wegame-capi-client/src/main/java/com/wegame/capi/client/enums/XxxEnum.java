package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 投注类型
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum XxxEnum {
    UN_KNOWN(-1, "UN_KNOWN");
    private final Integer code;
    private final String desc;

    public static XxxEnum valueOfCode(int code) {
        for (XxxEnum item : XxxEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return UN_KNOWN;
    }

}

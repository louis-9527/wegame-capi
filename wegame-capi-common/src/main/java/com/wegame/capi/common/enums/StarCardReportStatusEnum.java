package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StarCardReportStatusEnum {

    ING(0, "待上报"),
    NONE(1, "无须上报"),
    SUCCESS(2, "上报成功");

    /**
     * 枚举码.
     */
    private final Integer code;

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
    private StarCardReportStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StarCardReportStatusEnum valueOfCode(Integer code) {
        for (StarCardReportStatusEnum item : StarCardReportStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

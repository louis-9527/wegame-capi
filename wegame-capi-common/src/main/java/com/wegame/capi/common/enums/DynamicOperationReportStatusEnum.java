package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DynamicOperationReportStatusEnum {

    ING(0, "上报中"),
    FINISH_NORMAL(1, "上报成功"),
    FINISH_EXPIRED(2, "上报失败");

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
    private DynamicOperationReportStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DynamicOperationReportStatusEnum valueOfCode(Integer code) {
        for (DynamicOperationReportStatusEnum item : DynamicOperationReportStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

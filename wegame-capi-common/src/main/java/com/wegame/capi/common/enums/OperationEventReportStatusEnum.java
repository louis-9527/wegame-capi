package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OperationEventReportStatusEnum {

    NONE(0, "未上报(默认)"),
    FINISH(1, "已上报"),
    FAIL(2, "上报失败");

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
    private OperationEventReportStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationEventReportStatusEnum valueOfCode(Integer code) {
        for (OperationEventReportStatusEnum item : OperationEventReportStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

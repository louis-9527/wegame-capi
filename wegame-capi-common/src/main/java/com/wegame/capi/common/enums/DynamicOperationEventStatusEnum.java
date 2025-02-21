package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DynamicOperationEventStatusEnum {

    NONE(0, "未知"),
    FINISH_NORMAL(1, "干预完成(正常)"),
    FINISH_EXPIRED(2, "干预完成(过期)");

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
    private DynamicOperationEventStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DynamicOperationEventStatusEnum valueOfCode(Integer code) {
        for (DynamicOperationEventStatusEnum item : DynamicOperationEventStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

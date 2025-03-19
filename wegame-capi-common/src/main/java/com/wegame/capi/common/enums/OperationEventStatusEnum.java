package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OperationEventStatusEnum {

    NONE(0, "可干预"),
    ING(1, "干预中"),
    FINISH(2, "干预完成");

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
    private OperationEventStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static OperationEventStatusEnum valueOfCode(Integer code) {
        for (OperationEventStatusEnum item : OperationEventStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

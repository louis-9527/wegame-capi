package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DynamicOperationEventKindEnum {

    NORMAL_URGENT(1, "正常投注紧急干预"),
    NORMAL_NORMAL(2, "正常投注普通干预"),
    FREE_URGENT(3, "免费游戏紧急干预"),
    FREE_NORMAL(4, "免费游戏普通干预");

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
    DynamicOperationEventKindEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DynamicOperationEventKindEnum valueOfCode(Integer code) {
        for (DynamicOperationEventKindEnum item : DynamicOperationEventKindEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

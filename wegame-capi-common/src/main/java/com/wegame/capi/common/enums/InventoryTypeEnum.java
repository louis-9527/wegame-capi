package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 库存类型
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum InventoryTypeEnum {

    JUNIOR(1, "初级"),
    INTERMEDIATE(2, "中级"),
    SENIOR(3, "高级"),
    MASTER(4, "大师");


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
    InventoryTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static InventoryTypeEnum valueOfCode(Integer code) {
        for (InventoryTypeEnum item : InventoryTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

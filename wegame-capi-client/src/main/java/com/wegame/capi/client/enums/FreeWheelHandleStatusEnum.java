package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * 免费轮处理状态
 *
 * @author apple
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum FreeWheelHandleStatusEnum {

    NOT_REQUIRED("NOT_REQUIRED", "无需处理"),
    PENDING("PENDING", "待处理"),
    PROCESSED("PROCESSED", "已处理"),
    ;

    /**
     * 枚举码.
     */
    private final String code;
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
    FreeWheelHandleStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FreeWheelHandleStatusEnum valueOfCode(String code) {
        for (FreeWheelHandleStatusEnum item : FreeWheelHandleStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OperationEventSubStatusEnum {

    NONE(0, "处理中"),
    FINISH_NORMAL(1, "干预完成(正常)"),
    FINISH_EXPIRED(2, "干预完成(事件超时)"),
    FINISH_AMOUNT_ERROR(3, "干预完成(投注金额不符)");

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
    private OperationEventSubStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationEventSubStatusEnum valueOfCode(Integer code) {
        for (OperationEventSubStatusEnum item : OperationEventSubStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }


    public static Boolean finish(OperationEventSubStatusEnum subStatus) {
        if(subStatus == null){
            return Boolean.FALSE;
        }
        if (OperationEventSubStatusEnum.FINISH_NORMAL == subStatus
                || OperationEventSubStatusEnum.FINISH_EXPIRED == subStatus
                || OperationEventSubStatusEnum.FINISH_AMOUNT_ERROR == subStatus) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}

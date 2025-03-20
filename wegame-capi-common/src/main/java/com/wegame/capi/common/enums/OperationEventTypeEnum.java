package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.client.enums.BetTypeEnum;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OperationEventTypeEnum {

    NORMAL(0, "正常投注"),
    FREE(1, "购买免费轮");

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
    private OperationEventTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationEventTypeEnum valueOfCode(Integer code) {
        for (OperationEventTypeEnum item : OperationEventTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static OperationEventTypeEnum valueOfBetType(BetTypeEnum betTypeEnum){
        if(BetTypeEnum.NORMAL_BET == betTypeEnum){
            return OperationEventTypeEnum.NORMAL;
        }else if(BetTypeEnum.BUY_BET == betTypeEnum) {
            return OperationEventTypeEnum.FREE;
        }
        return null;
    }

}


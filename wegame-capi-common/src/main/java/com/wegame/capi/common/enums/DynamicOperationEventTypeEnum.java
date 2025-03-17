package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DynamicOperationEventTypeEnum {

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
    private DynamicOperationEventTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DynamicOperationEventTypeEnum valueOfCode(Integer code) {
        for (DynamicOperationEventTypeEnum item : DynamicOperationEventTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static DynamicOperationEventTypeEnum valueOfBetType(BetTypeEnum betTypeEnum){
        if(BetTypeEnum.NORMAL_BET == betTypeEnum){
            return DynamicOperationEventTypeEnum.NORMAL;
        }else if(BetTypeEnum.BUY_BET == betTypeEnum) {
            return DynamicOperationEventTypeEnum.FREE;
        }
        return null;
    }

}


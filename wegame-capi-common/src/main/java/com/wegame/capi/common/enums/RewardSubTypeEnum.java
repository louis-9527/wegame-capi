package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RewardSubTypeEnum {

    NONE("none", "未中奖"),
    NICE("nice", "nice"),
    MEGA("mega", "mega"),
    SUPER("super", "super"),
    SENSATIONAL("sensational", "sensational");

    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;

    public static RewardSubTypeEnum valueOfCode(String code) {
        for (RewardSubTypeEnum item : RewardSubTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

}

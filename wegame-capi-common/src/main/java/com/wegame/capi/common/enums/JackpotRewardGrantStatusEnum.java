package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum JackpotRewardGrantStatusEnum {

    NONE(0, "待派奖"),
    SUCCESS(1, "派奖成功");

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
    JackpotRewardGrantStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static JackpotRewardGrantStatusEnum valueOfCode(Integer code) {
        for (JackpotRewardGrantStatusEnum item : JackpotRewardGrantStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

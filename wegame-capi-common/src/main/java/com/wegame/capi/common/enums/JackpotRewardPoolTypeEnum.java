package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum JackpotRewardPoolTypeEnum {

    grand(1, "grand","grand"),
    major(2, "major","major"),
    minor(3, "minor","minor"),
    mini(4, "mini","mini");

    /**
     * 枚举码.
     */
    private final Integer code;

    private final String name;

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
    JackpotRewardPoolTypeEnum(Integer code,String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static JackpotRewardPoolTypeEnum valueOfCode(Integer code) {
        for (JackpotRewardPoolTypeEnum item : JackpotRewardPoolTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static JackpotRewardPoolTypeEnum valueOfName(String name) {
        for (JackpotRewardPoolTypeEnum item : JackpotRewardPoolTypeEnum.values()) {
            if (Objects.equals(item.getName(), name)) {
                return item;
            }
        }
        return null;
    }
}

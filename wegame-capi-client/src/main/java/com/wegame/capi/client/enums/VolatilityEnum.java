package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum VolatilityEnum {

    LOW("LOW", "低波动"),
    HIGH("HIGH", "高波动");

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
    VolatilityEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VolatilityEnum valueOfCode(String code) {
        for (VolatilityEnum item : VolatilityEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

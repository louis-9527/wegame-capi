package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum VolatilityTypeEnum {


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
    VolatilityTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VolatilityTypeEnum valueOfCode(String code) {
        for (VolatilityTypeEnum item : VolatilityTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static VolatilityTypeEnum random() {
        VolatilityTypeEnum[] values = values(); // 获取所有枚举值
        int index = ThreadLocalRandom.current().nextInt(values.length);
        return values[index];
    }


    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

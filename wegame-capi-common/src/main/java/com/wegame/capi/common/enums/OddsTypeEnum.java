package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * 赔率类型
 *
 * @author apple
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OddsTypeEnum {

    THREE("THREE", "三连赔率", "3"),
    FOUR("FOUR", "四连赔率", "4"),
    FIVE("FIVE", "五连赔率", "5"),
    SIX("SIX", "六连赔率", "6"),

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
     * 描述信息.
     */
    private final String no;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    OddsTypeEnum(String code, String desc, String no) {
        this.code = code;
        this.desc = desc;
        this.no = no;
    }

    public static OddsTypeEnum valueOfCode(String code) {
        for (OddsTypeEnum item : OddsTypeEnum.values()) {
            if (item.getCode().equals(code)) {
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

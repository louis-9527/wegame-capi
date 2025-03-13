package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 中奖线类型
 *
 * @author apple
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RewardLineTypeEnum {

    THREE(3,"THREE", "三连", OddsTypeEnum.THREE),
    FOUR(4,"FOUR", "四连", OddsTypeEnum.FOUR),
    FIVE(5,"FIVE", "五连", OddsTypeEnum.FIVE),
    SIX(6,"SIX", "六连", OddsTypeEnum.SIX),

    ;

    private int no;
    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;

    private OddsTypeEnum oddsType;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    RewardLineTypeEnum(Integer no, String code, String desc, OddsTypeEnum oddsType) {
        this.no = no;
        this.code = code;
        this.desc = desc;
        this.oddsType = oddsType;
    }

    public static RewardLineTypeEnum valueOfCode(String code) {
        for (RewardLineTypeEnum item : RewardLineTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static RewardLineTypeEnum valueOfNo(Integer no) {
        for (RewardLineTypeEnum item : RewardLineTypeEnum.values()) {
            if (Objects.equals(item.getNo(), no)) {
                return item;
            }
        }
        return null;
    }

    public Integer getNo() {
        return no;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public OddsTypeEnum getOddsType() {
        return oddsType;
    }
}

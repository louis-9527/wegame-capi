package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

/**
 * 投注类型模板预热倍率枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BetTypeTemplateOddsWarmUpEnum {

    NORMAL_BET("NORMAL_BET", "0", "正常投注"),
    BUY_BET("BUY_BET", "17.5", "购买投注"),

    ;
    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 预热赔率
     */
    private final String odds;
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
    BetTypeTemplateOddsWarmUpEnum(String code, String odds, String desc) {
        this.code = code;
        this.odds = odds;
        this.desc = desc;
    }

    public static BetTypeTemplateOddsWarmUpEnum valueOfCode(String code) {
        for (BetTypeTemplateOddsWarmUpEnum item : BetTypeTemplateOddsWarmUpEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getOdds() {
        return odds;
    }

    public String getDesc() {
        return desc;
    }
}

package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

/**
 * 投注类型模板预热倍率枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GameTypeTemplateOddsWarmUpEnum {

    MARIA_MAKILING("zhumeng001", "1", "17.5", "MARIA_MAKILING"),
    MALAKAS("zhumeng002", "3", "-1", "MALAKAS"),
    DIWATA_MAYARI("zhumeng006", "2", "-1", "DIWATA_MAYARI"),

    ;
    /**
     * 枚举码.
     */
    private final String id;
    /**
     * 正常投注预热赔率
     */
    private final String normalBetOdds;
    /**
     * 购买投注预热赔率
     */
    private final String buyBetOdds;
    /**
     * 描述信息.
     */
    private final String desc;
    /**
     * 私有构造方法.
     *
     * @param id code值
     * @param desc 描述信息
     */
    GameTypeTemplateOddsWarmUpEnum(String id, String normalBetOdds, String buyBetOdds, String desc) {
        this.id = id;
        this.normalBetOdds = normalBetOdds;
        this.buyBetOdds = buyBetOdds;
        this.desc = desc;
    }

    public static GameTypeTemplateOddsWarmUpEnum valueOfCode(String code) {
        for (GameTypeTemplateOddsWarmUpEnum item : GameTypeTemplateOddsWarmUpEnum.values()) {
            if (item.getId().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getNormalBetOdds() {
        return normalBetOdds;
    }

    public String getBuyBetOdds() {
        return buyBetOdds;
    }

    public String getDesc() {
        return desc;
    }
}

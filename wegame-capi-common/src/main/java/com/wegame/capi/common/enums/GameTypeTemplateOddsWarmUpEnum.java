package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * 投注类型模板预热倍率枚举
 *
 * @author apple
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GameTypeTemplateOddsWarmUpEnum {
    MARIA_MAKILING("zhumeng001", "1", "-1", "MARIA_MAKILING"),
    MALAKAS("zhumeng002", "3", "-1", "MALAKAS"),
    LAM_ANG("zhumeng004", "3", "-1", "LAMANG"),
    BAKUNAWA("zhumeng005", "5", "-1", "BAKUNAWA"),
    DIWATA_MAYARI("zhumeng006", "10", "3", "DIWATA_MAYARI")


    ;
    /**
     * 枚举码.
     */
    private final String id;
    /**
     * 正常投注预热倍率
     * 说明:
     * x > 0, 预热的范围 -> 左开右闭区间 -> （0, x]
     * x = 0, 预热范围 -> [0, 0]
     * x < 0, 不需要预热
     */
    private final String normalBetOdds;
    /**
     * 购买投注预热倍率
     * 同上.
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

}

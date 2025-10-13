package com.wegame.capi.client.enums.innerdos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


/**
 * 指标类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MetricsTypeEnum {
    IBONG_ADARNA(
            GameTypeEnum.IBONG_ADARNA,
            GameTypeEnum.IBONG_ADARNA.getName().toLowerCase() + "_" + "user_play_metrics",
            GameTypeEnum.IBONG_ADARNA.getName().toLowerCase() + "_" + "game_metrics"
    ),
    ;

    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;
    /**
     * 集合名称(用户指标)
     */
    private final String userPlayMetricsCollectionName;
    /**
     * 集合名称(大盘指标)
     */
    private final String gameMetricsCollectionName;


    public static MetricsTypeEnum valueOfGameType(GameTypeEnum gameType) {
        if (gameType == null) {
            return null;
        }
        for (MetricsTypeEnum item : MetricsTypeEnum.values()) {
            if (gameType == item.gameType) {
                return item;
            }
        }
        return null;
    }

}

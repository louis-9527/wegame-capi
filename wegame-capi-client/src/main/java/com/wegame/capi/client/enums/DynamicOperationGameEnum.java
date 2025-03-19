package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DynamicOperationGameEnum {

    MARIA_MAKILING_DYNAMIC(GameTypeEnum.MARIA_MAKILING, "mariamakiling_dynamicOperation_event", "mariamakiling_dynamicOperation_event_detail", "mariamakiling_dynamicOperation_event_config","mariamakiling_dynamicOperation_event_config_record");


    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;

    /**
     * 集合名称(事件)
     */
    private final String eventCollectionName;
    /**
     * 集合名称(事件明细)
     */
    private final String eventDetailCollectionName;

    /**
     * 集合名称(事件倍数配置)
     */
    private final String eventConfigCollectionName;

    /**
     * 集合名称(事件倍数配置)
     */
    private final String eventConfigRecordCollectionName;


    public static DynamicOperationGameEnum valueOfGameType(GameTypeEnum gameType) {
        if (gameType == null) {
            return null;
        }
        for (DynamicOperationGameEnum item : DynamicOperationGameEnum.values()) {
            if (gameType == item.gameType) {
                return item;
            }
        }
        return null;
    }

}

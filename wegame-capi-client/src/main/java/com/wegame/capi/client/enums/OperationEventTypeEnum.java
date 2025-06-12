package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


/**
 * 干预事件类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OperationEventTypeEnum {

    MARIA_MAKILING(
            GameTypeEnum.MARIA_MAKILING,
            "mariamakiling_operation_event",
            "mariamakiling_operation_event_detail",
            "mariamakiling_operation_event_config",
            "mariamakiling_operation_event_config_record"
    ),
    MALAKAS_DYNAMIC(GameTypeEnum.MALAKAS,
            "malakas_operation_event",
            "malakas_operation_event_detail",
            "malakas_operation_event_config",
            "malakas_operation_event_config_record"
    ),
    IBONG_ADARNA(
            GameTypeEnum.IBONG_ADARNA,
            "ibongadarna_operation_event",
            "ibongadarna_operation_event_detail",
            "ibongadarna_operation_event_config",
            "ibongadarna_operation_event_config_record"
    ),
    DIWATA_MAYARI_DYNAMIC(GameTypeEnum.DIWATA_MAYARI,
            "diwataMayari_operation_event",
            "diwataMayari_operation_event_detail",
            "diwataMayari_operation_event_config",
            "diwataMayari_operation_event_config_record"
    ),

    ;


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
     * 集合名称(投注记录模版配置)
     */
    private final String eventConfigCollectionName;
    /**
     * 集合名称(游戏记录模版配置)
     */
    private final String eventConfigRecordCollectionName;


    public static OperationEventTypeEnum valueOfGameType(GameTypeEnum gameType) {
        if (gameType == null) {
            return null;
        }
        for (OperationEventTypeEnum item : OperationEventTypeEnum.values()) {
            if (gameType == item.gameType) {
                return item;
            }
        }
        return null;
    }

}

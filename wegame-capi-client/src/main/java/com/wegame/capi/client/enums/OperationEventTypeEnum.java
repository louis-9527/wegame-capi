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
    BAKUNAUWA(
            GameTypeEnum.BAKUNAWA,
            "bakunawa_operation_event",
            "bakunawa_operation_event_detail",
            "bakunawa_operation_event_config",
            "bakunawa_operation_event_config_record"
    ),

    LAM_ANG(
            GameTypeEnum.LAM_ANG,
            "lamang_operation_event",
            "lamang_operation_event_detail",
            "lamang_operation_event_config",
            "lamang_operation_event_config_record"
    ),
    DIWATA_MAYARI(GameTypeEnum.DIWATA_MAYARI,
            "diwataMayari_operation_event",
            "diwataMayari_operation_event_detail",
            "diwataMayari_operation_event_config",
            "diwataMayari_operation_event_config_record"
    ),
    KAPRE(GameTypeEnum.KAPRE,
            "kapre_operation_event",
            "kapre_operation_event_detail",
            "kapre_operation_event_config",
            "kapre_operation_event_config_record"
    ),
    MANGKUKULAM(GameTypeEnum.MANGKUKULAM,
            "mangkukulam_operation_event",
            "mangkukulam_operation_event_detail",
            "mangkukulam_operation_event_config",
            "mangkukulam_operation_event_config_record"),
    SIRENA(GameTypeEnum.SIRENA,
            "sirena_operation_event",
                    "sirena_operation_event_detail",
                    "sirena_operation_event_config",
                    "sirena_operation_event_config_record"),
    ATLANTIS(GameTypeEnum.ATLANTIS,
            "atlantis_operation_event",
            "atlantis_operation_event_detail",
            "atlantis_operation_event_config",
            "atlantis_operation_event_config_record")
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

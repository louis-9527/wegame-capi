package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 模板事件类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TemplateTypeEnum {

    MARIA_MAKILING(
            GameTypeEnum.MARIA_MAKILING,
            "mariamakiling_bet_record_template",
            "mariamakiling_game_record_template"
    ),

    MALAKAS(
            GameTypeEnum.MALAKAS,
            "malakas_bet_record_template",
            ""
    ),
    ;


    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;
    /**
     * 投注记录
     */
    private final String betRecordTemplateName;
    /**
     * 游戏记录
     */
    private final String gameRecordTemplateName;

    public static TemplateTypeEnum valueOfGameType(GameTypeEnum gameType) {
        if (gameType == null) {
            return null;
        }
        for (TemplateTypeEnum item : TemplateTypeEnum.values()) {
            if (gameType == item.gameType) {
                return item;
            }
        }
        return null;
    }

}

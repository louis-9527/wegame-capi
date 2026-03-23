package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.TemplateConstants;
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

    MARIA_MAKILING(GameTypeEnum.MARIA_MAKILING, TemplateConstants.MARIAMAKILING_BET_RECORD_TEMPLATE),

    MALAKAS(GameTypeEnum.MALAKAS, TemplateConstants.MALAKAS_BET_RECORD_TEMPLATE),

    IBONG_ADARNA(GameTypeEnum.IBONG_ADARNA, TemplateConstants.IBONGADARNA_BET_RECORD_TEMPLATE),

    LAM_ANG(GameTypeEnum.LAM_ANG, TemplateConstants.LAMANG_BET_RECORD_TEMPLATE),

    LAM_ANG_V2(GameTypeEnum.LAM_ANG, TemplateConstants.LAMANG_BET_RECORD_TEMPLATE_V2),

    LAM_ANG_FREE_SPIN_V1(GameTypeEnum.LAM_ANG, TemplateConstants.LAMANG_FREE_SPIN_BET_RECORD_TEMPLATE_V1),

    BAKUNAWA(GameTypeEnum.BAKUNAWA, TemplateConstants.BAKUNAWA_BET_RECORD_TEMPLATE),

    ;

    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;
    /**
     * 投注记录
     */
    private final String betRecordTemplateName;

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

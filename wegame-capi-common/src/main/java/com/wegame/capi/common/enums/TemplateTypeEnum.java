package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.TemplateConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    BAKUNAWA(GameTypeEnum.BAKUNAWA, TemplateConstants.BAKUNAWA_BET_RECORD_TEMPLATE),

    MANGKUKULAM(GameTypeEnum.MANGKUKULAM, TemplateConstants.MANGKUKULAM_BET_RECORD_TEMPLATE),

    MANGKUKULAM_V3(GameTypeEnum.MANGKUKULAM, TemplateConstants.MANGKUKULAM_BET_RECORD_TEMPLATE_V3),

    SIRENA(GameTypeEnum.SIRENA, TemplateConstants.SIRENA_BET_RECORD_TEMPLATE),

    SIRENA_V2(GameTypeEnum.SIRENA, TemplateConstants.SIRENA_BET_RECORD_TEMPLATE_V2),

    SIRENA_V3(GameTypeEnum.SIRENA, TemplateConstants.SIRENA_BET_RECORD_TEMPLATE_V3),

    KAPRE(GameTypeEnum.KAPRE, TemplateConstants.KAPRE_BET_RECORD_TEMPLATE),

    ;

    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;
    /**
     * 投注记录
     */
    private final String betRecordTemplateName;

    public static List<TemplateTypeEnum> listByGameType(GameTypeEnum gameType) {
        if (gameType == null) {
            return null;
        }
        return Arrays.stream(TemplateTypeEnum.values())
                .filter(item -> item.gameType == gameType)
                .collect(Collectors.toList());
    }

}

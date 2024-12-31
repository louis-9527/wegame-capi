package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.common.constants.GameConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Objects;

/**
 * 赔率处理器类型enum
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum OddsHandlertypeEnum {
    MARIA_MAKILING(GameConstants.MARIA_MAKILING_GAME_ID, "mariaMakilingOddsHandler"),
    ;

    /**
     * 游戏id
     */
    private final String gameId;

    /**
     * 处理器名称
     */
    private final String handlerName;

    public static OddsHandlertypeEnum valueOfGameId(String gameId) {
        if (Objects.isNull(gameId)) {
            throw new BusinessException(ErrorCode.ERR_PARAM, "game id is null");
        }

        return Arrays.stream(OddsHandlertypeEnum.values()).filter(rule ->
                Objects.equals(gameId, rule.getGameId())).findFirst().orElseThrow(() ->
                new BusinessException(ErrorCode.ERR_PARAM, "no game can be found"));
    }


}

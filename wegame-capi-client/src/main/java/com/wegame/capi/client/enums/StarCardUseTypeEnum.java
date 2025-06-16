package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StarCardUseTypeEnum {
    MARIA_MAKILING(
            GameTypeEnum.MARIA_MAKILING,
            "mariamakiling_star_card_record"
    ),

    MALAKAS(
            GameTypeEnum.MALAKAS,
            "malakas_star_card_record"
    ),
    IBONG_ADARNA(
            GameTypeEnum.IBONG_ADARNA,
            "ibongadarna_star_card_record"
    ),
    LAM_ANG(
            GameTypeEnum.LAM_ANG,
            "lamang_star_card_record"
    ),

    ;
    /**
     * 游戏类型
     */
    private final GameTypeEnum gameType;
    /**
     * 星卡记录集合名称
     */
    private final String collectionName;

    public static StarCardUseTypeEnum valueOfGameType(GameTypeEnum gameType) {
        for (StarCardUseTypeEnum item : StarCardUseTypeEnum.values()) {
            if (Objects.equals(item.gameType, gameType)) {
                return item;
            }
        }
        return null;
    }
}

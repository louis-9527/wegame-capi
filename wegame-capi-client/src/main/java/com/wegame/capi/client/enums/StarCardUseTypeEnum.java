package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum StarCardUseTypeEnum {
    MARIA_MAKILING(GameTypeEnum.MARIA_MAKILING, "mariamakiling_startCard_record");
    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;

    /**
     *
     */
    private final String collectionName;

    public static StarCardUseTypeEnum valueOfGameType(GameTypeEnum gameType) {
        for (StarCardUseTypeEnum item : StarCardUseTypeEnum.values()) {
            if (item.gameType == gameType) {
                return item;
            }
        }
        return null;
    }

}

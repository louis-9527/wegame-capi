package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 游戏类型枚举
 *
 * @author apple
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GameTypeEnum {

    MARIA_MAKILING("zhumeng001",
            "MariaMakiling",
            "mariamakiling_bet_record",
            "mariamakiling_game_record", 300L),
    MALAKAS("zhumeng002",
            "Malakas",
            "malakas_bet_record",
            "malakas_game_record", 300L),
    IBONG_ADARNA("zhumeng003",
            "IbongAdarna",
            "ibongadarna_bet_record",
            "ibongadarna_game_record", 300L),


    ;


    /**
     * 枚举码.
     */
    private final String id;

    /**
     * 描述信息.
     */
    private final String name;

    /**
     * 投注记录集合名称.
     */
    private final String betRecordCollectionName;

    /**
     * 游戏记录集合名称.
     */
    private final String gameRecordCollectionName;

    /**
     * 最小投注金额
     */
    private final Long minBetAmount;

    public static GameTypeEnum valueOfId(String id) {
        for (GameTypeEnum item : GameTypeEnum.values()) {
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }

    public static GameTypeEnum valueOfName(String name) {
        for (GameTypeEnum item : GameTypeEnum.values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
}

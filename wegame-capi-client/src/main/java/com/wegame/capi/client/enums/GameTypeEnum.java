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
            "mariamakiling_game_record", 300L, 1.1),
    MALAKAS("zhumeng002",
            "Malakas",
            "malakas_bet_record",
            "malakas_game_record", 300L, 1.1),
    IBONG_ADARNA("zhumeng003",
            "IbongAdarna",
            "ibongadarna_bet_record",
            "ibongadarna_game_record", 300L, 1.2),
    LAM_ANG("zhumeng004",
            "LamAng",
            "lamang_bet_record",
            "lamang_game_record", 300L, 1.2),
    BAKUNAWA("zhumeng005",
            "Bakunawa",
            "bakunawa_bet_record",
            "bakunawa_game_record", 300L,2),


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

    /**
     * 最小投注倍数
     */
    private final double minBetMultiple;

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

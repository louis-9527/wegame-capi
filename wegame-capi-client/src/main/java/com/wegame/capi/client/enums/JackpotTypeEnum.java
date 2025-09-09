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
public enum JackpotTypeEnum {

    LAM_ANG(
            GameTypeEnum.LAM_ANG,
            "lamang_jackpot_reward_pool",
            "lamang_jackpot_reward_pool_input_record",
            "lamang_jackpot_user_reward_record"
    ),;


    ;


    /**
     * 枚举码.
     */
    private final GameTypeEnum gameType;
    /**
     * 集合名称(奖池)
     */
    private final String jackpotRewardPoolCollectionName;
    /**
     * 集合名称(奖池流水)
     */
    private final String jackpotRewardPoolInputRecordCollectionName;
    /**
     * 集合名称(派奖记录)
     */
    private final String jackpotUserRewardRewardCollectionName;


    public static JackpotTypeEnum valueOfGameType(GameTypeEnum gameType) {
        if (gameType == null) {
            return null;
        }
        for (JackpotTypeEnum item : JackpotTypeEnum.values()) {
            if (gameType == item.gameType) {
                return item;
            }
        }
        return null;
    }

}

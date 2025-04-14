package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RedeemRewardResStatusEnum {
    SUCCESS(200, "success"),
    INVALID_REWARD(5001, "invalid reward"),
    ACHIEVEMENT_NOT_FOUND(5002, "achievement not found"),
    invalid_redeemId(5003, "invalid redeemId"),
    ACHIEVEMENT_NOT_COMPLETED(5004, "achievement not completed"),
    OPERATION_IN_PROGRESS(5005, "operation_in_progress"),
    ACHIEVEMENT_ALREADY_REDEEMED(5006, "achievement already redeemed"),
    REWARD_EXPIRED(5007, "reward expired"),
    INVALID_TREASURE_CONFIG(5008, "invalid treasure config"),
    TREASURE_ALREADY_REDEEMED(5009, "treasure already redeemed"),
    PROVIDER_MISSION_NOT_FOUND(5010, "provider mission not found"),
    LEVEL_NOT_MET(5011, "level not met"),
    EXPERIENCE_NOT_MET(5012, "experience not met"),
    TREASURE_NOT_FOUND(5013, "treasure not found"),
    NO_CONFIG_MATCH_WITH_CHANNELID(5014, "no config match with channelId"),
    INVALID_REWARD_PAYOUT(5015, "invalid reward payout"),
    INSUFFICIENT_CREDIT(4014, "insufficient credit"),

    ;

    private final Integer code;
    private final String desc;

    RedeemRewardResStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RedeemRewardResStatusEnum valueOfCode(int code) {
        for (RedeemRewardResStatusEnum item : RedeemRewardResStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }


    public static ErrorCode convetErrorCode(int code) {
        if (INSUFFICIENT_CREDIT.code == code) {
            return ErrorCode.STAR_CARD_INSUFFICIENT_CREDIT;
        }
        if (EXPERIENCE_NOT_MET.code == code) {
            return ErrorCode.STAR_CARD_EXPIRED;
        }
        return null;
    }
}

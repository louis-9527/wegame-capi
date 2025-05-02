package com.wegame.capi.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AchievementErrorCode {
    INVALID_REWARD("5001", "invalid reward", ErrorCode.ACHIEVEMENT_INVALID_REWARD),
    ACHIEVEMENT_NOT_FOUND("5002", "achievement not found", ErrorCode.ACHIEVEMENT_ACHIEVEMENT_NOT_FOUND),
    INVALID_REDEEM_ID("5003", "invalid redeemId", ErrorCode.ACHIEVEMENT_INVALID_REDEEM_ID),
    ACHIEVEMENT_NOT_COMPLETED("5004", "achievement not completed", ErrorCode.ACHIEVEMENT_ACHIEVEMENT_NOT_COMPLETED),
    OPERATION_IN_PROGRESS("5005", "operation in progress", ErrorCode.ACHIEVEMENT_OPERATION_IN_PROGRESS),
    ACHIEVEMENT_ALREADY_REDEEMED("5006", "achievement already redeemed", ErrorCode.ACHIEVEMENT_ACHIEVEMENT_ALREADY_REDEEMED),
    REWARD_EXPIRED("5007", "reward expired", ErrorCode.ACHIEVEMENT_REWARD_EXPIRED),
    INVALID_TREASURE_CONFIG("5008", "invalid treasure config", ErrorCode.ACHIEVEMENT_INVALID_TREASURE_CONFIG),
    TREASURE_ALREADY_REDEEMED("5009", "treasure already redeemed", ErrorCode.ACHIEVEMENT_TREASURE_ALREADY_REDEEMED),
    PROVIDER_MISSION_NOT_FOUND("5010", "provider mission not found", ErrorCode.ACHIEVEMENT_PROVIDER_MISSION_NOT_FOUND),
    LEVEL_NOT_MET("5011", "level not met", ErrorCode.ACHIEVEMENT_LEVEL_NOT_MET),
    EXPERIENCE_NOT_MET("5012", "experience not met", ErrorCode.ACHIEVEMENT_EXPERIENCE_NOT_MET),
    TREASURE_NOT_FOUND("5013", "treasure not found", ErrorCode.ACHIEVEMENT_TREASURE_NOT_FOUND),
    NO_CONFIG_MATCH_WITH_CHANNEL_ID("5014", "no config match with channelId", ErrorCode.ACHIEVEMENT_NO_CONFIG_MATCH_WITH_CHANNEL_ID),
    INVALID_REWARD_PAYOUT("5015", "invalid reward payout", ErrorCode.ACHIEVEMENT_INVALID_REWARD_PAYOUT);

    private final String thirdCode;
    private final String msg;
    private final ErrorCode errorCode;

    AchievementErrorCode(String thirdCode, String msg, ErrorCode errorCode) {
        this.thirdCode = thirdCode;
        this.msg = msg;
        this.errorCode = errorCode;
    }

    public static AchievementErrorCode valueOfThirdCode(Integer thirdCode) {
        for (AchievementErrorCode item : values()) {
            if (StringUtils.equals(item.getThirdCode(), String.valueOf(thirdCode))) {
                return item;
            }
        }
        return null;
    }
}
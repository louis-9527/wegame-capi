package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 盘口状态
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SingleWalletResStatusEnum {
    SUCCESS(200, "success"),
    DUPLICATE_REFERENCE_ID(4001, "duplicate reference id"),
    REFERENCE_ID_OR_BET_ID_EMPTY(4002, "reference id / bet id empty"),
    BET_ID_NOT_EXIST(4003, "bet id not exist"),
    PLAYER_NOT_EXIST(4004, "player not exist"),
    MISSING_TOKEN_FOR_CHANNEL(4005, "missing token for channel"),
    INVALID_HASH(4006, "invalid hash"),
    INVALID_CANCEL_BET(4007, "invalid cancel bet"),
    SYSTEM_ERROR(4008, "system error"),
    THE_CHANNEL_SERVER_IS_UNDER_MAINTENANCE(4009, "the channel server is under maintenance"),
    INCORRECT_PARAM(4010, "incorrect param"),
    INVALID_CHANNEL_ID(4011, "invalid channelId"),
    INVALID_P_ID(4012, "invalid pId"),
    IP_NOT_ALLOWED(4013, "IP not allowed"),
    INSUFFICIENT_CREDIT(4014, "insufficient credit"),
    INVALID_TOKEN(4015, "Invalid token"),
    NO_JACKPOT_PERMISSION(4016, "No jackpot permission"),
    NO_GIFT_PERMISSION(4017, "No gift permission"),
    INVALID_PAY_OUT_LIST(4018, "Invalid payout list"),
    INVALID_BET(4019, "Invalid bet"),
    INVALID_LOGIN(4020, "Invalid login"),
    NO_BONUS_PERMISSION(4021, "No bonus permission"),
    INVALID_U_ID(4022, "Invalid Uid"),
    NO_LUCKY_DRAW_PERMISSION(4023, "No Lucky Draw Permission"),
    ;

    private final Integer code;
    private final String desc;

    SingleWalletResStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SingleWalletResStatusEnum valueOfCode(int code) {
        for (SingleWalletResStatusEnum item : SingleWalletResStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FreeSpinErrorCode {
    DUPLICATE_REFERENCE_ID("4001", "duplicate reference id", ErrorCode.FREE_SPIN_DUPLICATE_REFERENCE_ID),
    REFERENCE_ID_EMPTY("4002", "reference id / bet id empty", ErrorCode.FREE_SPIN_REFERENCE_ID_EMPTY),
    BET_ID_NOT_EXIST("4003", "bet id not exist", ErrorCode.FREE_SPIN_BET_ID_NOT_EXIST),
    PLAYER_NOT_EXIST("4004", "player not exist", ErrorCode.FREE_SPIN_PLAYER_NOT_EXIST),
    MISSING_TOKEN_FOR_CHANNEL("4005", "missing token for channel", ErrorCode.FREE_SPIN_MISSING_TOKEN_FOR_CHANNEL),
    INVALID_HASH("4006", "invalid hash", ErrorCode.FREE_SPIN_INVALID_HASH),
    INVALID_CANCEL_BET("4007", "invalid cancel bet", ErrorCode.FREE_SPIN_INVALID_CANCEL_BET),
    SYSTEM_ERROR("4008", "system error", ErrorCode.FREE_SPIN_SYSTEM_ERROR),
    CHANNEL_SERVER_UNDER_MAINTENANCE("4009", "the channel server is under maintenance", ErrorCode.FREE_SPIN_CHANNEL_SERVER_UNDER_MAINTENANCE),
    INCORRECT_PARAM("4010", "incorrect param", ErrorCode.FREE_SPIN_INCORRECT_PARAM),
    INVALID_CHANNEL_ID("4011", "invalid channelId", ErrorCode.FREE_SPIN_INVALID_CHANNEL_ID),
    INVALID_PID("4012", "invalid pId", ErrorCode.FREE_SPIN_INVALID_PID),
    IP_NOT_ALLOWED("4013", "IP not allowed", ErrorCode.FREE_SPIN_IP_NOT_ALLOWED),
    INSUFFICIENT_CREDIT("4014", "insufficient credit", ErrorCode.FREE_SPIN_INSUFFICIENT_CREDIT),
    INVALID_TOKEN("4015", "Invalid token", ErrorCode.FREE_SPIN_INVALID_TOKEN),
    NO_JACKPOT_PERMISSION("4016", "No jackpot permission", ErrorCode.FREE_SPIN_NO_JACKPOT_PERMISSION),
    NO_GIFT_PERMISSION("4017", "No gift permission", ErrorCode.FREE_SPIN_NO_GIFT_PERMISSION),
    INVALID_PAYOUT_LIST("4018", "Invalid payout list", ErrorCode.FREE_SPIN_INVALID_PAYOUT_LIST),
    INVALID_BET("4019", "Invalid bet", ErrorCode.FREE_SPIN_INVALID_BET),
    INVALID_LOGIN("4020", "Invalid login", ErrorCode.FREE_SPIN_INVALID_LOGIN),
    NO_BONUS_PERMISSION("4021", "No bonus permission", ErrorCode.FREE_SPIN_NO_BONUS_PERMISSION),
    INVALID_UID("4022", "Invalid Uid", ErrorCode.FREE_SPIN_INVALID_UID),
    NO_LUCKY_DRAW_PERMISSION("4023", "No Lucky Draw Permission", ErrorCode.FREE_SPIN_NO_LUCKY_DRAW_PERMISSION),
    NOT_SAME_PROVIDER_GAME("4024", "The player is not within the same provider game", ErrorCode.FREE_SPIN_NOT_SAME_PROVIDER_GAME),
    NO_SWAP_GAME_PERMISSION("4025", "No Swap Game Permission", ErrorCode.FREE_SPIN_NO_SWAP_GAME_PERMISSION),
    FREE_SPIN_DISABLED("4026", "free spin disabled", ErrorCode.FREE_SPIN_FREE_SPIN_DISABLED),
    CAMPAIGN_EXPIRED("4027", "campaign expired", ErrorCode.FREE_SPIN_CAMPAIGN_EXPIRED),
    FREE_SPINS_REMAINING_0("4028", "free spins remaining 0", ErrorCode.FREE_SPIN_FREE_SPINS_REMAINING_0),
    ;

    /** 三方错误码 */
    private final String thirdCode;
    /** 三方错误提示信息 */
    private final String msg;
    /** 关联的业务方错误码 */
    private final ErrorCode errorCode;

    public static FreeSpinErrorCode valueOfThirdCode(Integer thirdCode) {
        for (FreeSpinErrorCode item : values()) {
            if (StringUtils.equals(item.getThirdCode(), String.valueOf(thirdCode))) {
                return item;
            }
        }
        return null;
    }
}

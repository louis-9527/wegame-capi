package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * 单钱包营销res状态枚举
 * @see <a href="https://casinoplus.sg.larksuite.com/docx/XZzMdmW4aonhYfxtMEili1qGgkb">单钱包营销res状态枚举</a>
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SingleWalletPromotionResStatusEnum {
    SUCCESS(200, "success"),
    INVALID_REWARD(5001, "invalid reward"),
    ACHIEVEMENT_NOT_FOUND(5002, "achievement not found"),
    INVALID_REDEEM_ID(5003, "invalid redeemId"),
    ACHIEVEMENT_NOT_COMPLETED(5004, "achievement not completed"),
    OPERATION_IN_PROGRESS(5005, "operation in progress"),
    ACHIEVEMENT_ALREADY_REDEEMED(5006, "achievement already redeemed"),
    REWARD_EXPIRED(5007, "reward expired"),
    INVALID_TREASURE_CONFIG(5008, "invalid treasure config"),
    TREASURE_ALREADY_REDEEMED(5009, "treasure already redeemed"),
    PROVIDER_MISSION_NOT_FOUND(5010, "provider mission not found"),
    LEVEL_NOT_MET(5011, "level not met"),
    EXPERIENCE_NOT_MET(5012, "experience not met"),
    TREASURE_NOT_FOUND(5013, "treasure not found"),
    NO_CONFIG_MATCH_WITH_CHANNEL_ID(5014, "no config match with channelId"),
    INVALID_REWARD_PAYOUT(5015, "invalid reward payout");
    ;

    private final Integer code;
    private final String desc;

    SingleWalletPromotionResStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SingleWalletPromotionResStatusEnum valueOfCode(int code) {
        for (SingleWalletPromotionResStatusEnum item : SingleWalletPromotionResStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

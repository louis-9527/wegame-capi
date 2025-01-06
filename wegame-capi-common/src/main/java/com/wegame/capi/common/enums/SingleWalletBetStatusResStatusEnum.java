package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SingleWalletBetStatusResStatusEnum {
    RECEIVED_BET_REQUEST(1, "received bet request"),
    COMPLETED_BET_REQUEST(2, "completed bet request"),
    RECEIVED_PAYOUT_REQUEST(3, "received payout request"),
    COMPLETED_PAYOUT_REQUEST(4, "completed payout request"),
    RECEIVED_CANCEL_REQUEST(5, "received cancel request"),
    COMPLETED_CANCEL_REQUEST(6, "completed cancel request"),
    DATA_NOT_FOUND(7, "data not found");

    private final Integer code;
    private final String desc;

    public static SingleWalletBetStatusResStatusEnum valueOfCode(int code) {
        for (SingleWalletBetStatusResStatusEnum item : SingleWalletBetStatusResStatusEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

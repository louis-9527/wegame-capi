package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BetStatusEnum {

    ING(0, "进行中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    /**
     * 枚举码.
     */
    private final Integer code;

    /**
     * 描述信息.
     */
    private final String desc;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    private BetStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BetStatusEnum valueOfCode(Integer code) {
        for (BetStatusEnum item : BetStatusEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static BetStatusEnum valueOfSingleWalletBetStatus(SingleWalletBetStatusEnum singleWalletBetStatus) {
        switch (singleWalletBetStatus) {
            case RECEIVED_BET_REQUEST:
            case COMPLETED_BET_REQUEST:
            case RECEIVED_PAYOUT_REQUEST:
                return BetStatusEnum.ING;
            case COMPLETED_PAYOUT_REQUEST:
                return BetStatusEnum.SUCCESS;
            case DATA_NOT_FOUND:
                return BetStatusEnum.FAIL;
            default:
                throw new BusinessException(ErrorCode.ERR_PARAM, "invalid bet status code");
        }
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

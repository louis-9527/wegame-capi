package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

import static com.wegame.capi.common.Constants.*;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BetPromotionTypeEnum {

    NONE(0, "无"),
    OPERATION_EVENT(1, "干预事件"),
    STAR_CARD(2, "星卡"),
    FREE_SPIN(3, "预定投注"),
    STAR_CARD_IN_FREE(21, "免费星卡")

    ;

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
    BetPromotionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BetPromotionTypeEnum valueOfCode(Integer code) {
        for (BetPromotionTypeEnum item : BetPromotionTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public String getDefaultRewardControlSubKey() {
        switch (this) {
            case NONE:
                return Reward_Config_subkey_DEFAULT;
            case FREE_SPIN:
                return Reward_Config_subkey_FreeSpin;
            case STAR_CARD:
            case STAR_CARD_IN_FREE:
                return Reward_Config_subkey_StarCard;
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}

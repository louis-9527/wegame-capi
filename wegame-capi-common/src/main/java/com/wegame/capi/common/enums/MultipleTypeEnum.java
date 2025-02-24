package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 倍率类型枚举
 *
 * @author apple
 */
@Getter
@AllArgsConstructor
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MultipleTypeEnum {

    NORMAL("NORMAL", "正常倍率"),
    EX("EX", "ex倍率"),
    ;

    /**
     * 元素名称.
     */
    private final String code;

    /**
     * 元素名称.
     */
    private final String desc;

    public static MultipleTypeEnum valueOfCode(String code) {
        for (MultipleTypeEnum item : MultipleTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static MultipleTypeEnum fromBetSubTypeEnum(BetSubTypeEnum betSubTypeEnum) {
        AssertUtil.assertNotNull(betSubTypeEnum, ErrorCode.ERR_INTER, "no support MultipleTypeEnum");
        switch (betSubTypeEnum) {
            case NORMAL_BET:
                return NORMAL;
            case EX_BET:
                return EX;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "no support MultipleTypeEnum");
        }
    }

}

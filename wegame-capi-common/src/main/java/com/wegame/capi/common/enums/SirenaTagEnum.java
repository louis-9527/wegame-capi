package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SirenaTagEnum {
    SIRENA_FIRST_ROUND("SIRENA_FIRST_ROUND", "普通投注第一局"),
    SIRENA_NEXT_ROUND("SIRENA_NEXT_ROUND", "普通投注第二局"),
    SIRENA_BUY_BET("SIRENA_BUY_BET", "购买投注"),
    ;

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述信息.
     */
    private final String desc;
}

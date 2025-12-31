package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MangkukulamTagEnum {
    NORMAL_BET_INITIAL_STAGE("NORMAL_BET_INITIAL_STAGE", "普通投注的初始阶段"),
    BUY_BET_INITIAL_STAGE("BUY_BET_INITIAL_STAGE", "购买投注的初始阶段"),
    FREE_GAME_STAGE("FREE_GAME_STAGE", "免费轮阶段"),
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

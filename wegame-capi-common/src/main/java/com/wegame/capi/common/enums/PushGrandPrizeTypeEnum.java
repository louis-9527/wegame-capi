package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 推送的中大奖类型枚举
 *
 * @author apple
 */
@ToString
@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PushGrandPrizeTypeEnum {

    NORMAL_FREE_SPINS,
    ;
}

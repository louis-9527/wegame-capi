package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 推送的中大奖类型枚举
 *
 * @author apple
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum NotifyRewardTypeEnum {

    NORMAL_FREE_SPINS,
    MULTIPLE,
    ;


}

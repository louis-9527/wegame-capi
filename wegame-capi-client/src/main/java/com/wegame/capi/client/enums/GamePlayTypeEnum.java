package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 游戏Play类型枚举
 *
 * @author apple
 */
@AllArgsConstructor
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GamePlayTypeEnum {

    NORMAL_PLAY("NORMAL_PLAY", "NORMAL_PLAY"),
    EVENT_PLAY("EVENT_PLAY", "EVENT_PLAY"),
    TEMPLATE_PLAY("TEMPLATE_PLAY", "TEMPLATE_PLAY"),

    ;

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述信息.
     */
    private final String desc;

    public static GamePlayTypeEnum valueOfCode(String code) {
        for (GamePlayTypeEnum item : GamePlayTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }
}

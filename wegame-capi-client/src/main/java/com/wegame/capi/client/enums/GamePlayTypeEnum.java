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
    START_CARD_TEMPLATE_PLAY("START_CARD_TEMPLATE_PLAY", "START_CARD_TEMPLATE_PLAY"),
    FREE_SPIN_TEMPLATE_PLAY("FREE_SPIN_TEMPLATE_PLAY", "FREE_SPIN_TEMPLATE_PLAY"),
    INVENTORY_TEMPLATE_PLAY("INVENTORY_TEMPLATE_PLAY", "INVENTORY_TEMPLATE_PLAY"),

    ;

    /**
     * 枚举码.
     */
    private final String id;
    /**
     * 描述信息.
     */
    private final String desc;

    public static GamePlayTypeEnum valueOfId(String id) {
        for (GamePlayTypeEnum item : GamePlayTypeEnum.values()) {
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }
}

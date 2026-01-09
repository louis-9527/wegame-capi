package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 业务事件enum
 */
@ToString
@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BizEventEnum {
    EVENT_NONE(0, "EVENT_NONE", "无"),
    EVENT_SCATTER(1, "EVENT_SCATTER", "scatter替换"),
    EVENT_WILD(2, "EVENT_WILD", "wild替换"),
    EVENT_BOM(3, "EVENT_BOM", "爆炸"),
    EVENT_TRIDENT(4, "EVENT_TRIDENT", "三叉戟"),
    EVENT_GOLDEN_TANK(5, "EVENT_GOLDEN_TANK", "黄金战车"),
    EVENT_GOLDEN_COW(6, "EVENT_GOLDEN_COW", "黄金奶牛"),
    ;
    /**
     * 枚举码.
     */
    private final Integer type;

    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;

    public static BizEventEnum valueOfCode(String code) {
        for (BizEventEnum item : BizEventEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static BizEventEnum valueOfType(Integer type) {
        for (BizEventEnum item : BizEventEnum.values()) {
            if (Objects.equals(item.getType(), type)) {
                return item;
            }
        }
        return null;
    }
}

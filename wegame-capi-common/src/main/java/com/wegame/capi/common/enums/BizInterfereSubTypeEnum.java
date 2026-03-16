package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 业务干预子类型
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BizInterfereSubTypeEnum {

    NONE(0, null, "无"),
    MULTIPLE_CONTROL_11(11, BizInterfereTypeEnum.MULTIPLE_CONTROL, "倍率控制->奖池1"),
    MULTIPLE_CONTROL_12(12, BizInterfereTypeEnum.MULTIPLE_CONTROL, "倍率控制->奖池2"),
    MULTIPLE_CONTROL_13(13, BizInterfereTypeEnum.MULTIPLE_CONTROL, "倍率控制->奖池3"),
    MULTIPLE_CONTROL_14(14, BizInterfereTypeEnum.MULTIPLE_CONTROL, "倍率控制->奖池4"),
    // 冗余
    MULTIPLE_CONTROL_15(15, BizInterfereTypeEnum.MULTIPLE_CONTROL, "倍率控制->奖池5"),
    MULTIPLE_CONTROL_16(16, BizInterfereTypeEnum.MULTIPLE_CONTROL, "倍率控制->奖池6"),

    ;

    /**
     * 枚举码.
     */
    private final Integer code;

    /**
     * 对应业务干预类型
     *
     * @see BizInterfereTypeEnum
     */
    private final BizInterfereTypeEnum bizInterfereType;

    /**
     * 描述信息.
     */
    private final String desc;

    public static BizInterfereSubTypeEnum valueOfCode(Integer code) {
        for (BizInterfereSubTypeEnum item : BizInterfereSubTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }


}

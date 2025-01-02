package com.wegame.capi.common.enums;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 中奖类型
 *
 * @author apple
 */
@ToString
@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RewardTypeEnum {
    SINGLE(1, "单奖励"),
    MULTIPLE(2, "多奖励"),
    ;

    /**
     * 枚举码.
     */
    private final Integer code;

    /**
     * 描述信息.
     */
    private final String desc;

    public static RewardTypeEnum valueOfCode(Integer code) {
        for (RewardTypeEnum item : RewardTypeEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}

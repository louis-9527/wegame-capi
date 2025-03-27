package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 中奖类型
 *
 * @author apple
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RewardTypeEnum {
    NONE("NONE", "未中奖"),
    SINGLE("SINGLE", "单奖励"),
    MULTIPLE("MULTIPLE", "多奖励"),
    ;

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述信息.
     */
    private final String desc;

    RewardTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RewardTypeEnum valueOfCode(String code) {
        for (RewardTypeEnum item : RewardTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static RewardTypeEnum valueOfRewardLineCont(int rewardLineCount) {
        if (Objects.equals(rewardLineCount, 1)) {
            return SINGLE;
        } else if (rewardLineCount > 1) {
            return MULTIPLE;
        } else {
            return NONE;
        }
    }
}

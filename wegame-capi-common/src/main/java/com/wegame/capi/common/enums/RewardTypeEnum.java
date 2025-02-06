package com.wegame.capi.common.enums;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.common.deserializer.EnumDeserializerInterface;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RewardTypeEnum implements EnumDeserializerInterface<RewardTypeEnum> {
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

    @Override
    public String jsonValue() {
        return code;
    }

    @Override
    public RewardTypeEnum jsonCreator(String jsonValue) {
        return valueOfCode(jsonValue);
    }
}

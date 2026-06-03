package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 干预链id 类型
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum InterventionChainIdTypeEnum {
    PRE_INTERVENTION("preIntervention", "%_preIntervention", "前置干预", 1),
    POST_INTERVENTION("postIntervention", "%_postIntervention", "后置干预", 2),
    ;

    private final String id;
    private final String format;
    private final String desc;
    private final int defaultSort;

    public String formatChainId(GameTypeEnum gameType) {
        return String.format(format, gameType.getId());
    }

    public static InterventionChainIdTypeEnum valueOfConfigId(String configId) {
        String[] split = configId.split(Constants.COMMON_CONFIG_SPLIT);
        return valueOfChainId(split[1]);
    }

    public static InterventionChainIdTypeEnum valueOfChainId(String chainId) {
        for (InterventionChainIdTypeEnum value : InterventionChainIdTypeEnum.values()) {
            if (Objects.equals(chainId, value.getId())) {
                return value;
            }
        }
        return null;
    }


}

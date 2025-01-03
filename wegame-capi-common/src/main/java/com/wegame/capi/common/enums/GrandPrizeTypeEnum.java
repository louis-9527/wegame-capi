package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 中大奖类型枚举
 *
 * @author apple
 */
@ToString
@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GrandPrizeTypeEnum {

    NORMAL_FREE_SPINS("1","0"){


    },
    ;

    /**
     * 枚举码.
     */
    private final String id;

    /**
     * 描述信息.
     */
    private final String name;


    public static GrandPrizeTypeEnum valueOfCode(String id) {
        for (GrandPrizeTypeEnum item : GrandPrizeTypeEnum.values()) {
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }
}

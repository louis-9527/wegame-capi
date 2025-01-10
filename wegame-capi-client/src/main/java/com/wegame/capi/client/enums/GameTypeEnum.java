package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 游戏类型枚举
 *
 * @author apple
 */
@AllArgsConstructor
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameTypeEnum {

    MARIA_MAKILING("zhumeng001", "MariaMakiling", "maria_makiling_bet_record");

    /**
     * 枚举码.
     */
    private final String id;

    /**
     * 描述信息.
     */
    private final String name;

    /**
     * mongo 集合名称.
     */
    private final String collectionName;


    public static GameTypeEnum valueOfId(String id) {
        for (GameTypeEnum item : GameTypeEnum.values()) {
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }
}

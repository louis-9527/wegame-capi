package com.wegame.capi.client.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 游戏类型枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameTypeEnum {

    MARIA_MAKILING("zhumeng001", "MariaMakiling");;

    /**
     * 枚举码.
     */
    private final String id;

    /**
     * 描述信息.
     */
    private final String name;

    /**
     * 私有构造方法.
     *
     * @param id id
     * @param name 名称
     */
    private GameTypeEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static GameTypeEnum valueOfCode(String id) {
        for (GameTypeEnum item : GameTypeEnum.values()) {
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

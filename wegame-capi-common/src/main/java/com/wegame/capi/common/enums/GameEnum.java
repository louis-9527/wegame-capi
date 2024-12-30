package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

import java.util.Objects;

/**
 * 游戏枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameEnum {

    MYSTERIOUS(10000L, "mysterious");;

    /**
     * 枚举码.
     */
    private final Long id;

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
    private GameEnum(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static GameEnum valueOfCode(Long id) {
        for (GameEnum item : GameEnum.values()) {
            if (Objects.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

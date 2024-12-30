package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

import java.util.Objects;

/**
 * 元素类型枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SymbolTypeEnum {

    Symbol1001(1001, "Symbol1001"),
    Symbol1002(1002, "Symbol1001"),
    Symbol1003(1003, "Symbol1001"),
    Symbol1004(1004, "Symbol1001"),
    Symbol1005(1005, "Symbol1001"),

    Symbol2001(2001, "Symbol2001"),
    Symbol2002(2002, "Symbol2002"),
    Symbol2003(2002, "Symbol2003"),
    Symbol2004(2004, "Symbol2004"),
    Symbol2005(2005, "Symbol2005"),

    Scatter(3001, "Scatter"),

    Wild(4001, "Wild"),
    WildX2(4002, "WildX2"),
    WildX3(4003, "WildX3"),
    WildX5(4004, "WildX5"),
    WildX10(4005, "WildX10"),

    ;

    /**
     * 元素id.
     */
    private final Integer id;

    /**
     * 元素名称.
     */
    private final String name;

    /**
     * 私有构造方法.
     *
     * @param id   id
     * @param name 名称
     */
    private SymbolTypeEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SymbolTypeEnum valueOfId(Long id) {
        for (SymbolTypeEnum item : SymbolTypeEnum.values()) {
            if (Objects.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

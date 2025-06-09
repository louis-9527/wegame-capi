package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 元素类型枚举
 *
 * @author apple
 */
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SymbolEnum {

    /**
     * 占位元素
     */
    Symbol1000("1000", "Symbol1000"),

    Symbol1001("1001", "Symbol1001"),
    Symbol1002("1002", "Symbol1002"),
    Symbol1003("1003", "Symbol1003"),
    Symbol1004("1004", "Symbol1004"),
    Symbol1005("1005", "Symbol1005"),
    Symbol1006("1006", "Symbol1006"),
    Symbol1007("1007", "Symbol1007"),
    Symbol1008("1008", "Symbol1008"),
    Symbol1009("1009", "Symbol1009"),
    Symbol1010("1010", "Symbol1010"),

    Symbol2001("2001", "Symbol2001"),
    Symbol2002("2002", "Symbol2002"),
    Symbol2003("2003", "Symbol2003"),
    Symbol2004("2004", "Symbol2004"),
    Symbol2005("2005", "Symbol2005"),

    Scatter("3001", "Scatter"),
    Scatter1("3002", "Scatter+1"),

    Wild("4001", "Wild"),
    WildX2("4002", "WildX2"),
    WildX3("4003", "WildX3"),
    WildX5("4004", "WildX5"),
    WildX10("4005", "WildX10"),

    BigWild("4006", "BigWild"),
    SuperWild("4007", "SuperWild"),
    ;

    /**
     * 元素id.
     */
    private final String id;
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
    SymbolEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SymbolEnum valueOfId(String id) {
        for (SymbolEnum item : SymbolEnum.values()) {
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

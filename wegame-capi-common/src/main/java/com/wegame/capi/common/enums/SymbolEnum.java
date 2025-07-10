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

    Symbol0("0", "Symbol0"),
    Symbol1("1", "Symbol1"),
    Symbol2("2", "Symbol2"),
    Symbol3("3", "Symbol3"),
    Symbol5("5", "Symbol5"),
    Symbol10("10", "Symbol10"),
    SymbolNull("null", "SymbolNull"),

    BigWild("4006", "BigWild"),
    SuperWild("4007", "SuperWild"),
    Wild_Sword("4010", "Wild_Sword"),
    Wild_Stick("4011", "Wild_stick"),

    // 动态wild
    DynamicWild("4020", "DynamicWild"),
    SpecialWild("4021", "SpecialWild"),

    // mangkukulam symbol
    SymbolL1("L1", "SymbolL1"),
    SymbolL2("L2", "SymbolL2"),
    SymbolL3("L3", "SymbolL3"),
    SymbolL4("L4", "SymbolL4"),
    SymbolL5("L5", "SymbolL5"),

    SymbolH1("H1", "SymbolH1"),
    SymbolH2("H2", "SymbolH2"),
    SymbolH3("H3", "SymbolH3"),
    SymbolH4("H4", "SymbolH4"),

    SymbolWD("WD", "SymbolWD"),
    SymbolWD_E("WD_E", "SymbolWD_E"),

    SymbolSC("SC", "SymbolSC"),

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

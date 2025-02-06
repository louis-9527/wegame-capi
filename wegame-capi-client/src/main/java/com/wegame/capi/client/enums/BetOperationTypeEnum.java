package com.wegame.capi.client.enums;

public enum BetOperationTypeEnum {

    ADD("ADD", "新增"),
    UPDATE("UPDATE", "修改"),

    ;

    private BetOperationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;


    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

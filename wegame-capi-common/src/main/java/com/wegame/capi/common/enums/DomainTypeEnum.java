package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DomainTypeEnum {

    SINGLE_DOMAIN("singleDomain", "进行中"),
    FREE_SPIN_DOMAIN("freeSpinDomain", "freeSpinDomain"),
    PROMOTION_DOMAIN("promotionDomain", "promotionDomain"),
    OPERATION_DOMAIN("operationDomain", "operationDomain");

    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;
    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    DomainTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DomainTypeEnum valueOfCode(Integer code) {
        for (DomainTypeEnum item : DomainTypeEnum.values()) {
            if (Objects.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

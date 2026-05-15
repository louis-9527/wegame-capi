package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 模板索引类型枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TemplateIndexTypeEnum {

    StarCard("StarCard", "星卡"),
    FreeSpin("FreeSpin", "FreeSpin"),
    Default("Default", "Default"),
    ;

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    public static TemplateIndexTypeEnum valueOfCode(String code) {
        for (TemplateIndexTypeEnum item : TemplateIndexTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static TemplateIndexTypeEnum getByBetPromotionType(BetPromotionTypeEnum betPromotionType) {
        switch (betPromotionType) {
            case STAR_CARD:
                return StarCard;
            case FREE_SPIN:
                return FreeSpin;
            case NONE:
                return Default;
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}

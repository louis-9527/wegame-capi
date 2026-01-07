package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 中奖线类型
 *
 * @author apple
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RewardLineTypeEnum {

    THREE(3,"THREE", "三连", OddsTypeEnum.THREE),
    FOUR(4,"FOUR", "四连", OddsTypeEnum.FOUR),
    FIVE(5,"FIVE", "五连", OddsTypeEnum.FIVE),
    SIX(6,"SIX", "六连", OddsTypeEnum.SIX),
    SEVEN(7,"SEVEN", "七连", OddsTypeEnum.SEVEN),
    EIGHT(8,"EIGHT", "八连", OddsTypeEnum.EIGHT),
    NINE(9,"NINE", "九连", OddsTypeEnum.NINE),
    TEN(10,"TEN", "十连", OddsTypeEnum.TEN),
    ELEVEN(11,"ELEVEN", "十一连", OddsTypeEnum.ELEVEN),
    TWELVE(12,"TWELVE", "十二连", OddsTypeEnum.TWELVE),
    THIRTEEN(13,"THIRTEEN", "十三连", OddsTypeEnum.THIRTEEN),
    FOURTEEN(14,"FOURTEEN", "十四连", OddsTypeEnum.FOURTEEN),
    FIFTEEN(15,"FIFTEEN", "十五连", OddsTypeEnum.FIFTEEN),
    SIXTEEN(16,"SIXTEEN", "十六连", OddsTypeEnum.SIXTEEN),
    SEVENTEEN(17,"SEVENTEEN", "十七连", OddsTypeEnum.SEVENTEEN),
    EIGHTEEN(18,"EIGHTEEN", "十八连", OddsTypeEnum.EIGHTEEN),
    NINETEEN(19,"NINETEEN", "十九连", OddsTypeEnum.NINETEEN),
    TWENTY(20,"TWENTY", "二十连", OddsTypeEnum.TWENTY),
    TWENTY_ONE(21,"TWENTY_ONE", "二十一连", OddsTypeEnum.TWENTY_ONE),
    TWENTY_TWO(22,"TWENTY_TWO", "二十二连", OddsTypeEnum.TWENTY_TWO),
    TWENTY_THREE(23,"TWENTY_THREE", "二十三连", OddsTypeEnum.TWENTY_THREE),
    TWENTY_FOUR(24,"TWENTY_FOUR", "二十四连", OddsTypeEnum.TWENTY_FOUR),
    TWENTY_FIVE(25,"TWENTY_FIVE", "二十五连", OddsTypeEnum.TWENTY_FIVE),
    TWENTY_SIX(26,"TWENTY_SIX", "二十六连", OddsTypeEnum.TWENTY_SIX),
    TWENTY_SEVEN(27,"TWENTY_SEVEN", "二十七连", OddsTypeEnum.TWENTY_SEVEN),
    TWENTY_EIGHT(28,"TWENTY_EIGHT", "二十八连", OddsTypeEnum.TWENTY_EIGHT),
    TWENTY_NINE(29,"TWENTY_NINE", "二十九连", OddsTypeEnum.TWENTY_NINE),
    THIRTY(30,"THIRTY", "三十连", OddsTypeEnum.THIRTY),

    ;

    private int no;
    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述信息.
     */
    private final String desc;

    private OddsTypeEnum oddsType;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    RewardLineTypeEnum(Integer no, String code, String desc, OddsTypeEnum oddsType) {
        this.no = no;
        this.code = code;
        this.desc = desc;
        this.oddsType = oddsType;
    }

    public static RewardLineTypeEnum valueOfCode(String code) {
        for (RewardLineTypeEnum item : RewardLineTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    public static RewardLineTypeEnum valueOfNo(Integer no) {
        for (RewardLineTypeEnum item : RewardLineTypeEnum.values()) {
            if (Objects.equals(item.getNo(), no)) {
                return item;
            }
        }
        return null;
    }

    public Integer getNo() {
        return no;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public OddsTypeEnum getOddsType() {
        return oddsType;
    }
}

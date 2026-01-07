package com.wegame.capi.common.enums;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * 赔率类型
 *
 * @author apple
 */
@ToString
@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OddsTypeEnum {

    THREE("THREE", "三连赔率", "3"),
    FOUR("FOUR", "四连赔率", "4"),
    FIVE("FIVE", "五连赔率", "5"),
    SIX("SIX", "六连赔率", "6"),
    SEVEN("SEVEN", "七连赔率", "7"),
    EIGHT("EIGHT", "八连赔率", "8"),
    NINE("NINE", "九连赔率", "9"),
    TEN("TEN", "十连赔率", "10"),
    ELEVEN("ELEVEN", "十一连赔率", "11"),
    TWELVE("TWELVE", "十二连赔率", "12"),
    THIRTEEN("THIRTEEN", "十三连赔率", "13"),
    FOURTEEN("FOURTEEN", "十四连赔率", "14"),
    FIFTEEN("FIFTEEN", "十五连赔率", "15"),
    SIXTEEN("SIXTEEN", "十六连赔率", "16"),
    SEVENTEEN("SEVENTEEN", "十七连赔率", "17"),
    EIGHTEEN("EIGHTEEN", "十八连赔率", "18"),
    NINETEEN("NINETEEN", "十九连赔率", "19"),
    TWENTY("TWENTY", "二十连赔率", "20"),
    TWENTY_ONE("TWENTY_ONE", "二十一连赔率", "21"),
    TWENTY_TWO("TWENTY_TWO", "二十二连赔率", "22"),
    TWENTY_THREE("TWENTY_THREE", "二十三连赔率", "23"),
    TWENTY_FOUR("TWENTY_FOUR", "二十四连赔率", "24"),
    TWENTY_FIVE("TWENTY_FIVE", "二十五连赔率", "25"),
    TWENTY_SIX("TWENTY_SIX", "二十六连赔率", "26"),
    TWENTY_SEVEN("TWENTY_SEVEN", "二十七连赔率", "27"),
    TWENTY_EIGHT("TWENTY_EIGHT", "二十八连赔率", "28"),
    TWENTY_NINE("TWENTY_NINE", "二十九连赔率", "29"),
    THIRTY("THIRTY", "三十连赔率", "30"),

    ;

    /**
     * 枚举码.
     */
    private final String code;
    /**
     * 描述信息.
     */
    private final String desc;

    /**
     * 描述信息.
     */
    private final String no;

    /**
     * 私有构造方法.
     *
     * @param code code值
     * @param desc 描述信息
     */
    OddsTypeEnum(String code, String desc, String no) {
        this.code = code;
        this.desc = desc;
        this.no = no;
    }

    public static OddsTypeEnum valueOfCode(String code) {
        for (OddsTypeEnum item : OddsTypeEnum.values()) {
            if (item.getCode().equals(code)) {
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

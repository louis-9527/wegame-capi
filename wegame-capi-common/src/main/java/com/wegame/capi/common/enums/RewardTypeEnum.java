package com.wegame.capi.common.enums;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 中奖类型
 *
 * @author apple
 */
@ToString
@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RewardTypeEnum {
    UN_KNOWN("UN_KNOWN", "未中奖"),
    SINGLE("SINGLE", "单奖励"),
    MULTIPLE("MULTIPLE", "多奖励"),
    ;

    /**
     * 枚举码.
     */
    private final String code;

    /**
     * 描述信息.
     */
    private final String desc;

    public static RewardTypeEnum valueOfCode(String code) {
        for (RewardTypeEnum item : RewardTypeEnum.values()) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

}

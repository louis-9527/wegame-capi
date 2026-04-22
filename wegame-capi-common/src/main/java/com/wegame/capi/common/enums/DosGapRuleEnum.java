package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * DOS 渠道：按动态事件滚轮命中的 {@code configMultiple} 决定下一次命中前的间隔局数随机范围（闭区间）。
 * <p>
 * 档位从高到低匹配。
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DosGapRuleEnum {

    /** configMultiple ≥ 100：间隔局数 [20, 50] 随机 */
    MATCHED_MULTIPLE_AT_LEAST_100(100.0, null, 20, 50),

    /** configMultiple ∈ [5, 100)：间隔局数 [20, 30] 随机 */
    MATCHED_MULTIPLE_FROM_5_TO_BELOW_100(5.0, 100.0, 20, 30);

    /** 该档 configMultiple 下界（含） */
    private final double tierMinConfigMultiple;
    /** 该档 configMultiple 上界（不含）；{@code null} 表示无上限 */
    private final Double tierMaxConfigMultipleExclusive;
    /** 随机间隔局数下限（含） */
    private final int randomGapRoundsMinInclusive;
    /** 随机间隔局数上限（含） */
    private final int randomGapRoundsMaxInclusive;

    DosGapRuleEnum(double tierMinConfigMultiple, Double tierMaxConfigMultipleExclusive,
                   int randomGapRoundsMinInclusive, int randomGapRoundsMaxInclusive) {
        this.tierMinConfigMultiple = tierMinConfigMultiple;
        this.tierMaxConfigMultipleExclusive = tierMaxConfigMultipleExclusive;
        this.randomGapRoundsMinInclusive = randomGapRoundsMinInclusive;
        this.randomGapRoundsMaxInclusive = randomGapRoundsMaxInclusive;
    }

    /**
     * @param configMultiple 滚轮命中的配置倍数（与 GameOperationEventDo.configMultiple 同源）
     * @return 命中的规则；倍数 &lt; 5 等未配置档位返回 {@code null}
     */
    public static DosGapRuleEnum forConfigMultiple(double configMultiple) {
        for (DosGapRuleEnum rule : values()) {
            if (configMultiple < rule.tierMinConfigMultiple) {
                continue;
            }
            if (rule.tierMaxConfigMultipleExclusive != null
                    && configMultiple >= rule.tierMaxConfigMultipleExclusive) {
                continue;
            }
            return rule;
        }
        return null;
    }
}

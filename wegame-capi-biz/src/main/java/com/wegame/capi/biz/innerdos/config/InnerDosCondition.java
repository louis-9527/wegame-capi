package com.wegame.capi.biz.innerdos.config;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 内部干预条件
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InnerDosCondition {
    /**
     * 新人配置
     */
    private NormalCondition newPlayerCondition;

    /**
     * 常态配置
     */
    private NormalCondition normalCondition;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class NormalCondition {
        /**
         * 游戏局数目条件
         * 针对新人：则为最大局数
         * 针对常态化：则为最小局数
         */
        private Integer roundsCountLimit;
        /**
         * 空盘数限制
         */
        private Integer emptyDiskLimit;
        /**
         * 干预系数
         */
        private BigDecimal dosCoefficient;
    }





}

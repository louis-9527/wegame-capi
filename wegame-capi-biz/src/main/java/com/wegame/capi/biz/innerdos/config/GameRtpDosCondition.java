package com.wegame.capi.biz.innerdos.config;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 大盘干预rtp条件
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRtpDosCondition implements Serializable {
    private static final long serialVersionUID = 987264626434L;
    /**
     * 进入条件
     */
    private Condition in;
    /**
     * 退出条件
     */
    private Condition out;
    /**
     * 条件
     */
    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Condition {
        /**
         * 条件值
         */
        private BigDecimal conditionValue;
        /**
         * 条件计算公式
         */
        private Formula formula;

        /**
         * 判断条件满足性
         *
         * @param byValue byValue
         * @return true/false
         */
        public boolean assertIt(BigDecimal byValue) {
            return formula.compare(this.conditionValue, byValue);
        }

    }

    /**
     * 条件计算公式
     */
    @Getter
    @NoArgsConstructor
    public enum Formula {
        THAN {
            @Override
            public boolean compare(BigDecimal conditionValue, BigDecimal byValue) {
                return byValue.compareTo(conditionValue) > 0;
            }
        },
        THAN_AND_EQUAL {
            @Override
            public boolean compare(BigDecimal conditionValue, BigDecimal byValue) {
                return byValue.compareTo(conditionValue) >= 0;
            }
        },
        LESS {
            @Override
            public boolean compare(BigDecimal conditionValue, BigDecimal byValue) {
                return byValue.compareTo(conditionValue) < 0;
            }
        },
        LESS_AND_EQUAL {
            @Override
            public boolean compare(BigDecimal conditionValue, BigDecimal byValue) {
                return byValue.compareTo(conditionValue) <= 0;
            }
        },
        EQUAL {
            @Override
            public boolean compare(BigDecimal conditionValue, BigDecimal byValue) {
                return byValue.compareTo(conditionValue) == 0;
            }
        };

        public abstract boolean compare(BigDecimal conditionValue, BigDecimal byValue);
    }
}

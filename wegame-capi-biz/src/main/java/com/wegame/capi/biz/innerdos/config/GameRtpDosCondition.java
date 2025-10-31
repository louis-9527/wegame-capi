package com.wegame.capi.biz.innerdos.config;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
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
        public static final String CONDITION_SPLIT ="_";
        /**
         * 条件值
         */
        private String conditionValue;
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
            AssertUtil.assertStrNotEmpty(this.conditionValue, ErrorCode.ERR_PARAM, "conditionValue is empty");
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
            public boolean compare(String conditionValue, BigDecimal byValue) {
                return byValue.compareTo(new BigDecimal(conditionValue)) > 0;
            }
        },
        THAN_AND_EQUAL {
            @Override
            public boolean compare(String conditionValue, BigDecimal byValue) {
                return byValue.compareTo(new BigDecimal(conditionValue)) >= 0;
            }
        },
        LESS {
            @Override
            public boolean compare(String conditionValue, BigDecimal byValue) {
                return byValue.compareTo(new BigDecimal(conditionValue)) < 0;
            }
        },
        LESS_AND_EQUAL {
            @Override
            public boolean compare(String conditionValue, BigDecimal byValue) {
                return byValue.compareTo(new BigDecimal(conditionValue)) <= 0;
            }
        },
        EQUAL {
            @Override
            public boolean compare(String conditionValue, BigDecimal byValue) {
                return byValue.compareTo(new BigDecimal(conditionValue)) == 0;
            }
        },
        BETWEEN {
            @Override
            public boolean compare(String conditionValue, BigDecimal byValue) {
                String[] split = conditionValue.split(Condition.CONDITION_SPLIT);
                AssertUtil.assertTrue(split.length == 2, ErrorCode.ERR_PARAM, "conditionValue is not between 2");
                BigDecimal lowLimit=new BigDecimal(split[0]);
                BigDecimal upLimit=new BigDecimal(split[1]);
                return byValue.compareTo(lowLimit) >= 0 && byValue.compareTo(upLimit) <= 0;
            }
        },
        NOT_BETWEEN {
            @Override
            public boolean compare(String conditionValue, BigDecimal byValue) {
                String[] split = conditionValue.split(Condition.CONDITION_SPLIT);
                AssertUtil.assertTrue(split.length == 2, ErrorCode.ERR_PARAM, "conditionValue is not between 2");
                BigDecimal lowLimit=new BigDecimal(split[0]);
                BigDecimal upLimit=new BigDecimal(split[1]);
                return byValue.compareTo(lowLimit) < 0 && byValue.compareTo(upLimit) > 0;
            }
        };

        public abstract boolean compare(String conditionValue, BigDecimal byValue);
    }
}

package com.wegame.capi.client.enums.innerdos;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 用户指标采集属性名称枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserPlayMetricsPropertyNameEnum {
    totalGameCount("totalGameCount", MetricsPropertyTypeEnum.FIXED, "累计游戏局数") {
        @Override
        public <T> T calc(T target, Object source, Class<T> targetClass) {
            if (Objects.isNull(source)) {
                return target;
            }
            if (!NumberUtil.isLong(source.toString())) {
                throw new BusinessException(ErrorCode.ERR_PARAM, "property totalGameCount must be type Long");
            }

            if (Objects.nonNull(target)) {
                return targetClass.cast((long) target + (long) source);
            } else {
                return targetClass.cast(source);
            }
        }
    },
    totalBetAmount("totalBetAmount", MetricsPropertyTypeEnum.FIXED, "累计投注金额") {
        @Override
        public <T> T calc(T target, Object source, Class<T> targetClass) {
            if (Objects.isNull(source)) {
                return target;
            }
            if (!NumberUtil.isLong(source.toString())) {
                throw new BusinessException(ErrorCode.ERR_PARAM, "property totalBetAmount must be type Long");
            }
            if (Objects.nonNull(target)) {
                return targetClass.cast((long) target + (long) source);
            } else {
                return targetClass.cast(source);
            }
        }
    },
    totalPayoutAmount("totalPayoutAmount", MetricsPropertyTypeEnum.FIXED, "累计获奖金额") {
        @Override
        public <T> T calc(T target, Object source, Class<T> targetClass) {
            if (Objects.isNull(source)) {
                return target;
            }
            if (!NumberUtil.isLong(source.toString())) {
                throw new BusinessException(ErrorCode.ERR_PARAM, "property totalPayoutAmount must be type Long");
            }
            if (Objects.nonNull(target)) {
                return targetClass.cast((long) target + (long) source);
            } else {
                return targetClass.cast(source);
            }
        }
    },
    continuousLostCount("continuousLostCount", MetricsPropertyTypeEnum.FIXED, "连续空盘次数") {
        @Override
        public <T> T calc(T target, Object source, Class<T> targetClass) {
            if (Objects.isNull(source)) {
                return target;
            }
            if (!NumberUtil.isInteger(source.toString())) {
                throw new BusinessException(ErrorCode.ERR_PARAM, "property continuousLostCount must be type Long");
            }
            int longSource = (int) source;
            if (Objects.nonNull(target)) {
                // 当前增量空盘大于0,则增加
                if (longSource > 0) {
                    return targetClass.cast((int) target + longSource);
                } else {
                    // 当前增量空盘小于0,则重置为0
                    return targetClass.cast(0);
                }
            } else {
                return targetClass.cast(longSource);
            }
        }
    },
    lastBetTime("lastBetTime", MetricsPropertyTypeEnum.FIXED, "最后投注时间") {
        @Override
        public <T> T calc(T target, Object source, Class<T> targetClass) {
            if (Objects.isNull(source)) {
                return target;
            }
            if (!NumberUtil.isLong(source.toString())) {
                throw new BusinessException(ErrorCode.ERR_PARAM, "property lastBetTime must be type Long");
            }
            long longSource = (long) source;
            if (Objects.nonNull(target)) {
                return targetClass.cast(Math.max((long) target, longSource));
            } else {
                return targetClass.cast(longSource);
            }
        }
    },
    continuousNoFreeSpinCount("continuousNoFreeSpinCount", MetricsPropertyTypeEnum.FIXED, "连续不中免费轮次数") {
        @Override
        public <T> T calc(T target, Object source, Class<T> targetClass) {
            if (Objects.isNull(source)) {
                return target;
            }
            if (!NumberUtil.isLong(source.toString())) {
                throw new BusinessException(ErrorCode.ERR_PARAM, "property continuousNoFreeSpinCount must be type Long");
            }
            long longSource = (long) source;
            if (Objects.nonNull(target)) {
                // 当前增量大于0,则增加
                if (longSource > 0) {
                    return targetClass.cast((long) target + longSource);
                } else {
                    // 当前增量小于0,则重置为0
                    return targetClass.cast(0);
                }
            } else {
                return targetClass.cast(longSource);
            }
        }
    },
    // 机制列表：不做calc实现
    specialRuleList("specialRuleList", MetricsPropertyTypeEnum.FIXED, "机制列表"),
    extend_example("extend_example", MetricsPropertyTypeEnum.EXTEND, "扩展字段示例") {
        @Override
        public <T> T calc(T target, Object source, Class<T> targetClass) {
            Long longTarget = (Long) target;
            Long longSource = (Long) source;
            if (Objects.nonNull(longTarget) && Objects.nonNull(longSource)) {
                return targetClass.cast(longTarget + longSource);
            } else if (Objects.isNull(longTarget)) {
                return targetClass.cast(longSource);
            } else {
                return targetClass.cast(longTarget);
            }
        }
    },

    ;

    private final String propertyName;
    /**
     * 类型:fixed:固定属性。extend:扩展属性
     */
    private final MetricsPropertyTypeEnum type;
    private final String desc;

    /**
     * 注入计算
     *
     * @param source 来源数据(新增数据)
     * @param target 目标数据，原有数据
     * @param <T>    数据类型
     * @return 计算后的值
     */
    public <T> T calc(T target, Object source, Class<T> targetClass) {
        // 提供默认实现
        throw new BusinessException(ErrorCode.ERR_INTER, "can not calc by default method");
    }
}

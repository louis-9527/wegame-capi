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
 * 大盘指标采集属性名称枚举
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GameMetricsPropertyNameEnum {
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
                return targetClass.cast(Long.parseLong(target.toString()) + (long) source);
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
                return targetClass.cast(Long.parseLong(target.toString()) + (long) source);
            } else {
                return targetClass.cast(source);
            }
        }
    },
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

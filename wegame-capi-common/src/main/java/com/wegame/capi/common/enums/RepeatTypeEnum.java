package com.wegame.capi.common.enums;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

/**
 * 重复类型
 */
@Getter
@ToString
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RepeatTypeEnum {
    NEVER_REPEAT("never_repeat", "永不重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return false;
        }
    },
    TODAY_NOT_REPEAT("today_not_repeat", "今日不重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return !DateUtil.isSameDay(new Date(byTimeStamp), new Date(System.currentTimeMillis()));
        }
    },
    TODAY_REPEAT("today_repeat", "今日重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return DateUtil.isSameDay(new Date(byTimeStamp), new Date(System.currentTimeMillis()));
        }
    },
    WEEK_NOT_REPEAT("week_not_repeat", "本周不重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return !DateUtil.isSameWeek(new Date(byTimeStamp), new Date(System.currentTimeMillis()), true);
        }
    },
    WEEK_REPEAT("week_repeat", "本周重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return DateUtil.isSameWeek(new Date(byTimeStamp), new Date(System.currentTimeMillis()), true);
        }
    },
    MONTH_NOT_REPEAT("month_not_repeat", "本月不重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return !DateUtil.isSameMonth(new Date(byTimeStamp), new Date(System.currentTimeMillis()));
        }
    },
    MONTH_REPEAT("month_repeat", "本月重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return DateUtil.isSameMonth(new Date(byTimeStamp), new Date(System.currentTimeMillis()));
        }
    },
    ALWAYS_REPEAT("always_repeat", "始终重复") {
        @Override
        public boolean assertTime(long byTimeStamp, Map<String, Object> param) {
            return true;
        }
    },

    ;

    private final String code;
    private final String desc;

    /**
     * 是否满足
     *
     * @param byTimeStamp 被校验的时间戳
     * @param param       额外参数
     * @return true/false
     */
    public abstract boolean assertTime(long byTimeStamp, Map<String, Object> param);


    public static RepeatTypeEnum valueOfCode(String code) {
        for (RepeatTypeEnum value : RepeatTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}

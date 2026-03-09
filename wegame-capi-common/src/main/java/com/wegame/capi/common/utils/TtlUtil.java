package com.wegame.capi.common.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

/**
 * TTL 过期时间工具类
 * 用于计算 MongoDB TTL 索引所需的过期时间
 */
public class TtlUtil {

    /**
     * TTL 过期月数
     */
    public static final int TTL_MONTHS = 6;

    /**
     * 根据创建时间计算过期时间（createTime + 6个月）
     *
     * @param createTime 创建时间（毫秒时间戳）
     * @return 过期时间（Date类型，MongoDB TTL索引要求）
     */
    public static Date calculateExpiredAt(Long createTime) {
        if (createTime == null) {
            createTime = System.currentTimeMillis();
        }
        return Date.from(Instant.ofEpochMilli(createTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusMonths(TTL_MONTHS)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}

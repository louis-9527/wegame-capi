package com.wegame.capi.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 脱敏工具类
 */
@Slf4j
public class MaskUtil {

    /**
     * playerName脱敏
     *
     * @param playerName
     * @param showPreLength  前面显示位数
     * @param showPostLength 后显示位数
     * @return
     */
    public static String maskPlayerName(String playerName, int showPreLength, int showPostLength, int maxStarCount) {
        try {
            int totalShowLength = showPreLength + showPostLength;
            if (Objects.isNull(playerName) || playerName.length() <= totalShowLength) {
                return playerName;
            }
            int length = playerName.length();
            // 截取前缀和后缀
            String prefix = playerName.substring(0, showPreLength);
            String suffix = playerName.substring(length - showPostLength);
            // 生成中间的脱敏字符（星号）
            int starCount = length - showPreLength - showPostLength;
            int needStarCount = Math.min(starCount, maxStarCount);
            StringBuilder stars = new StringBuilder();
            stars.append(prefix);
            for (int i = 0; i < needStarCount; i++) {
                stars.append('*');
            }
            stars.append(suffix);
            // 拼接结果
            return stars.toString();
        } catch (Exception e) {
            log.error("maskPlayerName error", e);
            return playerName;
        }
    }
}

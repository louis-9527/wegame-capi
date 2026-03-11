package com.wegame.capi.common.utils;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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



    /**
     * 玩家信息(playerId、playerName)脱敏处理规则：
     * 1. 长度不足4位时，随机生成0-9数字补齐到4位；
     * 2. 补齐后（或原长度≥4）：前2位明文+6位星号+后2位明文
     * eg: 1234 -> 12****34
     */
    public static String desensitizePlayerInfo(String playerInfo) {
        String processedPlayerInfo = StringUtils.defaultString(playerInfo, "");
        int length = processedPlayerInfo.length();
        if (processedPlayerInfo.length() < 4) {
            // 计算需要补齐的位数
            int needFill = 4 - length;
            // 生成needFill位0-9的随机数字串
            String randomFill = RandomUtil.randomNumbers(needFill);
            // 拼接补齐（补在末尾，也可根据业务需求补在开头）
            processedPlayerInfo += randomFill;
        }

        String prefix = processedPlayerInfo.substring(0, 2);
        String suffix = processedPlayerInfo.substring(processedPlayerInfo.length() - 2);
        return prefix + "******" + suffix;
    }
}

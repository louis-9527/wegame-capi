package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MariaMakilingConfig implements Serializable {

    private static final long serialVersionUID = -7867808743045979621L;

    public static final String REEL_PREFIX = "reel";

    public static final String ROUND_PREFIX = "Round";

    private static final Integer MAX_REEL_SIZE = 5;

    private static final Integer MAX_FREE_SPIN_TIMES = 13;

    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;

    /**
     * 首轮游戏配置
     */
    private MainGame mainGame;

    /**
     * 免费游戏配置
     */
    private FreeGame freeGame;

    /**
     * 模板配置
     */
    private Map<String, TemplateMultipleRange> templateRangeConfigMap;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class OddsConfig implements Serializable {
        private static final long serialVersionUID = 3937946953651929522L;
        /**
         * TreeMap<元素ID，<赔率类型，具体赔率>>
         */
        private TreeMap<String, TreeMap<String, BigDecimal>> commonOddsConfig;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ReelConfig implements Serializable {
        private static final long serialVersionUID = -4203318920583123742L;
        /**
         * TreeMap<元素ID，weight>
         */
        private TreeMap<String, Long> symbolWeights;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class MainGame implements Serializable {
        private static final long serialVersionUID = 6797290129033623348L;
        /**
         * TreeMap<modeName，weight>
         */
        private TreeMap<String, Long> modeWeights;

        /**
         * TreeMap<modeName, <reelName,ReelConfig>>
         */
        private TreeMap<String, TreeMap<String, ReelConfig>> modeConfig;

        public ReelConfig getReelConfig(String modeName, int reelIndex) {
            return modeConfig.get(modeName).get(generateReelKey(reelIndex));
        }
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class FreeGame implements Serializable {
        private static final long serialVersionUID = 6151000233708276160L;
        /**
         * TreeMap<freeSpinTimes，weight>
         */
        private TreeMap<String, Long> fgNumbersWeights;

        /**
         * TreeMap<modeName，weight>
         */
        private TreeMap<String, Long> fgModeWeights;

        /**
         * TreeMap<modeName，<roundName,<reelName, reelConfig>>>
         */
        private TreeMap<String, TreeMap<String, TreeMap<String, ReelConfig>>> fgModeConfig;

        public ReelConfig getReelConfig(String modeName, int freeSpinIndex, int reelIndex) {
            return fgModeConfig.get(modeName).get(generateRoundKey(freeSpinIndex)).get(generateReelKey(reelIndex));
        }
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class TemplateMultipleRange implements Serializable {
        private static final long serialVersionUID = -734841249803450751L;
        /**
         * 倍数区间开始 todo 修改为Bigdeceimal
         */
        private String multipleRangeStart;
        /**
         * 倍数区间结束
         */
        private String multipleRangeEnd;
    }

    public void validate() {
        rewardMaxMultipleValidate();
        oddsConfigValidate();
        validateMainGame();
        validateFreeGame();
        templateRangeConfigMapValidate();
    }

    private void validateMainGame() {
        Assert.notNull(mainGame);
        // 1. 校验权重 : modeWeights
        Assert.notNull(mainGame.getModeWeights());
        validateWeightMap(mainGame.getModeWeights());
        // 2. 校验每个mode下的reel权重
        Assert.notNull(mainGame.getModeConfig());
        for (Map.Entry<String, Long> entry : mainGame.getModeWeights().entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }
            Map<String, ReelConfig> reelConfigMap = mainGame.getModeConfig().get(entry.getKey());
            Assert.notNull(reelConfigMap, "reelConfigMap is null, modeName is " + entry.getKey());
            validateReelConfigMap(reelConfigMap);
        }
    }

    private void validateFreeGame() {
        Assert.notNull(freeGame);
        // 1. 校验权重 : fgNumbersWeights
        Assert.notNull(freeGame.getFgNumbersWeights());
        validateWeightMap(freeGame.getFgNumbersWeights());
        // 2. 校验权重 : fgModeWeights
        Assert.notNull(freeGame.getFgModeWeights());
        validateWeightMap(freeGame.getFgModeWeights());
        // 3. 校验 fgModeConfig
        Assert.notNull(freeGame.getFgModeConfig());
        for (Map.Entry<String, Long> entry : freeGame.getFgModeWeights().entrySet()) {
            // 权重为0不去校验
            if (entry.getValue() == 0) {
                continue;
            }
            TreeMap<String, TreeMap<String, ReelConfig>> roundConfig = freeGame.getFgModeConfig().get(entry.getKey());
            Assert.notNull(roundConfig, "roundConfig is null, modeName is " + entry.getKey());
            validateRoundConfig(roundConfig);
        }
    }

    private void validateRoundConfig(TreeMap<String, TreeMap<String, ReelConfig>> roundConfigMap) {
        // 必须有13项 并且是Round1 - Round13
        Assert.notNull(roundConfigMap);
        Assert.isTrue(roundConfigMap.size() == MAX_FREE_SPIN_TIMES);
        for (int i = 0; i < MAX_FREE_SPIN_TIMES; i++) {
            String key = generateRoundKey(i);
            Map<String, ReelConfig> reelConfigMap = roundConfigMap.get(key);
            Assert.notNull(reelConfigMap, "roundConfigMap is null, roundName is " + key);
            validateReelConfigMap(reelConfigMap);
        }
    }

    private void validateReelConfigMap(Map<String, ReelConfig> reelConfigMap) {
        // 必须有5列 并且是reel1 - reel5
        Assert.notNull(reelConfigMap);
        Assert.isTrue(reelConfigMap.size() == MAX_REEL_SIZE);
        for (int i = 0; i < MAX_REEL_SIZE; i++) {
            String key = generateReelKey(i);
            ReelConfig reelConfig = reelConfigMap.get(key);
            Assert.notNull(reelConfig, "reelConfig is null, reelName is " + key);
            validateWeightMap(reelConfig.getSymbolWeights());
        }
    }

    private static String generateReelKey(int index) {
        int reel = index + 1;
        return REEL_PREFIX + reel;
    }

    private static String generateRoundKey(int index) {
        int round = index + 1;
        return ROUND_PREFIX + round;
    }

    private void validateWeightMap(Map<String, Long> weightMap) {
        Long totalWeights = 0L;
        for (Map.Entry<String, Long> entry : weightMap.entrySet()) {
            Assert.isTrue(entry.getValue() >= 0, "weight can not be less than zero");
            totalWeights += entry.getValue();
        }
        Assert.isTrue(totalWeights > 0, "weight must be greater than zero");
    }

    private void rewardMaxMultipleValidate() {
        Assert.notNull(this.rewardMaxMultiple, "rewardMaxMultiple is null");
        Assert.isTrue(this.rewardMaxMultiple > 0, "rewardMaxMultiple is must great than 0");
    }

    private void oddsConfigValidate() {
        Assert.notNull(oddsConfig, "oddsConfig is null");
        Assert.notNull(oddsConfig.commonOddsConfig, "oddsConfig.commonOddsConfig is null");
        GameInfoDo gameInfoDo = MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
        // 校验-赔率配置
        oddsConfig.commonOddsConfig.keySet().forEach(symbolId -> {
            Assert.notEmpty(oddsConfig.commonOddsConfig.get(symbolId), "Odds Config Err");
            Assert.notNull(gameInfoDo.getSymbolById(symbolId), "symbolId is not exist: " + symbolId);
        });
    }

    private void templateRangeConfigMapValidate() {
        //todo mai dong 增加校验
    }
}

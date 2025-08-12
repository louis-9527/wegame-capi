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
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;

    private MainGame mainGame;

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
        /**
         * TreeMap<modeName，weight>
         */
        private TreeMap<String, Long> modeWeights;

        /**
         * TreeMap<modeName, <reelName,ReelConfig>>
         */
        private TreeMap<String, TreeMap<String, ReelConfig>> modeConfig;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class FreeGame implements Serializable {
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
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class RoundConfig implements Serializable {
        private static final long serialVersionUID = -1002375630440628067L;
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
    public static class TemplateMultipleRange implements Serializable {
        private static final long serialVersionUID = -734841249803450751L;
        /**
         * 倍数区间开始
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
        templateRangeConfigMapValidate();
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

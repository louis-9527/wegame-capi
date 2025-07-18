package com.wegame.capi.biz.config.domain;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.VolatilityTypeEnum;
import lombok.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 游戏配置主类
 * yaml 配置文件形式
 * <a href="https://casinoplus.sg.larksuite.com/docx/HEnhdEWwioZpMFxRF4MlX7Azgig"/>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MangkukulamConfig {
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 主游戏分散符号到免费游戏数量映射
     */
    private Map<Integer, Integer> mgToFgSc;
    /**
     * 免费游戏分散符号加局数量映射
     */
    private Map<Integer, Integer> fgToFgSc;
    /**
     * 赔率表，格式为Map<符号类型, Map<符号数量, 赔率>>
     */
    private TreeMap<String, TreeMap<String, BigDecimal>> oddsTable;
    /**
     * 正常投注
     */
    private Bet normalBet;
    /**
     * 购买投注
     */
    private Bet buyBet;

    public MangkukulamConfig.MainGame getSupportMainGame(GamePlayDo gamePlay, boolean freeWheelFlag) {
        MangkukulamConfig.Bet bet = null;
        if (Objects.equals(BetTypeEnum.NORMAL_BET.getCode(), gamePlay.getBetType().getCode())) {
            bet = getNormalBet();
        } else {
            bet = getBuyBet();
        }
        MangkukulamConfig.MainGame mainGame = null;
        if (freeWheelFlag) {
            if (Objects.equals(VolatilityTypeEnum.LOW.getCode(), gamePlay.getVolatility().getCode())) {
                mainGame = bet.getLow();
            } else {
                mainGame = bet.getHigh();
            }
        } else {
            mainGame = bet.getMainGame();
        }
        return mainGame;
    }

    public void validate() {
        rewardMaxMultipleValidate();
        mgToFgScValidate();
        fgToFgScValidate();
        oddsTableValidate();
        betValidate(normalBet);
        betValidate(buyBet);
    }


    /**
     * 主游戏配置类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bet implements Serializable {
        private static final long serialVersionUID = 3905906623124847454L;
        private MainGame mainGame;
        private MainGame high;
        private MainGame low;
    }

    /**
     * 主游戏配置类
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainGame implements Serializable {
        private static final long serialVersionUID = -7278913963962257305L;
        /**
         * 轮带配置
         */
        private Map<String, Stage> stages;

        private Map<Integer, Integer> weightMap;

        public void setStages(Map<String, Stage> stages) {
            this.stages = stages;
            weightMap = IntStream.range(0, stages.size())
                    .boxed()  // 将 IntStream 转为 Stream<Integer>
                    .collect(Collectors.toMap(
                            i -> i,
                            i -> stages.get(i).getWeights()
                    ));
        }
    }

    /**
     * 轮带配置类
     */
//    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stage implements Serializable {
        private static final long serialVersionUID = 3085182463426753205L;
        /**
         * 配置权重
         */
        @Getter
        @Setter
        private int weights;

        /**
         * 轮带表（每行代表一个轮轴的符号配置）
         */
        @Getter
        @Setter
        private List<List<String>> reels;

        /**
         * 符号阶段范围配置
         */
        @Getter
        @Setter
        private List<SymbolStageRange> symbolStagesRanges;

        /**
         * 符号权重配置（不同阶段的符号权重映射）
         */
        @Getter
        private Map<String, Map<String, Integer>> symbolWeights;
        private Map<String, Integer> symbolWeightTotalMap;

        /**
         * 火咒阶段对应组配置
         */
        @Getter
        @Setter
        private List<FireNumStageRange> fireNumStagesRanges;

        /**
         * 火咒触发数量权重配置
         */
        @Getter
        private Map<String, Map<String, Integer>> fireNumWeights;
        private Map<String, Integer> fireNumWeightTotalMap;

        /**
         * 拓展摆搭倍率权重配置
         */
        @Getter
        @Setter
        private Map<String, Integer> wdExpandingTimesWeights;

        public void setSymbolWeights(Map<String, Map<String, Integer>> symbolWeights) {
            this.symbolWeights = symbolWeights;
            symbolWeightTotalMap = new TreeMap<>();
            if (Objects.nonNull(symbolWeights)) {
                symbolWeights.forEach((k, v) -> {
                    symbolWeightTotalMap.put(k, v.values().stream().mapToInt(Integer::intValue).sum());
                });
            }
        }

        public void setFireNumWeights(Map<String, Map<String, Integer>> fireNumWeights) {
            this.fireNumWeights = fireNumWeights;
            fireNumWeightTotalMap = new TreeMap<>();
            if (Objects.nonNull(fireNumWeights)) {
                fireNumWeights.forEach((k, v) -> {
                    fireNumWeightTotalMap.put(k, v.values().stream().mapToInt(Integer::intValue).sum());
                });
            }
        }

        public Pair<Integer, Map<String, Integer>> getCurrentFireWeights(int index) {
            FireNumStageRange range = getCurrentFireNumberStageRange(index);
            if (Objects.isNull(range)) {
                return null;
            } else {
                String key = String.valueOf(range.getValue());
                return Pair.of(fireNumWeightTotalMap.get(key), fireNumWeights.get(key));
            }
        }

        public Pair<Integer, Map<String, Integer>> getCurrentSymbolsWeights(int index) {
            SymbolStageRange range = getCurrentSymbolStageRange(index);
            if (Objects.isNull(range)) {
                return null;
            } else {
                String key = String.valueOf(range.getValue());
                return Pair.of(symbolWeightTotalMap.get(key), symbolWeights.get(key));
            }
        }

        private FireNumStageRange getCurrentFireNumberStageRange(int index) {
            return fireNumStagesRanges.stream()
                    .filter(item -> item.isCurrentInRange(index))
                    .findFirst()
                    .orElse(null);
        }

        private SymbolStageRange getCurrentSymbolStageRange(int index) {
            return symbolStagesRanges.stream()
                    .filter(item -> item.isCurrentInRange(index))
                    .findFirst()
                    .orElse(null);
        }

    }

    /**
     * 符号阶段范围配置类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SymbolStageRange implements Serializable {
        private static final long serialVersionUID = 3378743330763739478L;
        /**
         * 起始阶段
         */
        private int from;
        /**
         * 结束阶段，-1表示无限
         */
        private int to;
        /**
         * 对应值
         */
        private int value;

        public boolean isCurrentInRange(int index) {
            if (index < 0) {
                return false;
            }
            return index >= from && (index <= to || to == -1);
        }
    }

    /**
     * 火咒阶段对应组配置类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FireNumStageRange implements Serializable {
        private static final long serialVersionUID = 5386163794364329805L;
        /**
         * 起始阶段
         */
        private int from;
        /**
         * 结束阶段，-1表示无限
         */
        private int to;
        /**
         * 对应值
         */
        private int value;

        public boolean isCurrentInRange(int index) {
            if (index < 0) {
                return false;
            }
            return index >= from && (index <= to || to == -1);
        }
    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }

    private void mgToFgScValidate() {
        AssertUtil.assertNotNull(this.mgToFgSc, ErrorCode.ERR_INTER, "mgToFgSc is null");
        AssertUtil.assertTrue(this.mgToFgSc.size() > 0, ErrorCode.ERR_INTER, "mgToFgSc is must great than 0");
        List<Integer> keysList = Lists.newArrayList(3, 4, 5);
        this.mgToFgSc.forEach((key, value) -> {
            AssertUtil.assertTrue(keysList.contains(key), ErrorCode.ERR_INTER, "mgToFgSc ERR");
            AssertUtil.assertTrue(value > 0, ErrorCode.ERR_INTER, "mgToFgSc ERR");
        });
    }

    private void fgToFgScValidate() {
        AssertUtil.assertNotNull(this.fgToFgSc, ErrorCode.ERR_INTER, "fgToFgSc is null");
        AssertUtil.assertTrue(this.fgToFgSc.size() > 0, ErrorCode.ERR_INTER, "fgToFgSc is must great than 0");
        List<Integer> keysList = Lists.newArrayList(3, 4, 5);
        this.mgToFgSc.forEach((key, value) -> {
            AssertUtil.assertTrue(keysList.contains(key), ErrorCode.ERR_INTER, "mgToFgSc ERR");
            AssertUtil.assertTrue(value > 0, ErrorCode.ERR_INTER, "mgToFgSc ERR");
        });
    }

    private void oddsTableValidate() {
        AssertUtil.assertNotNull(this.oddsTable, ErrorCode.ERR_INTER, "oddsTable is null");
        AssertUtil.assertTrue(this.oddsTable.size() > 0, ErrorCode.ERR_INTER, "oddsTable is must great than 0");

        // 业务校验
        List<Integer> keysList = Lists.newArrayList(3, 4, 5);
        Map<String, SymbolDo> symbolMap = MangkukulamGameHelper.MANGKUKULAM_GAME_INFO.getSymbolMap();
        oddsTable.forEach((key, value) -> {
            AssertUtil.assertTrue(symbolMap.containsKey(key), ErrorCode.ERR_INTER, "oddsTable ERR");
            value.forEach((k, v) -> {
                AssertUtil.assertTrue(keysList.contains(k), ErrorCode.ERR_INTER, "oddsTable ERR");
            });
        });
    }

    private void betValidate(Bet bet) {
        AssertUtil.assertNotNull(bet, ErrorCode.ERR_INTER, "bet is null");
        AssertUtil.assertNotNull(bet.mainGame, ErrorCode.ERR_INTER, "bet#mainGame is null");
        AssertUtil.assertNotNull(bet.high, ErrorCode.ERR_INTER, "bet#high is null");
        AssertUtil.assertNotNull(bet.low, ErrorCode.ERR_INTER, "bet#low is null");

        mainGameValidate(bet.mainGame);
        mainGameValidate(bet.high);
        mainGameValidate(bet.low);
    }

    private void mainGameValidate(MainGame mainGame) {
        AssertUtil.assertNotNull(mainGame, ErrorCode.ERR_INTER, "mainGame is null");
        AssertUtil.assertNotNull(mainGame.stages, ErrorCode.ERR_INTER, "mainGame#stages is null");
        mainGame.stages.forEach((key, stage) -> stagesValidate(stage));
    }

    private void stagesValidate(Stage stage) {
        // 空校验
        AssertUtil.assertNotNull(stage, ErrorCode.ERR_INTER, "stage is null");
        AssertUtil.assertTrue(stage.weights >= 0, ErrorCode.ERR_INTER, "stage#weights is must great than 0");
        AssertUtil.assertNotNull(stage.reels, ErrorCode.ERR_INTER, "stage#reels is null");
        AssertUtil.assertTrue(stage.reels.size() > 0, ErrorCode.ERR_INTER, "stage#reels is must great than 0");
        AssertUtil.assertNotNull(stage.symbolStagesRanges, ErrorCode.ERR_INTER, "stage#symbolStagesRanges is null");
        AssertUtil.assertTrue(stage.symbolStagesRanges.size() > 0, ErrorCode.ERR_INTER, "stage#symbolStagesRanges is must great than 0");
        AssertUtil.assertNotNull(stage.symbolWeights, ErrorCode.ERR_INTER, "stage#symbolWeights is null");
        AssertUtil.assertTrue(stage.symbolWeights.size() > 0, ErrorCode.ERR_INTER, "stage#symbolWeights is must great than 0");
        AssertUtil.assertNotNull(stage.fireNumStagesRanges, ErrorCode.ERR_INTER, "stage#fireNumStagesRanges is null");
        AssertUtil.assertTrue(stage.fireNumStagesRanges.size() > 0, ErrorCode.ERR_INTER, "stage#fireNumStagesRanges is must great than 0");
        AssertUtil.assertNotNull(stage.fireNumWeights, ErrorCode.ERR_INTER, "stage#fireNumWeights is null");
        AssertUtil.assertTrue(stage.fireNumWeights.size() > 0, ErrorCode.ERR_INTER, "stage#fireNumWeights is must great than 0");
        AssertUtil.assertNotNull(stage.wdExpandingTimesWeights, ErrorCode.ERR_INTER, "stage#wdExpandingTimesWeights is null");
        AssertUtil.assertTrue(stage.wdExpandingTimesWeights.size() > 0, ErrorCode.ERR_INTER, "stage#wdExpandingTimesWeights is must great than 0");

        // reels 业务校验
        Map<String, SymbolDo> symbolMap = MangkukulamGameHelper.MANGKUKULAM_GAME_INFO.getSymbolMap();
        stage.reels.forEach(reel -> {
            reel.forEach(symbol -> {
                AssertUtil.assertTrue(symbolMap.containsKey(symbol), ErrorCode.ERR_INTER, "stage#reels#symbol is not exists in symbolMap");
            });
        });
        // todo 麦冬 scatter和valid的连续校验

        // symbolStagesRanges 业务校验
        stage.symbolStagesRanges.forEach(symbolStagesRange -> {
            int value = symbolStagesRange.getValue();
            AssertUtil.assertTrue(value >= 0, ErrorCode.ERR_INTER, "stage#symbolStagesRanges#value is must great than 0");
            AssertUtil.assertTrue(stage.symbolWeights.containsKey(String.valueOf(value)), ErrorCode.ERR_INTER, "stage#symbolStagesRanges#value is not exists");
        });

        // symbolWeights 业务校验
        stage.symbolWeights.forEach((key, valueMap) -> {
            valueMap.forEach((symbolStr, weight) -> {
                // symbolStr 校验
                String[] symbolArr = symbolStr.split(",");
                for (String symbol : symbolArr) {
                    AssertUtil.assertTrue(symbolMap.containsKey(symbol), ErrorCode.ERR_INTER, "stage#symbolWeights#symbol is not exists in symbolMap");
                }
                // weight 校验
                AssertUtil.assertTrue(weight >= 0, ErrorCode.ERR_INTER, "stage#symbolWeights#weight is must great than 0");
            });
        });

    }

}

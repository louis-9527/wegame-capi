package com.wegame.capi.biz.config.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
     * 轮面行
     */
    private List<Integer> row;
    /**
     * 轮面列
     */
    private int columns;
    /**
     * 最高赔付金额
     */
    private int maxPayout;
    /**
     * 免费游戏次数
     */
    private int fgTimes;
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

    public void validate() {
        rewardMaxMultipleValidate();
        rowValidate();
        columnsValidate();
        maxPayoutValidate();
        fgTimesValidate();
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
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainGame implements Serializable {
        private static final long serialVersionUID = -7278913963962257305L;
        /**
         * 轮带配置
         */
        private List<Stage> stages;
    }

    /**
     * 轮带配置类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stage implements Serializable {
        private static final long serialVersionUID = 3085182463426753205L;
        /**
         * 配置权重
         */
        private int weights;
        /**
         * 轮带表（每行代表一个轮轴的符号配置）
         */
        private List<List<String>> reels;
        /**
         * 符号阶段范围配置
         */
        private List<SymbolStageRange> symbolStagesRanges;
        /**
         * 符号权重配置（不同阶段的符号权重映射）
         */
        private List<Map<String, Integer>> symbolWeights;
        /**
         * 火咒阶段对应组配置
         */
        private List<FireNumStageRange> fireNumStagesRanges;
        /**
         * 火咒触发数量权重配置
         */
        private List<Map<String, Integer>> fireNumWeights;
        /**
         * 拓展摆搭倍率权重配置
         */
        private Map<String, Integer> wdExpandingTimesWeights;

        public FireNumStageRange getCurrentFireNumberStageRange(int index) {
            return fireNumStagesRanges.stream()
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
            if (index >= from && (index <= to || to == -1)) {
                return true;
            }
            return false;
        }
    }

    // todo maidong 增加方法用于根据场景获取对应的配置


    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }

    private void rowValidate() {
        AssertUtil.assertNotNull(this.row, ErrorCode.ERR_INTER, "row is null");
        AssertUtil.assertTrue(this.row.size() > 0, ErrorCode.ERR_INTER, "row is must great than 0");
    }

    private void columnsValidate() {
        AssertUtil.assertTrue(this.columns == 5, ErrorCode.ERR_INTER, "columns is must equals 5");
    }

    private void maxPayoutValidate() {
        AssertUtil.assertTrue(this.maxPayout > 0, ErrorCode.ERR_INTER, "maxPayout is must great than 0");
    }

    private void fgTimesValidate() {
        AssertUtil.assertTrue(this.fgTimes > 0, ErrorCode.ERR_INTER, "fgTimes is must great than 0");
    }

    private void mgToFgScValidate() {
        AssertUtil.assertNotNull(this.mgToFgSc, ErrorCode.ERR_INTER, "mgToFgSc is null");
        AssertUtil.assertTrue(this.mgToFgSc.size() > 0, ErrorCode.ERR_INTER, "mgToFgSc is must great than 0");
    }

    private void fgToFgScValidate() {
        AssertUtil.assertNotNull(this.fgToFgSc, ErrorCode.ERR_INTER, "fgToFgSc is null");
        AssertUtil.assertTrue(this.fgToFgSc.size() > 0, ErrorCode.ERR_INTER, "fgToFgSc is must great than 0");
    }

    private void oddsTableValidate() {
        AssertUtil.assertNotNull(this.oddsTable, ErrorCode.ERR_INTER, "oddsTable is null");
        AssertUtil.assertTrue(this.oddsTable.size() > 0, ErrorCode.ERR_INTER, "oddsTable is must great than 0");

        // 业务校验
        Map<String, SymbolDo> symbolMap = MangkukulamGameHelper.MANGKUKULAM_GAME_INFO.getSymbolMap();
        oddsTable.forEach((key, value) -> {
            AssertUtil.assertTrue(symbolMap.containsKey(key), ErrorCode.ERR_INTER, "oddsTable ERR");
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
        mainGame.stages.forEach(this::stagesValidate);
    }

    private void stagesValidate(Stage stage) {
        // 空校验
        AssertUtil.assertNotNull(stage, ErrorCode.ERR_INTER, "stage is null");
        AssertUtil.assertTrue(stage.weights > 0, ErrorCode.ERR_INTER, "stage#weights is must great than 0");
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

        // symbolWeights 业务校验
        stage.symbolWeights.forEach(symbolWeight -> {
            AssertUtil.assertTrue(symbolWeight.size() == this.columns, ErrorCode.ERR_INTER, "stage#symbolWeights#size is must equals " + this.columns);
            symbolWeight.forEach((symbol, weight) -> {
                AssertUtil.assertTrue(symbolMap.containsKey(symbol), ErrorCode.ERR_INTER, "stage#symbolWeights#symbol is not exists in symbolMap");
            });
        });
    }

}

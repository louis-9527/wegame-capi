package com.wegame.capi.biz.config.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    private Map<String, Integer> mgToFgSc;
    /**
     * 免费游戏分散符号加局数量映射
     */
    private Map<String, Integer> fgToFgSc;
    /**
     * 赔率表，格式为Map<符号类型, Map<符号数量, 赔率>>
     */
    private Map<String, Map<String, Double>> oddsTable;
    /**
     * 正常投注
     */
    private Bet normalBet;
    /**
     * 购买投注
     */
    private Bet buyBet;

    public void validate() {

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
    }

    // todo maidong 增加方法用于根据场景获取对应的配置
}

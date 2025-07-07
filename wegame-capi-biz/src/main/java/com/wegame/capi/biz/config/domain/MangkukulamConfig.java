package com.wegame.capi.biz.config.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 游戏配置主类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MangkukulamConfig {
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
    private int max_payout;
    /**
     * 免费游戏次数
     */
    private int fg_times;
    /**
     * 主游戏分散符号到免费游戏数量映射
     */
    private Map<String, Integer> mg_to_fg_sc;
    /**
     * 免费游戏分散符号加局数量映射
     */
    private Map<String, Integer> fg_to_fg_sc;
    /**
     * 赔率表，格式为Map<符号类型, Map<符号数量, 赔率>>
     */
    private Map<String, Map<String, Double>> odds_table;
    /**
     * 主游戏配置
     */
    private MainGame main_game;

    /**
     * 主游戏配置类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainGame {
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
    public static class Stage {
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
        private List<SymbolStageRange> symbol_stages_ranges;
        /**
         * 符号权重配置（不同阶段的符号权重映射）
         */
        private List<Map<String, Integer>> symbol_weights;
        /**
         * 火咒阶段对应组配置
         */
        private List<FireNumStageRange> fire_num_stages_ranges;
        /**
         * 火咒触发数量权重配置
         */
        private List<Map<String, Integer>> fire_num_weights;
        /**
         * 拓展摆搭倍率权重配置
         */
        private Map<String, Integer> wd_expanding_times_weights;
    }

    /**
     * 符号阶段范围配置类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SymbolStageRange {
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
    public static class FireNumStageRange {
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
}

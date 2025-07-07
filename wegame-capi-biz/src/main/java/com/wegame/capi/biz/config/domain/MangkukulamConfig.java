package com.wegame.capi.biz.config.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MangkukulamConfig implements Serializable {
    private static final long serialVersionUID = 765957070931379207L;

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
     * 主游戏配置
     */
    private MainGame mainGame;
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
    /**
     * 免费游戏配置
     */
    private FreeGame freeGame;

    /**
     * 主游戏配置类
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainGame {
        /**
         * 轮带配置列表
         */
        private List<Stage> stages;
    }

    /**
     * 免费游戏配置类
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FreeGame {
        /**
         * 轮带配置列表
         */
        private List<Stage> stages;
    }

    /**
     * 轮带配置类
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stage {
        /**
         * 该配置权重
         */
        private int weights;
        /**
         * 轮带表(建议后台检查每rows有没有重复SC与WD_E、WD与WD_E位置)
         */
        private List<List<String>> reels;
        /**
         * 回合对应降落图标表
         */
        private Map<String, List<String>> symbolStagesRanges;
        /**
         * 降落图标权重
         */
        private Map<String, Integer> symbolWeights;
        /**
         * 倍率图标倍率权重
         */
        private Map<String, Integer> mpTimesWeights;
    }

    /**
     * 火咒阶段对应组配置类
     */
    @Data
    @SuperBuilder
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

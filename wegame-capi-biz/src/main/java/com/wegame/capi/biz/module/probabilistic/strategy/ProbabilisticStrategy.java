package com.wegame.capi.biz.module.probabilistic.strategy;

import com.wegame.capi.common.enums.GameTypeEnum;

/**
 * 概率策略
 */
public interface ProbabilisticStrategy {

    /**
     * 根据游戏类型和列序号随机生产一个元素
     * @param gameType 游戏类型
     * @param cellNo 列序号，0：第一列, 1：第二列
     * @return SymbolDo
     */
    String randomOneSymbol(GameTypeEnum gameType, int cellNo);

}

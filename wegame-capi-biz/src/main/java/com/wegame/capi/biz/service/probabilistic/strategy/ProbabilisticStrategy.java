package com.wegame.capi.biz.service.probabilistic.strategy;

import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.GameTypeEnum;

/**
 * 概率策略
 */
public interface ProbabilisticStrategy {

    /**
     * 根据游戏类型和列序号随机生产一个元素
     * @param gameType 游戏类型
     * @param cellNo 列序号，1则表示第一列
     * @return SymbolDo
     */
    public SymbolDo randomOneSymbol(GameTypeEnum gameType, int cellNo);

}

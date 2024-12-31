package com.wegame.capi.biz.service.probabilistic;

import com.wegame.capi.biz.domain.SymbolDo;

/**
 * 概率服务
 */
public abstract class AbstractProbabilisticService {

    /**
     * 根据游戏id和列随机生产一个元素
     * @param gameId
     * @param cellNo
     * @return
     */
    public abstract SymbolDo randomOneSymbol(String gameId, int cellNo);

}

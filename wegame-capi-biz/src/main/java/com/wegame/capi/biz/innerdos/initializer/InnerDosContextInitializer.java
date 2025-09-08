package com.wegame.capi.biz.innerdos.initializer;

import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.client.enums.GameTypeEnum;

/**
 * 内部干预context 初始化器
 */
public interface InnerDosContextInitializer {
    /**
     * 初始化器类型 game维度
     *
     * @return gameType
     */
    GameTypeEnum gameType();

    /**
     * context 初始化
     *
     * @param initParams 初始化参数，根据实际自定义
     * @return context
     */
    InnerDosContext init(Object... initParams);
}

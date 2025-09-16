package com.wegame.capi.biz.innerdos.initializer;

import com.wegame.capi.biz.innerdos.config.GameRtpDosCondition;
import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;

import java.util.Map;

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

    /**
     * 大盘rtp干预条件 配置
     * Map<游戏内干预类型，游戏内干预条件>
     */
    Map<GameInnerDosTypeEnum, GameRtpDosCondition> getGameRtpDosConditionMap();
}

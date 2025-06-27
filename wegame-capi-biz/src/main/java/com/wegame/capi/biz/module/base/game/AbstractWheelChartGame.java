package com.wegame.capi.biz.module.base.game;

import com.wegame.capi.biz.converter.ToVoConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象游戏
 *
 * @param <R>
 */
@Slf4j
public abstract class AbstractWheelChartGame<R extends ToVoConverter> extends  AbstractGame<R> {

}

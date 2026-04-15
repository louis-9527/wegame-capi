package com.wegame.capi.biz.module.base.game;


import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;

import java.util.List;

/**
 * 模版游戏
 * <p>
 * R: 游戏记录
 * T：游戏ES模版
 */
public interface TemplateGame<R, T extends BetRecordTemplateEsDo> {

    /**
     * 通用模版Play
     *
     * @param betDo                 投注上下文
     * @param gamePlayDo            游戏上下文
     * @param template              es模版
     * @return 游戏记录
     */
    List<R> templatePlay(BetDo betDo, GamePlayDo gamePlayDo, T template);

}
package com.wegame.capi.biz.module.base.game.template;


import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.domain.TemplateMultipleRange;

import java.util.List;

/**
 * 游戏
 */
public interface TemplateGame<R, T extends BetRecordTemplateEsDo> {

    /**
     * 通用模版Play
     *
     * @param bet      投注上下文
     * @param gamePlay 游戏上下文
     * @param templateMultipleRange 模版倍率范围
     * @param template es模版
     * @return 游戏记录
     */
    List<R> commonTemplatePlay(BetDo bet, GamePlayDo gamePlay, TemplateMultipleRange templateMultipleRange, T template);

}
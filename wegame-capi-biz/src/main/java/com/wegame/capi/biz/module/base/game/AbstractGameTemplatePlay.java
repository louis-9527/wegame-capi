package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.TreeMap;

/**
 * 游戏模版Play
 */
@Slf4j
public abstract class AbstractGameTemplatePlay<R extends ToVoConverter, T extends BetRecordTemplateEsDo> implements Game<R> {

    @Autowired
    public RandomService randomService;

    public abstract TemplateGame<R, T> templateGame();

    public T getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        // do nothing, subClass override this method
        return null;
    }

    @Override
    public List<R> play(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        T template = getTemplate(betDo, gamePlayDo);
        return templateGame().templatePlay(betDo, gamePlayDo, template);
    }

    public TemplateMultipleRange getTemplateMultipleRange(TreeMap<String, Integer> treeMap) {
        String templateMultipleRange = randomService.randomKeyByWeight(treeMap);
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }
}

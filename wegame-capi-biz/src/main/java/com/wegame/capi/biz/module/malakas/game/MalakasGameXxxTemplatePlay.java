package com.wegame.capi.biz.module.malakas.game;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractGameTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.MALAKAS_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("malakasGameXxxTemplatePlay")
@ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
public class MalakasGameXxxTemplatePlay extends AbstractGameTemplatePlay<MalakasGameRecordDo, MalakasBetRecordTemplateEsDo> {

    @Autowired
    MalakasGameCommonTemplatePlay malakasGameCommonTemplatePlay;

    @Override
    public TemplateGame templateGame() {
        return malakasGameCommonTemplatePlay;
    }

    @Override
    public TemplateMultipleRange getTemplateMultipleRange(BetDo betDo, GamePlayDo gamePlayDo) {
        return null;
    }

    @Override
    public MalakasBetRecordTemplateEsDo getTemplate(BetDo betDo, GamePlayDo gamePlayDo, TemplateMultipleRange templateMultipleRange) {
        return null;
    }
}
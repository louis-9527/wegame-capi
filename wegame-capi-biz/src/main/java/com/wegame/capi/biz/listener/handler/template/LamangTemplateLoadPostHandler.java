package com.wegame.capi.biz.listener.handler.template;

import com.wegame.base.enums.EnvEnum;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler<LamangBetRecordTemplateEsDo> {

    @Autowired
    LamangConfig lamangConfig;

    @Override
    public Class<LamangBetRecordTemplateEsDo> getClassType() {
        return LamangBetRecordTemplateEsDo.class;
    }

    @Override
    public void handle() {
        if (env.equals(EnvEnum.LOCAL.getValue())) {
            return;
        }
        if (!supportGame(GameTypeEnum.LAM_ANG)) {
            return;
        }
        commonWarmUp(lamangConfig.getCommonTemplateConfig());
    }
}

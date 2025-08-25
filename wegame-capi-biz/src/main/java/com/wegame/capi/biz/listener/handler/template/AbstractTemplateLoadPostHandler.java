package com.wegame.capi.biz.listener.handler.template;

import com.google.common.collect.Lists;
import com.wegame.base.enums.EnvEnum;
import com.wegame.capi.biz.listener.handler.AbstractPostHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.biz.service.template.GameRecordTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 模版预加载后处理器
 */
@Slf4j
public abstract class AbstractTemplateLoadPostHandler extends AbstractPostHandler {
    public static final String MESSAGE_FORMAT = "%s,投注模版预加载失败,模版倍率范围：%s";
    @Autowired
    public BetRecordTemplateService betRecordTemplateService;
    @Autowired
    public GameRecordTemplateService gameRecordTemplateService;

    public static final List<String> enableEnvList = Lists.newArrayList(EnvEnum.STS.getValue(), EnvEnum.PROD.getValue());
    public boolean supportWarmUpEnv() {
        return enableEnvList.contains(env);
    }
}

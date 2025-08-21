package com.wegame.capi.biz.listener.handler.template;

import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.listener.handler.AbstractPostHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.biz.service.template.GameRecordTemplateService;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
}

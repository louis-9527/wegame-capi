package com.wegame.capi.biz.manager.template;

import com.wegame.capi.biz.domain.biz.operation.BetRecordTemplateDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractBetRecordTemplateManager<T> {
    @Autowired
    BetRecordTemplateService betRecordTemplateService;

    public abstract Long add(BetRecordTemplateDo<T> betRecord, TemplateTypeEnum gameType);
}

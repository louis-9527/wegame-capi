package com.wegame.capi.biz.listener.runner;

import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 投注记录模版预加载后处理执行器
 */
@Slf4j
@Component
public class MariaMakilingBetRecordTemplatePreLoadPostRunner extends AbstractBetRecordTemplatePreLoadPostRunner {
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

    @Override
    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.MARIA_MAKILING;
    }

    @Override
    protected Map<String, MariaMakilingConfig.TemplateMultipleRange> getTemplateRangeConfigMap() {
        return mariaMakilingConfig.getTemplateRangeConfigMap();
    }

    @Override
    protected String getPostRunnerName() {
        return "MariaMakilingBetRecordTemplatePreLoadPostRunner";
    }
}

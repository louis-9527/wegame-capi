package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.EnvEnum;
import com.wegame.capi.biz.config.domain.BayanihanConfig;
import com.wegame.capi.biz.domain.biz.template.bayanihan.BayanihanBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.wegame.capi.common.Constants.BAYANIHAN_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = BAYANIHAN_PROP, havingValue = TRUE_VAL)
public class BayanihanTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    @Autowired
    BayanihanConfig bayanihanConfig;

    @Autowired
    BetRecordTemplateEsService<BayanihanBetRecordTemplateEsDo> betRecordTemplateEsService;

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.ATLANTIS;
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.ATLANTIS)) {
            return;
        }
        if (StringUtils.equals(env, EnvEnum.LOCAL.getValue()) || StringUtils.equals(env, EnvEnum.DEV.getValue())) {
            return;
        }
        Map<TemplateTypeEnum, BayanihanConfig.TemplateRangeConfig> map = bayanihanConfig.getTemplateConfig().getTemplateRangeConfig();
        for (Map.Entry<TemplateTypeEnum, BayanihanConfig.TemplateRangeConfig> entry : map.entrySet()) {
            loadTemplate(entry.getKey(), entry.getValue());
        }
    }

    private void loadTemplate(TemplateTypeEnum templateType, BayanihanConfig.TemplateRangeConfig config) {
        try {
            log.info("main game template load start...");
            List<Long> mainGameTemplateIdList = new LinkedList<>();
            config.getMainGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(templateType, BetTypeEnum.NORMAL_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    mainGameTemplateIdList.addAll(idList);
                }
            });
            loadLocalCache(templateType, BetTypeEnum.NORMAL_BET, mainGameTemplateIdList);
            log.info("main game template load end...");
        } catch (Exception e) {
            log.error("template load error", e);
        }

        try {
            log.info("free game template load start...");
            List<Long> freeGameTemplateIdList = new LinkedList<>();
            config.getFreeGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(templateType, BetTypeEnum.BUY_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    freeGameTemplateIdList.addAll(idList);
                }
            });
            //loadRedisCache(BetTypeEnum.BUY_BET, freeGameTemplateIdList);
            log.info("free game template load end...");
        } catch (Exception e) {
            log.info("free game template load error", e);
        }
    }

    private List<Long> loadTemplateIdList(TemplateTypeEnum templateType, BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        log.info("templateIdList load start, templateType: {}, betType: {}, range: {}-{}...", templateType.name(), betType , multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd(), BayanihanBetRecordTemplateEsDo.class);
            if (!bayanihanConfig.getTemplateConfig().supportWarmUp(betType, multipleRange)) {
                log.info("templateIdList load end, betType: {}, range: {}-{}, size: {}， no load gameRecordTemplate", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return null;
            }
            log.info("templateIdList load success, betType: {}, range: {}-{}, size: {}", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
            return betRecordTemplateIdList;
        } catch (Exception e) {
            log.info("templateIdList load error, betType: {}, range: {}-{}", betType, multipleRange.getStart(), multipleRange.getEnd(), e);
            return null;
        }
    }

    private void loadLocalCache(TemplateTypeEnum templateType, BetTypeEnum betType, List<Long> templateIdList) {
        if (!supportWarmUpEnv()) {
            return;
        }
        try {
            if (CollectionUtil.isNotEmpty(templateIdList)) {
                log.info("loadLocalCache start, betType: {} size: {}", betType, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<BayanihanBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(templateType, idList, BayanihanBetRecordTemplateEsDo.class);
                    templateList.forEach(templateItem -> betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem));
                });
                log.info("loadLocalCache success, betType: {}, size: {}", betType, templateIdList.size());
            }
        } catch (Exception e) {
            log.info("loadLocalCache error, betType: {}", betType, e);
        }
    }
}

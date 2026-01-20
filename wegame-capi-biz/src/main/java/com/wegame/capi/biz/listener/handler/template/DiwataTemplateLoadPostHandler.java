package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.template.malakas.DiwataBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.wegame.capi.common.Constants.DIWATA_MAYARI_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    @Autowired
    DiwataMayariConfig diwataMayariConfig;
    @Autowired
    BetRecordTemplateEsService<DiwataBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.DIWATA_MAYARI)) {
            return;
        }
        List<TemplateTypeEnum> templateTypeList = TemplateTypeEnum.listByGameType(GameTypeEnum.DIWATA_MAYARI);
        Optional.ofNullable(templateTypeList).orElse(Collections.emptyList())
                .forEach(item -> {
                    loadTemplate(BetTypeEnum.NORMAL_BET, item);
                    loadTemplate(BetTypeEnum.BUY_BET, item);
                });
    }


    private void loadTemplate(BetTypeEnum betType, TemplateTypeEnum templateType) {
        try {
            log.info("{} game template load start... {}", betType, templateType);
            List<Long> templateIdList = new LinkedList<>();
            diwataMayariConfig.getTemplateRangeConfig().getTemplateRangeMap().get(templateType).get(betType).keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(betType, templateType, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    templateIdList.addAll(idList);
                }
            });
            loadLocalCache(betType, templateType, templateIdList);
            log.info("{} game template load end... {}", betType, templateType);
        } catch (Exception e) {
            log.info("{} game template load error {} ", betType, templateType, e);
        }
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, TemplateTypeEnum templateType, TemplateMultipleRange multipleRange) {
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd(), DiwataBetRecordTemplateEsDo.class);
            if (!diwataMayariConfig.getTemplateRangeConfig().supportWarmUp(betType, multipleRange)) {
                log.info("templateIdList load end, betType: {}, range: {}-{}, size: {}， no load betRecordTemplate", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return null;
            }
            log.info("templateIdList load success, betType: {}, range: {}-{}, size: {}", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
            return betRecordTemplateIdList;
        } catch (Exception e) {
            log.info("templateIdList load error, betType: {}, range: {}-{}", betType, multipleRange.getStart(), multipleRange.getEnd(), e);
            return null;
        }
    }

    private void loadLocalCache(BetTypeEnum betType, TemplateTypeEnum templateType, List<Long> templateIdList) {
        if (!supportWarmUpEnv()) {
            return;
        }
        try {
            if (CollectionUtil.isNotEmpty(templateIdList)) {
                log.info("loadLocalCache start, betType: {}, templateType: {}, size: {}", betType, templateType, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<DiwataBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.DIWATA_MAYARI, idList, DiwataBetRecordTemplateEsDo.class);
                    templateList.forEach(templateItem -> {
                        betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem);
                    });
                });
                log.info("loadLocalCache success, betType: {}, templateType: {}, size: {}", betType, templateType, templateIdList.size());
            }
        } catch (Exception e) {
            log.info("loadLocalCache error, betType: {}", betType, e);
        }
    }
}

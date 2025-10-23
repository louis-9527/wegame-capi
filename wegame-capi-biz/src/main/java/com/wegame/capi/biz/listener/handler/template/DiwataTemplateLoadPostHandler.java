package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.template.malakas.DiwataBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.DIWATA_MAYARI;
    }

    protected DiwataMayariConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return diwataMayariConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.DIWATA_MAYARI)) {
            return;
        }
        DiwataMayariConfig.TemplateRangeConfig config = this.getTemplateRangeConfig();
        if (Objects.isNull(config)) {
            return;
        }
        try {
            log.info("main game template load start...");
            List<Long> mainGameTemplateIdList = new LinkedList<>();
            config.getMainGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(BetTypeEnum.NORMAL_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    mainGameTemplateIdList.addAll(idList);
                }
            });
            loadLocalCache(BetTypeEnum.NORMAL_BET, mainGameTemplateIdList);
            log.info("main game template load end...");
        } catch (Exception e) {
            log.error("template load error", e);
        }

        try {
            log.info("free game template load start...");
            List<Long> freeGameTemplateIdList = new LinkedList<>();
            config.getFreeGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(BetTypeEnum.BUY_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    freeGameTemplateIdList.addAll(idList);
                }
            });
            log.info("free game template load end...");
        } catch (Exception e) {
            log.info("free game template load error", e);
        }
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd(), DiwataBetRecordTemplateEsDo.class);
            if (!TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.DIWATA_MAYARI, betType, multipleRange)) {
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

    private void loadLocalCache(BetTypeEnum betType, List<Long> templateIdList) {
        if (!supportWarmUpEnv()) {
            return;
        }
        TemplateTypeEnum templateType = this.getTemplateType();
        try {
            if (CollectionUtil.isNotEmpty(templateIdList)) {
                log.info("loadLocalCache start, betType: {}, size: {}", betType, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<DiwataBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.DIWATA_MAYARI, idList, DiwataBetRecordTemplateEsDo.class);
                    templateList.forEach(templateItem -> {
                        betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem);
                    });
                });
                log.info("loadLocalCache success, betType: {}, size: {}", betType, templateIdList.size());
            }
        } catch (Exception e) {
            log.info("loadLocalCache error, betType: {}", betType, e);
        }
    }

}

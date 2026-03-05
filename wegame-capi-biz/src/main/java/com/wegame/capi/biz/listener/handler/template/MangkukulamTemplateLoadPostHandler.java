package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.template.mangkukulam.MangkukulamBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.MangkukulamTagEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.wegame.capi.common.Constants.MANGKUKULAM_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
public class MangkukulamTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Autowired
    BetRecordTemplateEsService<MangkukulamBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.MANGKUKULAM)) {
            return;
        }
        Map<TemplateTypeEnum, MangkukulamConfig.TemplateRangeConfig> configMap = mangkukulamConfig.getTemplateConfig().getTemplateRangeConfigMapping();
        for (Map.Entry<TemplateTypeEnum, MangkukulamConfig.TemplateRangeConfig> entry : configMap.entrySet()) {
            MangkukulamConfig.TemplateRangeConfig rangeConfig = entry.getValue();
            TemplateTypeEnum templateType = entry.getKey();
            if (Objects.nonNull(rangeConfig) && Objects.nonNull(templateType)) {
                log.info("start load mangkukulam template from {}", templateType.getBetRecordTemplateName());
                loadTemplate(templateType, rangeConfig);
            }
        }
    }

    private void loadTemplate(TemplateTypeEnum templateType, MangkukulamConfig.TemplateRangeConfig config) {
        // 1. 普通轮首轮
        try {
            log.info("normal bet main game template load start...");
            List<Long> normalBetMainGameTemplateIdList = new LinkedList<>();
            config.getNormalBetMainGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(templateType, MangkukulamTagEnum.NORMAL_BET_INITIAL_STAGE, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    normalBetMainGameTemplateIdList.addAll(idList);
                }
            });
            loadTemplateToLocalCache(templateType, MangkukulamTagEnum.NORMAL_BET_INITIAL_STAGE.getCode(), normalBetMainGameTemplateIdList);
            log.info("normal bet main game template load end...");
        } catch (Exception e) {
            log.error("normal bet main game template load error", e);
        }

        // 2. 免费轮首轮
        try {
            log.info("buy bet main game template load start...");
            List<Long> buybetMainGameTemplateIdList = new LinkedList<>();
            config.getBuyBetMainGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(templateType, MangkukulamTagEnum.BUY_BET_INITIAL_STAGE, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    buybetMainGameTemplateIdList.addAll(idList);
                }
            });
            loadTemplateToLocalCache(templateType, MangkukulamTagEnum.BUY_BET_INITIAL_STAGE.getCode(), buybetMainGameTemplateIdList);
            log.info("buy bet  main game template load end...");
        } catch (Exception e) {
            log.info("buy bet main game template load error", e);
        }

        // 3. 加载后续轮
        try {
            log.info("free game template load start...");
            List<Long> freeGameTemplateList = new LinkedList<>();
            mergeFreeWheelRange(config).forEach(rangeKey -> {
                List<Long> idList = loadTemplateIdList(templateType, MangkukulamTagEnum.FREE_GAME_STAGE, TemplateMultipleRange.build(rangeKey));
                if (CollectionUtils.isNotEmpty(idList)) {
                    freeGameTemplateList.addAll(idList);
                }
            });
            loadTemplateToLocalCache(templateType, MangkukulamTagEnum.FREE_GAME_STAGE.getCode(), freeGameTemplateList);
        } catch (Exception e) {
            log.info("free game template load error", e);
        }
    }

    private Set<String> mergeFreeWheelRange(MangkukulamConfig.TemplateRangeConfig config) {
        Set<String> freeWheelKeySet = new HashSet<>();

        freeWheelKeySet.addAll(config.getNormalBetFreeGameHigh().keySet());
        freeWheelKeySet.addAll(config.getNormalBetFreeGameLow().keySet());
        freeWheelKeySet.addAll(config.getBuyBetFreeGameHigh().keySet());
        freeWheelKeySet.addAll(config.getBuyBetFreeGameLow().keySet());

        return freeWheelKeySet;
    }

    private List<Long> loadTemplateIdList(TemplateTypeEnum templateType, MangkukulamTagEnum tag, TemplateMultipleRange multipleRange) {
        log.info("templateIdList load start, tag: {}, range: {}-{}...", tag, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByTagAndMultipleRange(templateType, tag.getCode(), multipleRange.getStart(), multipleRange.getEnd(), MangkukulamBetRecordTemplateEsDo.class);
            if (!mangkukulamConfig.getTemplateConfig().supportWarmUp(tag, multipleRange)) {
                log.info("templateIdList load end, tag: {}, range: {}-{}, size: {}， no load gameRecordTemplate", tag, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return null;
            }
            log.info("templateIdList load success, tag: {}, range: {}-{}, size: {}", tag, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
            return betRecordTemplateIdList;
        } catch (Exception e) {
            log.info("templateIdList load error, tag: {}, range: {}-{}", tag, multipleRange.getStart(), multipleRange.getEnd(), e);
            return null;
        }
    }

    private void loadTemplateToLocalCache(TemplateTypeEnum templateType, String tag, List<Long> templateIdList) {
        if (!supportWarmUpEnv()) {
            return;
        }
        try {
            if (CollectionUtil.isNotEmpty(templateIdList)) {
                log.info("loadLocalCache start, index : {}, tag: {}, size: {}", templateType.getBetRecordTemplateName(), tag, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<MangkukulamBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.MANGKUKULAM, idList, MangkukulamBetRecordTemplateEsDo.class);
                    templateList.forEach(templateItem -> {
                        betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem);
                    });
                });
                log.info("loadLocalCache success, index: {}, tag: {}, size: {}", templateType.getBetRecordTemplateName(), tag, templateIdList.size());
            }
        } catch (Exception e) {
            log.info("loadLocalCache error, tag: {}", tag, e);
        }
    }

}

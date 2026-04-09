package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.config.domain.SirenaConfig;
import com.wegame.capi.biz.domain.biz.template.sirena.SirenaBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.SirenaTagEnum;
import com.wegame.capi.common.enums.TemplateMultipleWarmUpEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.wegame.capi.common.Constants.SIRENA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
public class SirenaTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {

    public TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.SIRENA;
    }

    @Autowired
    SirenaConfig sirenaConfig;

    @Autowired
    BetRecordTemplateEsService<SirenaBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.SIRENA)) {
            return;
        }
        SirenaConfig.TemplateRangeConfig templateRangeConfig = sirenaConfig.getTemplateRangeConfig();
        loadTemplate(getTemplateType(), templateRangeConfig);
    }

    private void loadTemplate(TemplateTypeEnum templateType, SirenaConfig.TemplateRangeConfig config) {
        // 1. 普通投注
        try {
            log.info("normal bet main game template load start...");
            List<Long> normalBetTemplateIdList = new LinkedList<>();
            config.getMainGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(templateType, SirenaTagEnum.SIRENA_FIRST_ROUND, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    normalBetTemplateIdList.addAll(idList);
                }
            });
            loadTemplateToLocalCache(templateType, SirenaTagEnum.SIRENA_FIRST_ROUND.getCode(), normalBetTemplateIdList);
            log.info("normal bet main game template load end...");
        } catch (Exception e) {
            log.error("normal bet main game template load error", e);
        }

        // 2. 购买投注
        try {
            log.info("buy bet main game template load start...");
            List<Long> buybetTemplateIdList = new LinkedList<>();
            config.getFreeGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(templateType, SirenaTagEnum.SIRENA_BUY_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    buybetTemplateIdList.addAll(idList);
                }
            });
            loadTemplateToLocalCache(templateType, SirenaTagEnum.SIRENA_BUY_BET.getCode(), buybetTemplateIdList);
            log.info("buy bet  main game template load end...");
        } catch (Exception e) {
            log.info("buy bet main game template load error", e);
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

    private List<Long> loadTemplateIdList(TemplateTypeEnum templateType, SirenaTagEnum tag, TemplateMultipleRange multipleRange) {
        log.info("templateIdList load start, template: {}, tag: {}, range: {}-{}...", templateType.getBetRecordTemplateName(), tag, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByTagAndAvgMultipleRange(templateType, tag.getCode(), multipleRange.getStart(), multipleRange.getEnd(), SirenaBetRecordTemplateEsDo.class);
            TemplateMultipleWarmUpEnum warmUpEnum = TemplateMultipleWarmUpEnum.valueOfCode(templateType.getGameType());
            AssertUtil.assertNotNull(warmUpEnum, ErrorCode.ERR_INTER);
            if (!warmUpEnum.canWarmUp(tag.getCode(), multipleRange.getEnd())) {
                log.info("templateIdList load end, template: {}, tag: {}, range: {}-{}, size: {}， no load gameRecordTemplate", templateType.getBetRecordTemplateName(), tag, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return null;
            }
            log.info("templateIdList load success, template: {}, tag: {}, range: {}-{}, size: {}", templateType.getBetRecordTemplateName(), tag, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
            return betRecordTemplateIdList;
        } catch (Exception e) {
            log.info("templateIdList load error,template:{}, tag: {}, range: {}-{}", templateType.getBetRecordTemplateName(), tag, multipleRange.getStart(), multipleRange.getEnd(), e);
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
                    List<SirenaBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(templateType, idList, SirenaBetRecordTemplateEsDo.class);
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

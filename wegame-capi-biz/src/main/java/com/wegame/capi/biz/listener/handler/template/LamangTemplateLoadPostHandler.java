package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    BetRecordTemplateEsService<LamangBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public Class<LamangBetRecordTemplateEsDo> getClassType() {
        return LamangBetRecordTemplateEsDo.class;
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.LAM_ANG)) {
            return;
        }
        Map<TemplateTypeEnum, LamangConfig.TemplateRangeConfig> templateConfigMapping = lamangConfig.getTemplateConfig().getTemplateRangeConfig();
        for (TemplateTypeEnum templateType : templateConfigMapping.keySet()) {
            LamangConfig.TemplateRangeConfig config = templateConfigMapping.get(templateType);
            AssertUtil.assertNotNull(config, ErrorCode.ERR_INTER, "template config can not be null");
            loadTemplate(config, templateType);
        }
    }

    private void loadTemplate(LamangConfig.TemplateRangeConfig config, TemplateTypeEnum templateType) {
        try {
            log.info("main game template load start...");
            List<Long> mainGameTemplateIdList = new LinkedList<>();
            config.getMainGame().keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(BetTypeEnum.NORMAL_BET, templateType, TemplateMultipleRange.build(key));
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
                List<Long> idList = loadTemplateIdList(BetTypeEnum.BUY_BET, templateType, TemplateMultipleRange.build(key));
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

    private List<Long> loadTemplateIdList(BetTypeEnum betType, TemplateTypeEnum templateType, TemplateMultipleRange multipleRange) {
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd(), LamangBetRecordTemplateEsDo.class);
            if (!lamangConfig.getTemplateConfig().supportWarmUp(betType, multipleRange)) {
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
                log.info("loadLocalCache start, templateType : {}, betType: {}, size: {}", templateType.getBetRecordTemplateName(), betType, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<LamangBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(templateType, idList, LamangBetRecordTemplateEsDo.class);
                    templateList.forEach(templateItem -> {
                        betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem);
                    });
                });
                log.info("loadLocalCache success, templateType : {}, betType: {}, size: {}", templateType.getBetRecordTemplateName(), betType, templateIdList.size());
            }
        } catch (Exception e) {
            log.info("loadLocalCache error, betType: {}", betType, e);
        }
    }
}

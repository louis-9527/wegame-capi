package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.EnvEnum;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
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

import java.util.*;

import static com.wegame.capi.common.Constants.MALAKAS_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
public class MalakasTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    BetRecordTemplateEsService<MalakasBetRecordTemplateEsDo> betRecordTemplateEsService;

    public static Map<String, String> normalBetRangeMap = new HashMap<>();
    public static Map<String, String> buyBetRangeMap = new HashMap<>();

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.MALAKAS;
    }

    protected MalakasConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return malakasConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (env.equals(EnvEnum.LOCAL.getValue())) {
            return;
        }
        if (!supportGame(GameTypeEnum.MALAKAS)) {
            return;
        }
        loadTemplate(BetTypeEnum.NORMAL_BET);
        loadTemplate(BetTypeEnum.BUY_BET);
        malakasConfig.getInventoryControlConfig().getRewardConfig().getRewardConfigMap().forEach((key, value) -> {
            value.forEach((subKey, subValue) -> {
                value.forEach((thirdKey, thirdValue) -> {
                    loadTemplateIdList(key, thirdKey);
                });
            });
        });
    }

    private void loadTemplate(BetTypeEnum betType) {
        try {
            log.info("{} game template load start...", betType);
            List<Long> templateIdList = new LinkedList<>();
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                malakasConfig.getTemplateRangeConfig().getMainGame().keySet().forEach((String key) -> {
                    List<Long> idList = loadTemplateIdList(betType, key);
                    if (CollectionUtils.isNotEmpty(idList)) {
                        templateIdList.addAll(idList);
                    }
                });
            } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
                malakasConfig.getTemplateRangeConfig().getFreeGame().keySet().forEach((String key) -> {
                    List<Long> idList = loadTemplateIdList(betType, key);
                    if (CollectionUtils.isNotEmpty(idList)) {
                        templateIdList.addAll(idList);
                    }
                });
            }
            loadLocalCache(betType, templateIdList);
            log.info("{} game template load end...", betType);
        } catch (Exception e) {
            log.info("{} game template load error", betType, e);
        }
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, String rangeStr) {
        if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
            if (normalBetRangeMap.containsKey(rangeStr)) {
                return Collections.emptyList();
            }
        } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
            if (buyBetRangeMap.containsKey(rangeStr)) {
                return Collections.emptyList();
            }
        }
        TemplateMultipleRange multipleRange = TemplateMultipleRange.build(rangeStr);
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd(), MalakasBetRecordTemplateEsDo.class);
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                normalBetRangeMap.put(rangeStr, rangeStr);
            } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
                buyBetRangeMap.put(rangeStr, rangeStr);
            }
            if (!TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.MALAKAS, betType, multipleRange)) {
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
                    List<MalakasBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.MALAKAS, idList, MalakasBetRecordTemplateEsDo.class);
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

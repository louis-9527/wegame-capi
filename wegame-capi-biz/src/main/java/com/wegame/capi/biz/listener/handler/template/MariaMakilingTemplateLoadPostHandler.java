package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.template.mariamakiling.MariaMakilingBetRecordTemplateEsDo;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
public class MariaMakilingTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    private static final String key = "load:template:redis:cache:MariaMakiling7";
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;
    @Autowired
    BetRecordTemplateEsService<MariaMakilingBetRecordTemplateEsDo> betRecordTemplateEsService;

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.MARIA_MAKILING;
    }

    protected MariaMakilingConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return mariaMakilingConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.MARIA_MAKILING)) {
            log.warn("not support game MARIA_MAKILING");
            return;
        }
        MariaMakilingConfig.TemplateRangeConfig config = this.getTemplateRangeConfig();
        loadTemplate(config.getMainGame().keySet(), BetTypeEnum.NORMAL_BET);
        loadTemplate(config.getFreeGame().keySet(), BetTypeEnum.BUY_BET);
    }

    public void loadTemplate(Set<String> keySet, BetTypeEnum betType) {
        if (CollectionUtil.isEmpty(keySet)) {
            return;
        }
        try {
            log.info("{} game template load start...", betType);
            List<Long> templateIdList = new LinkedList<>();
            keySet.forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(betType, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    templateIdList.addAll(idList);
                }
            });
            loadLocalCache(BetTypeEnum.NORMAL_BET, templateIdList);
            log.info("{} game template load end...", betType);
        } catch (Exception e) {
            log.info("{} game template load error", betType, e);
        }
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd(), MariaMakilingBetRecordTemplateEsDo.class);
            if (!TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.MARIA_MAKILING, betType, multipleRange)) {
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
        if (CollectionUtil.isEmpty(templateIdList)) {
            log.warn("loadLocalCache fail, betType: {}, size is 0", betType);
            return;
        }
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("loadLocalCache start, betType: {}, size: {}", betType, templateIdList.size());
        List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
        multiList.forEach(idList -> {
            List<MariaMakilingBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.MARIA_MAKILING, idList, MariaMakilingBetRecordTemplateEsDo.class);
            templateList.forEach(templateItem -> {
                betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem);
            });
        });
        log.info("loadLocalCache success, betType: {}, size: {}", betType, templateIdList.size());
    }
}

package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.template.ibong.IbongAdarnaBetRecordTemplateEsDo;
import com.wegame.capi.biz.innerdos.context.DefaultInnerDosContext;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.*;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
public class IbongAdarnaTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    private static final String key = "load:template:redis:cache:IbongAdarna0";
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    BetRecordTemplateEsService<IbongAdarnaBetRecordTemplateEsDo> betRecordTemplateEsService;

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.IBONG_ADARNA;
    }

    protected DefaultInnerDosContext.DefaultTemplateRangeConfig getTemplateRangeConfig() {
        return ibongAdarnaConfig.getInnerDosConfig().getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.IBONG_ADARNA)) {
            return;
        }
        DefaultInnerDosContext.DefaultTemplateRangeConfig templateRangeConfig = this.getTemplateRangeConfig();
        Map<String, BigDecimal> normalBetTemplateRangeMap = templateRangeConfig.getTemplateRangeMap(GameInnerDosTypeEnum.NORMAL_DOS, BetSubTypeEnum.NORMAL_BET);
        try {
            log.info("normal game template load start...");
            List<Long> normalBetGameTemplateIdList = new LinkedList<>();
            normalBetTemplateRangeMap.keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(BetTypeEnum.NORMAL_BET, BetSubTypeEnum.NORMAL_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    normalBetGameTemplateIdList.addAll(idList);
                }
            });
            loadLocalCache(BetTypeEnum.NORMAL_BET, BetSubTypeEnum.NORMAL_BET, normalBetGameTemplateIdList);
            log.info("normal game template load end...");
        } catch (Exception e) {
            log.error("template load error", e);
        }

        Map<String, BigDecimal> exBetTemplateRangeMap = templateRangeConfig.getTemplateRangeMap(GameInnerDosTypeEnum.NORMAL_DOS, BetSubTypeEnum.EX_BET);
        try {
            log.info("ex game template load start...");
            List<Long> exBetGameTemplateIdList = new LinkedList<>();
            exBetTemplateRangeMap.keySet().forEach((String key) -> {
                List<Long> idList = loadTemplateIdList(BetTypeEnum.BUY_BET, BetSubTypeEnum.EX_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    exBetGameTemplateIdList.addAll(idList);
                }
            });
            loadLocalCache(BetTypeEnum.NORMAL_BET, BetSubTypeEnum.EX_BET, exBetGameTemplateIdList);
            log.info("ex game template load end...");
        } catch (Exception e) {
            log.info("ex game template load error", e);
        }
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, BetSubTypeEnum betSubType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, betSubType, multipleRange.getStart(), multipleRange.getEnd(), IbongAdarnaBetRecordTemplateEsDo.class);
            if (!TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.IBONG_ADARNA, betType, betSubType, multipleRange)) {
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

    private void loadLocalCache(BetTypeEnum betType, BetSubTypeEnum betSubType, List<Long> templateIdList) {
        if (!supportWarmUpEnv()) {
            return;
        }
        TemplateTypeEnum templateType = this.getTemplateType();
        try {
            if (CollectionUtil.isNotEmpty(templateIdList)) {
                log.info("loadLocalCache start, betType: {}, betSubType:{},size: {}", betType, betSubType, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<IbongAdarnaBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.IBONG_ADARNA, idList, IbongAdarnaBetRecordTemplateEsDo.class);
                    templateList.forEach(templateItem -> {
                        betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem);
                    });
                });
                log.info("loadLocalCache success, betType: {},betSubType:{}, size: {}", betType, betSubType, templateIdList.size());
            }
        } catch (Exception e) {
            log.info("loadLocalCache error, betType: {},betSubType:{}", betType, betSubType, e);
        }
    }

    private void loadRedisCache(BetTypeEnum betType, List<Long> templateIdList) {
        if (!supportWarmUpEnv()) {
            return;
        }
        TemplateTypeEnum templateType = this.getTemplateType();
        try {
            Object hasLoad = redisTemplate.opsForValue().get(key);
            if (Objects.equals(hasLoad, BooleanEnum.TRUE.getCode())) {
                log.info("loadRedisCache fail, hasLoad. betType: {}, size: {}", betType, templateIdList.size());
                return;
            }
            distributeLock.lockThenRun(LockNameUtil.getBetRecordTemplatePreLoadLockName(this.getTemplateType()), 10, TimeUnit.SECONDS, () -> {
                log.info("loadRedisCache start, betType: {}, size: {}", betType, templateIdList.size());
                if (CollectionUtil.isNotEmpty(templateIdList)) {
                    List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                    multiList.forEach(idList -> {
                        List<IbongAdarnaBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(templateType, idList, IbongAdarnaBetRecordTemplateEsDo.class);
                        templateList.forEach(template -> {
                            betRecordTemplateEsService.putTemplateRedisCache(templateType, template.getId(), template);
                        });
                    });
                }
                redisTemplate.opsForValue().set(key, BooleanEnum.TRUE.getCode());
                log.info("loadRedisCache success, betType: {}, size: {}", betType, templateIdList.size());
            }, "lock fail");
        } catch (Exception e) {
            log.info("loadRedisCache error, betType: {}", betType, e);
        }
    }
}

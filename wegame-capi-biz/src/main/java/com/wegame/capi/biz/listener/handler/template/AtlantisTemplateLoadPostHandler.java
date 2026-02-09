package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.BooleanEnum;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.template.atlantis.AtlantisBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.*;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = ATLANTIS_PROP, havingValue = TRUE_VAL)
public class AtlantisTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    private static final String key = "load:template:redis:cache:Atlantis0";
    @Autowired
    AtlantisConfig atlantisConfig;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    BetRecordTemplateEsService<AtlantisBetRecordTemplateEsDo> betRecordTemplateEsService;

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.ATLANTIS;
    }

    protected AtlantisConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return atlantisConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.ATLANTIS)) {
            return;
        }
        if (StringUtils.equals(env, EnvEnum.LOCAL.getValue()) || StringUtils.equals(env, EnvEnum.DEV.getValue())) {
            return;
        }
        LinkedHashMap<String, LinkedHashMap<String, Integer>> templateRangeMap = this.getTemplateRangeConfig().getTemplateRangeMap();
        if (CollectionUtil.isEmpty(templateRangeMap)) {
            log.warn("AtlantisTemplateLoadPostHandler#handle templateRangeMap is empty");
            return;
        }

        templateRangeMap.forEach((String betSubTypeCode, LinkedHashMap<String, Integer> rangeWeightMap) -> {
            try {
                // normal 和 luck boy(EX_BET)一个模版，统一使用Normal
                BetSubTypeEnum betSubType = BetSubTypeEnum.valueOfCode(betSubTypeCode);
                AssertUtil.assertNotNull(betSubType, ErrorCode.ERR_PARAM, "no support betSubType of " + betSubTypeCode);
                // luckboy 不预热
                if (BetSubTypeEnum.EX_BET.equals(betSubType)) {
                    return;
                }
                log.info("{} template load start...", betSubTypeCode);
                List<Long> gameTemplateIdList = new LinkedList<>();
                rangeWeightMap.keySet().forEach((String rangeString) -> {
                    List<Long> idList = this.loadTemplateIdList(betSubType.getBetType(), betSubType, TemplateMultipleRange.build(rangeString));
                    if (CollectionUtils.isNotEmpty(idList)) {
                        gameTemplateIdList.addAll(idList);
                    }
                });
                this.loadLocalCache(betSubType.getBetType(), betSubType, gameTemplateIdList);
                log.info("{} template load end...", betSubTypeCode);
            } catch (Exception e) {
                log.error("template load error", e);
            }
        });
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, BetSubTypeEnum betSubType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("templateIdList load start, betType: {},betSubType:{}, range: {}-{}...", betType, betSubType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, betSubType, multipleRange.getStart(), multipleRange.getEnd(), AtlantisBetRecordTemplateEsDo.class);
            if (!TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.ATLANTIS, betType, betSubType, multipleRange)) {
                log.info("templateIdList load end, betType: {},betSubType:{}, range: {}-{}, size: {}， no load gameRecordTemplate", betType, betSubType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return null;
            }
            log.info("templateIdList load success, betType: {},betSubType:{}, range: {}-{}, size: {}", betType, betSubType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
            return betRecordTemplateIdList;
        } catch (Exception e) {
            log.info("templateIdList load error, betType: {},betSubType:{}, range: {}-{}", betType, betSubType, multipleRange.getStart(), multipleRange.getEnd(), e);
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
                log.info("loadLocalCache start, betType: {},betSubType:{}, size: {}", betType, betSubType, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<AtlantisBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.ATLANTIS, idList, AtlantisBetRecordTemplateEsDo.class);
                    templateList.forEach(templateItem -> betRecordTemplateEsService.putTemplateLocalCache(templateType, templateItem.getId(), templateItem));
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
                        List<AtlantisBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(templateType, idList, AtlantisBetRecordTemplateEsDo.class);
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

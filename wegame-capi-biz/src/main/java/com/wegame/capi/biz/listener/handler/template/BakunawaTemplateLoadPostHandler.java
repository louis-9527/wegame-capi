package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.template.bakunawa.BakunawaBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.BAKUNAWA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
@ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
public class BakunawaTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    private static final String key = "load:template:redis:cache:Bakunawa0";
    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    BetRecordTemplateEsService<BakunawaBetRecordTemplateEsDo> betRecordTemplateEsService;

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.BAKUNAWA;
    }

    protected BakunawaConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return bakunawaConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportGame(GameTypeEnum.BAKUNAWA)) {
            return;
        }
        BakunawaConfig.TemplateRangeConfig config = this.getTemplateRangeConfig();
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
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd(), BakunawaBetRecordTemplateEsDo.class);
            if (!TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.BAKUNAWA, betType, multipleRange)) {
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
                    List<BakunawaBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.BAKUNAWA, idList, BakunawaBetRecordTemplateEsDo.class);
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
                        List<BakunawaBetRecordTemplateEsDo> templateList = betRecordTemplateEsService.listByIdList(templateType, idList, BakunawaBetRecordTemplateEsDo.class);
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

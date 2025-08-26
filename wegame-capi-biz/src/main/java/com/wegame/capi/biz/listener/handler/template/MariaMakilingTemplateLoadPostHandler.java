package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 模版加载后处理执行器
 */
@Slf4j
@Component
public class MariaMakilingTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    private static final String key = "load:template:redis:cache:MariaMakiling2";
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.MARIA_MAKILING;
    }

    protected MariaMakilingConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return mariaMakilingConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportWarmUpEnv()) {
            log.warn("not support env: {}", env);
            return;
        }
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
                List<Long> idList = loadTemplateIdList(BetTypeEnum.BUY_BET, TemplateMultipleRange.build(key));
                if (CollectionUtils.isNotEmpty(idList)) {
                    templateIdList.addAll(idList);
                }
            });
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                loadLocalCache(BetTypeEnum.NORMAL_BET, templateIdList);
            } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
                loadRedisCache(BetTypeEnum.BUY_BET, templateIdList);
            } else {
                return;
            }
            log.info("{} game template load end...", betType);
        } catch (Exception e) {
            log.info("{} game template load error", betType, e);
        }
    }

    private List<Long> loadTemplateIdList(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("templateIdList load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd());
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
        if (CollectionUtil.isEmpty(templateIdList)) {
            log.warn("loadLocalCache fail, betType: {}, size is 0", betType);
            return;
        }
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("loadLocalCache start, betType: {}, size: {}", betType, templateIdList.size());
        List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
        multiList.forEach(idList -> {
            try {
                List<GameRecordTemplateDo> templateList = gameRecordTemplateService.listByBetRecordIdList(templateType, idList);
                idList.forEach(betRecordId -> {
                    gameRecordTemplateService.putLocalCache(templateType, betRecordId, templateList.stream()
                            .filter(template -> Objects.equals(template.getBetRecordId(), betRecordId))
                            .collect(Collectors.toList()));
                });
            } catch (Exception e) {
                log.info("loadLocalCache error, betType: {}, idListSize: {}", betType, idList.size(), e);
            }
        });
        log.info("loadLocalCache success, betType: {}, size: {}", betType, templateIdList.size());
    }

    private void loadRedisCache(BetTypeEnum betType, List<Long> templateIdList) {
        if (CollectionUtil.isEmpty(templateIdList)) {
            log.warn("loadRedisCache fail, betType: {}, size is 0", betType);
            return;
        }
        Object hasLoad = redisTemplate.opsForValue().get(key);
        if (Objects.equals(hasLoad, BooleanEnum.TRUE.getCode())) {
            log.info("loadRedisCache fail, hasLoad. betType: {}, size: {}", betType, templateIdList.size());
            return;
        }
        TemplateTypeEnum templateType = this.getTemplateType();
        try {
            distributeLock.lockThenRun(LockNameUtil.getBetRecordTemplatePreLoadLockName(this.getTemplateType()), 10, TimeUnit.SECONDS, () -> {
                log.info("loadRedisCache start, betType: {}, size: {}", betType, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    try {
                        List<GameRecordTemplateDo> templateList = gameRecordTemplateService.listByBetRecordIdList(templateType, idList);
                        idList.forEach(betRecordId -> {
                            gameRecordTemplateService.putRedisCache(templateType, betRecordId, templateList.stream()
                                    .filter(template -> Objects.equals(template.getBetRecordId(), betRecordId))
                                    .collect(Collectors.toList()));
                        });
                    } catch (Exception e) {
                        log.info("loadRedisCache error, betType: {}, idListSize: {}", betType, idList.size(), e);
                    }
                });
                redisTemplate.opsForValue().set(key, BooleanEnum.TRUE.getCode());
                log.info("loadRedisCache success, betType: {}, size: {}", betType, templateIdList.size());
            }, "lock fail");
        } catch (Exception e) {
            log.info("loadRedisCache error, betType: {}", betType, e);
        }
    }
}

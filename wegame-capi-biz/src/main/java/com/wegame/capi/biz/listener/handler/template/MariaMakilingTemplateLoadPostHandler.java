package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wegame.base.exception.LockException;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 模版预加载后处理执行器
 */
@Slf4j
@Component
public class MariaMakilingTemplateLoadPostHandler extends AbstractTemplateLoadPostHandler {
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;


    protected TemplateTypeEnum getTemplateType() {
        return TemplateTypeEnum.MARIA_MAKILING;
    }

    protected MariaMakilingConfig.TemplateRangeConfig getTemplateRangeConfig() {
        return mariaMakilingConfig.getTemplateRangeConfig();
    }

    @Override
    public void handle() {
        if (!supportWarmUpEnv()) {
            return;
        }
        if (!supportGame(GameTypeEnum.MARIA_MAKILING)) {
            return;
        }
        try {
            // todo maidong 加载redis缓存的时候需要抢锁，加载本地是缓存的时候不需要抢锁
//            distributeLock.lockThenRun(LockNameUtil.getBetRecordTemplatePreLoadLockName(this.getTemplateType()), 60, TimeUnit.SECONDS, () -> {
                try {
                    log.info("template load start...");
                    MariaMakilingConfig.TemplateRangeConfig config = this.getTemplateRangeConfig();
                    config.getMainGame().keySet().forEach((String key) -> loadTemplate(BetTypeEnum.NORMAL_BET, TemplateMultipleRange.build(key)));
                    config.getFreeGame().keySet().forEach((String key) -> loadTemplate(BetTypeEnum.BUY_BET, TemplateMultipleRange.build(key)));
                    log.info("template load end...");
                } catch (Exception e) {
                    log.error("template load error", e);
                }
//            }, "lock fail");
        } catch (LockException e) {
            log.warn("template load lock warn", e);
        } catch (Exception e) {
            log.error("template load error", e);
        }
    }

    private void loadTemplate(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        log.info("template load start, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateService.listIdByMultipleRange(templateType, betType, multipleRange.getStart(), multipleRange.getEnd());
            if (!TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.MARIA_MAKILING, betType, multipleRange)) {
                log.info("template load end, betType: {}, range: {}-{}, size: {}...， no load gameRecordTemplate", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return;
            }
            if (CollectionUtil.isNotEmpty(betRecordTemplateIdList)) {
                List<List<Long>> multiList = ListUtil.partition(betRecordTemplateIdList, 1000);
                multiList.forEach(idList -> {
                    List<GameRecordTemplateDo> templateList = gameRecordTemplateService.listByBetRecordIdList(templateType, idList);
                    idList.forEach(betRecordId -> {
                        if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                            gameRecordTemplateService.putLocalCache(templateType, betRecordId, templateList.stream()
                                    .filter(template -> Objects.equals(template.getBetRecordId(), betRecordId))
                                    .collect(Collectors.toList()));
                        } else {
                            gameRecordTemplateService.putRedisCache(templateType, betRecordId, templateList.stream()
                                    .filter(template -> Objects.equals(template.getBetRecordId(), betRecordId))
                                    .collect(Collectors.toList()));
                        }
                    });
                });
            }
            log.info("template load end, betType: {}, range: {}-{}, size: {}...", betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
        } catch (Exception e) {
            log.info("template load error, betType: {}, range: {}-{}...", betType, multipleRange.getStart(), multipleRange.getEnd());
        }
    }
}

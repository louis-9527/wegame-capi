package com.wegame.capi.biz.listener.runner;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.exception.LockException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.biz.service.template.GameRecordTemplateService;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.digest.Tiger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 投注记录模版预加载后处理抽象执行器
 */
@Slf4j
public abstract class AbstractBetRecordTemplatePreLoadPostRunner implements BusinessPostRunner {
    @Autowired
    DistributeLock distributeLock;
    @Autowired
    BetRecordTemplateService betRecordTemplateService;
    @Autowired
    GameRecordTemplateService gameRecordTemplateService;

    /**
     * 模版类型
     *
     * @return type
     */
    protected abstract TemplateTypeEnum getTemplateType();

    /**
     * 模版配置
     * // todo TemplateMultipleRange 抽取出来作为公共类，适配后续游戏
     *
     * @return map
     */
    protected abstract Map<String, MariaMakilingConfig.TemplateMultipleRange> getTemplateRangeConfigMap();

    protected abstract String getPostRunnerName();

    @Override
    public <T> T run() {
        TemplateTypeEnum templateType = this.getTemplateType();
        String postRunnerName = this.getPostRunnerName();
        try {
            distributeLock.lockThenRun(LockNameUtil.getBetRecordTemplatePreLoadLockName(this.getTemplateType()), 60, TimeUnit.SECONDS, () -> {
                try {
                    log.info("{} begin...", postRunnerName);
                    this.getTemplateRangeConfigMap().values().forEach(templateMultipleRange -> {
                        // 投注记录模版缓存 先失效，再加载
//                        betRecordTemplateService.evictListIdByMultipleRange(templateMultipleRange.getMultipleRangeStart(), templateMultipleRange.getMultipleRangeEnd(), templateType);
                        List<Long> betRecordTemplateIdList = betRecordTemplateService.listIdByMultipleRange(templateMultipleRange.getMultipleRangeStart(), templateMultipleRange.getMultipleRangeEnd(), templateType);
                        if (CollectionUtil.isNotEmpty(betRecordTemplateIdList)) {
                            // gameRecord模版缓存 先失效，再加载
                            betRecordTemplateIdList.forEach(betRecordTEmplateId -> {
//                                gameRecordTemplateService.evictListByBetRecordId(betRecordTEmplateId, templateType);
                                gameRecordTemplateService.listByBetRecordId(betRecordTEmplateId, templateType);
                            });
                        }
                    });
                    log.info("{} end...", postRunnerName);
                } catch (LockException e) {
                    // do nothing
                } catch (Exception e) {
                    log.error("{} error", postRunnerName, e);
                }
            }, null);
        } catch (Exception e) {
            log.error("{} error", postRunnerName, e);
        }
        return null;
    }
}

package com.wegame.capi.biz.listener.runner;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.base.domain.BizAlertMessage;
import com.wegame.base.exception.LockException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.LarkUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.biz.service.template.GameRecordTemplateService;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 投注记录模版预加载后处理抽象执行器
 */
@Slf4j
public abstract class AbstractBetRecordTemplatePreLoadPostRunner implements BusinessPostRunner {
    private static final String MESSAGE_FORMAT = "%s,投注模版预加载失败,模版倍率范围：%s，投注失败";
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.env}")
    private String env;
    @Value("${lark.warningRobot}")
    private String warningRobot;

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

    /**
     * @return runner name
     */
    protected abstract String getPostRunnerName();

    @Override
    public <T> T run() {
        String postRunnerName = this.getPostRunnerName();
        try {
            distributeLock.lockThenRun(LockNameUtil.getBetRecordTemplatePreLoadLockName(this.getTemplateType()), 60, TimeUnit.SECONDS, () -> {
                try {
                    log.info("{} begin...", postRunnerName);
                    this.getTemplateRangeConfigMap().values().forEach(this::preLoadBetRecordTemplate);
                    log.info("{} end...", postRunnerName);
                } catch (Exception e) {
                    log.error("{} error", postRunnerName, e);
                }
            }, "lock fail");
        } catch (LockException e) {
            log.warn("{} lock warn", postRunnerName, e);
        } catch (Exception e) {
            log.error("{} error", postRunnerName, e);
        }
        return null;
    }

    private void preLoadBetRecordTemplate(MariaMakilingConfig.TemplateMultipleRange templateMultipleRange) {
        TemplateTypeEnum templateType = this.getTemplateType();
        try {
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
        } catch (Exception e) {
            log.error("{} preLoadBetRecordTemplate error,range:{}", this.getPostRunnerName(), JSON.toJSONString(templateMultipleRange));
            this.warning(templateType, templateMultipleRange);
        }
    }

    public void warning(TemplateTypeEnum templateType, MariaMakilingConfig.TemplateMultipleRange templateMultipleRange) {
        LarkUtil.sendTextMsg(warningRobot, BizAlertMessage.builder()
                .applicationName(appName)
                .env(env)
                .traceId(ThreadMdcUtil.getTraceId())
                .body(String.format(MESSAGE_FORMAT,
                        templateType.name(),
                        JSON.toJSONString(templateType)))
                .build().buildMessage());

    }

}

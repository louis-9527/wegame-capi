package com.wegame.capi.scheduler.biz.common;

import com.alibaba.druid.util.StringUtils;
import com.wegame.base.domain.BizAlertMessage;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.LarkUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 告警定时任务
 */
@Slf4j
public abstract class AbstractAlertScheduler
        extends AbstractScheduler
        implements Runnable {

    private static final String MESSAGE_FORMAT = "%s，%s存在投注中的投注记录";
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.env}")
    private String env;
    @Value("${lark.warningRobot}")
    private String warningRobot;
    @Autowired
    BetRecordService betRecordService;

    public abstract GameTypeEnum getGameType();

    /**
     * 获取前一天，检查是否存储在投注中的投注记录，若存在，则告警通知
     */
    @Override
    public void run() {
        log.info("{} schedule run start...", taskName());
        if (StringUtils.equals(env, EnvEnum.LOCAL.getValue())) {
            return;
        }
        List<Long> idList = betRecordService.listId(BetRecordQuery.builder()
                .status(BetStatusEnum.ING.getCode())
                .startCreateTime(DateUtil.getYesterdayBegin().getTime())
                .endCreateTime(DateUtil.getYesterdayEnd().getTime())
                .limit(1)
                .build(), getGameType());
        if (CollectionUtils.isNotEmpty(idList)) {
            LarkUtil.sendTextMsg(warningRobot, BizAlertMessage.builder()
                    .applicationName(appName)
                    .env(env)
                    .traceId(ThreadMdcUtil.getTraceId())
                    .body(String.format(MESSAGE_FORMAT,
                            DateUtil.getWebDateString(DateUtil.getYesterdayBegin()),
                            getGameType().getName()))
                    .build().buildMessage());
        }
        log.info("{} schedule run end...", taskName());
    }
}

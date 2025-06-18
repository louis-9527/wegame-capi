package com.wegame.capi.scheduler.biz.common;

import com.alibaba.druid.util.StringUtils;
import com.wegame.base.domain.BizAlertMessage;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.LarkUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardGrantStatusEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 奖池中奖告警定时任务
 */
@Slf4j
public abstract class AbstractJackpotRewardGrantAlertScheduler
        extends AbstractScheduler
        implements Runnable {

    private static final String MESSAGE_FORMAT = "%s，%s存在待派奖的中奖记录";
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.env}")
    private String env;
    @Value("${lark.warningRobot}")
    private String warningRobot;

    @Autowired
    JackpotUserRewardRecordService jackpotUserRewardRecordService;

    public abstract JackpotTypeEnum getJackpotTypeEnum();

    /**
     * 查询中奖记录派奖派奖状态为（未派奖，派奖中)的记录，扭转为已派奖
     */
    @Override
    public void run() {
        log.info("{} schedule run start...", taskName());
        if (StringUtils.equals(env, EnvEnum.LOCAL.getValue())) {
            return;
        }
        List<Long> idList = jackpotUserRewardRecordService.listId(JackpotUserRewardRecordQuery.builder()
                .rewardGrantStatus(JackpotRewardGrantStatusEnum.NONE.getCode())
                .startCreateTime(DateUtil.getYesterdayBegin().getTime())
                .endCreateTime(DateUtil.getYesterdayEnd().getTime())
                .limit(1)
                .build(), getJackpotTypeEnum());
        if (CollectionUtils.isNotEmpty(idList)) {
            LarkUtil.sendTextMsg(warningRobot, BizAlertMessage.builder()
                    .applicationName(appName)
                    .env(env)
                    .traceId(ThreadMdcUtil.getTraceId())
                    .body(String.format(MESSAGE_FORMAT,
                            DateUtil.getWebDateString(DateUtil.getYesterdayBegin()),
                            this.getJackpotTypeEnum().getGameType().getName()))
                    .build().buildMessage());
        }
        log.info("{} schedule run end...", taskName());
    }
}

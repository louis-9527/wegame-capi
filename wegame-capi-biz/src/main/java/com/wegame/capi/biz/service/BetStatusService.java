package com.wegame.capi.biz.service;

import com.wegame.base.domain.BizAlertMessage;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.LarkUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletBetStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.shade.com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wegame.capi.biz.builder.req.BetStatusReqBuilder.buildReq;
import static com.wegame.capi.common.enums.SingleWalletBetStatusEnum.valueOfCode;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.betRecordStatusThreadPoolExecutor;

/**
 * 投注状态服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetStatusService {

    private static final long THREE_MINUTES = 3 * 60 * 1000;
    private static final String MESSAGE_FORMAT = "%s, 记录id：%s，投注失败";
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.env}")
    private String env;
    @Value("${lark.warningRobot}")
    private String warningRobot;
    @Autowired
    SingleWalletApi singleWalletApi;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    PulsarProducer pulsarProducer;

    /**
     * 扭转投注状态: 投注中 -> 投注成功/投注失败
     */
    public void convertBetStatus(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        log.info("convertBetStatus betRecordDo: {}", betRecordDo);
        if (!Objects.equals(BetStatusEnum.ING.getCode(), betRecordDo.getStatus())) {
            // 只处理投注中的投注记录
            log.error("betRecordDo status is int, betRecordId: {}", betRecordDo.getId());
            return;
        }
        if (System.currentTimeMillis() - betRecordDo.getCreateTime() >= THREE_MINUTES) {
            // 只处理3分钟以内的投注记录
            log.error("betRecordDo createTime is expired, betRecordId: {}", betRecordDo.getId());
            return;
        }
        // 控制20s不超过1个请求
        RateLimiter rateLimiter = RateLimiter.create(1.0 / 20.0);
        do {
            try {
                rateLimiter.acquire();
                log.info("convertBetStatus info, betRecordId: {}", betRecordDo.getId());
                // 查询投注记录的投注状态
                BetStatusRes betStatusRes = singleWalletApi.getBetStatus(buildReq(betRecordDo));
                AssertUtil.assertNotNull(betStatusRes, ErrorCode.ERR_INTER);
                SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
                AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid single wallet bet status code");
                BetStatusEnum betStatus = BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus);
                if (Objects.equals(BetStatusEnum.SUCCESS, betStatus)) {
                    handle(betRecordDo, gameType, betStatus, betStatusRes);
                    break;
                } else if (Objects.equals(BetStatusEnum.FAIL, betStatus)) {
                    log.warn("convertBetStatus warn bet fail, betRecordId: {}, betStatusRes: {}", betRecordDo.getId(), betStatusRes);
                    handle(betRecordDo, gameType, betStatus, betStatusRes);
                    break;
                } else {
                    log.warn("convertBetStatus warn, betRecordId: {}, betStatusRes: {}", betRecordDo.getId(), betStatusRes);
                    // do nothing
                }
            } catch (Exception e) {
                log.error("convertBetStatus error, betRecordId: {}, errMsg: {}", betRecordDo.getId(), e.getMessage(), e);
            }
        } while (System.currentTimeMillis() - betRecordDo.getCreateTime() <= THREE_MINUTES - 800);
    }

    @Async(betRecordStatusThreadPoolExecutor)
    public void asyncConvertBetStatus(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        convertBetStatus(betRecordDo, gameType);
    }

    private void handle(BetRecordDo betRecordDo, GameTypeEnum gameType, BetStatusEnum betStatus, BetStatusRes betStatusRes) {
        log.info("handle betStatus betRecordId: {}, betStatus: {}, betStatusRes: {}", betRecordDo.getId(), betStatus, betStatusRes);
        MessageId messageId = pulsarProducer.syncSendMessage(gameType,
                buildUpdateMessage(betRecordDo, betStatus, betStatusRes.getBetId()),
                100);
        if (Objects.isNull(messageId)) {
            log.error("handle betStatus betRecordId: {}, betStatus: {}, send message fail", betRecordDo.getId(), betStatus);
        }
        // 业务告警
        if (Objects.equals(BetStatusEnum.FAIL, betStatus)) {
            if (StringUtils.equals(env, EnvEnum.LOCAL.getValue())) {
                return;
            }
            LarkUtil.sendTextMsg(warningRobot, BizAlertMessage.builder()
                    .applicationName(appName)
                    .env(env)
                    .traceId(ThreadMdcUtil.getTraceId())
                    .body(String.format(MESSAGE_FORMAT,
                            gameType.getName(),
                            betRecordDo.getId()))
                    .build().buildMessage());
        }
    }

    private MQMessage<BetRecordDo> buildUpdateMessage(BetRecordDo betRecordDo, BetStatusEnum betStatus, String betId) {
        return MQMessage.buildMsg(String.valueOf(betRecordDo.getId()),
                MessageTypeEnum.UPDATE.getCode(),
                BetRecordDo.builder()
                        .id(betRecordDo.getId())
                        .betId(betId)
                        .status(betStatus.getCode())
                        .jackpot(betRecordDo.getJackpot())
                        .build());
    }
}
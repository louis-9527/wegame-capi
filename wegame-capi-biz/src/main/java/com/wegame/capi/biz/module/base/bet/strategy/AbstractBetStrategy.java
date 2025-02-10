package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;

import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象<br/>
 * 1、获取具体游戏
 * 2、
 */
@Component
public abstract class AbstractBetStrategy<T, R extends ToVoConverter> implements BetStrategy<T, R> {

    private static final int RETRY_TIMES = 3;
    @Autowired
    public BetRecordService betRecordService;
    @Autowired
    public SingleWalletService singleWalletService;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private PulsarProducer pulsarProducer;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public abstract Game getGame();

    public long generateRecordId() {
        return snowflakeIdGenerator.nextId();
    }

    /**
     * 保存投注记录并投注派彩
     *
     * @param betRecordDo
     * @return 用户余额
     */
    public Long saveBetRecordAndBetWithPayout(BetRecordDo betRecordDo) {
        Long betRecordId = betRecordService.add(betRecordDo, getGame().getGameInfo().getGameType());
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_INTER);
        BetSingleWalletResultDo betWithPayoutRes = singleWalletService.betWithPayout(betRecordDo);
        // 判断返回结果
        String betId = betWithPayoutRes.getBetId();
        // 更新记录
        BetRecordDo updateRecord = BetRecordDo.builder()
                .id(betRecordDo.getId())
                .playerId(betRecordDo.getPlayerId())
                .betId(betId)
                .status(betWithPayoutRes.getBetStatus().getCode())
                .build();
        // 异步更新
        betRecordService.asyncUpdate(updateRecord, getGame().getGameInfo().getGameType());
        return AmountUtil.toLong(betWithPayoutRes.getCredit());
    }

    /**
     * 保存投注记录并投注派彩
     *
     * @param betRecordDo
     * @return 用户余额
     */
    public Long saveBetRecordAndBetWithPayoutAsync(BetRecordDo betRecordDo, Boolean containFree) {
        if (containFree) {
            return this.saveBetRecordAndBetWithPayout(betRecordDo);
        } else {
            // 发送消息(新增)
            MQMessage<BetRecordDo> addMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.ADD.getCode(), betRecordDo);
            MessageId addMessageId = pulsarProducer.sendMessage(getGame().getGameInfo().getGameType(), addMqMessage, RETRY_TIMES);
            AssertUtil.assertNotNull(addMessageId, ErrorCode.ERR_INTER);
            // 调用单钱包
            BetSingleWalletResultDo betWithPayoutRes = singleWalletService.betWithPayout(betRecordDo);
            // 判断返回结果
            String betId = betWithPayoutRes.getBetId();
            // 更新记录
            BetRecordDo updateRecord = BetRecordDo.builder()
                    .id(betRecordDo.getId())
                    .playerId(betRecordDo.getPlayerId())
                    .betId(betId)
                    .status(betWithPayoutRes.getBetStatus().getCode())
                    .updateTime(DateUtil.getCurrentMilli())
                    .build();
            // 发送消息(更新)
            MQMessage<BetRecordDo> updateMqMessage = MQMessage.buildMsg(String.valueOf(betRecordDo.getId()), MessageTypeEnum.UPDATE.getCode(), updateRecord);
            pulsarProducer.sendMessage(getGame().getGameInfo().getGameType(), updateMqMessage, RETRY_TIMES);
            return AmountUtil.toLong(betWithPayoutRes.getCredit());
        }
    }
}

package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationCheckResultDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.manager.DynamicOperationManager;
import com.wegame.capi.biz.service.DynamicOperationEventConfigService;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 动态运营下注策略
 */
@Slf4j
@Service
public abstract class DynamicOperationBetStrategy<R extends ToVoConverter, BD, BR extends ToVoConverter, WD extends SymbolDo> extends AbstractBetStrategy<R, BD, BR> {
    @Autowired
    private DistributeLock distributeLock;
    @Autowired
    private DynamicOperationManager dynamicOperationManager;
    private static final String LOCK_FAIL_TIP = "Get lock[%s] fail";

    @Autowired
    private DynamicOperationEventDetailService dynamicOperationEventDetailService;
    @Autowired
    private DynamicOperationEventConfigService dynamicOperationEventConfigService;

    private static final double[] arr = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0,
            15.0, 20.0, 25.0, 30.0, 40.0, 50.0, 60.0, 70.0,
            80.0, 90.0, 100.0};


    @Override
    public BetResultDo<BR> bet(BetDo<BD> betDo) {
        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
            // 非免费轮投注
            return betForNonFreeWheel(betDo);
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheel(betDo);
        }
    }


    public BetResultDo<BR> betForNonFreeWheel(BetDo<BD> betDo) {
        DynamicOperationCheckResultDo<R> checkResult = this.checkEventBet(betDo);
        if (!checkResult.getEventResult()) {
            // 未命中干预事件,从结果中获取数据
            BetRecordDo<R> betRecordDo = checkResult.getBetRecordDo();
            return this.saveBetRecord(betRecordDo);
        } else {
            BetRecordDo<R> betRecordDo = checkResult.getBetRecordDo();
            // 调用派彩并返回记录
            BetResultDo<BR> brBetResultDo = this.saveBetRecord(betRecordDo);
            // 异步上报
            dynamicOperationManager.asyncEventReport(checkResult.getDynamicOperationEvent(), checkResult.getOperationRecordDetailId(), brBetResultDo.getBetId(), this.getGame().getGameInfo().getGameType());
            return brBetResultDo;
        }

    }


    private BetRecordDo<R> rBetRecordDo(BetDo<BD> betDo, GameEventDo<WD> gameEventDo) {
        //获取投注记录
        long betRecordId = generateBetRecordId();
        List<R> list = getGame().play(GamePlayDo.<WD>builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(this.getBetAndPayAmount(betDo).getKey())
                .betRecordId(betRecordId)
                .gameEventDo(gameEventDo)
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        // 构建投注记录
        return this.buildBetRecord(betRecordId, betDo, list);
    }


    private DynamicOperationCheckResultDo<R> checkEventBet(BetDo<BD> betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        String lockName = LockNameUtil.getDynamicOperationBetLockName(userInfo.getPlayerId(), betDo.getGameId());
        return distributeLock.lockThenCall(lockName, () -> {
            // 判断是否存在干预事件
            DynamicOperationEventDo eventDo = this.getEventDo(betDo);
            if (eventDo == null) {
                BetRecordDo<R> betRecordDo = this.rBetRecordDo(betDo, null);

                return DynamicOperationCheckResultDo.<R>builder()
                        .eventResult(Boolean.FALSE)
                        .betRecordDo(betRecordDo)
                        .build();
            }
            // 构建并获取Wheel
            List<WheelDo<WD>> wheelDos = this.getWheelDos(betDo, eventDo.getInterventionAmount());
            if (CollectionUtils.isEmpty(wheelDos)) {
                BetRecordDo<R> betRecordDo = this.rBetRecordDo(betDo, null);
                return DynamicOperationCheckResultDo.<R>builder()
                        .eventResult(Boolean.FALSE)
                        .betRecordDo(betRecordDo)
                        .build();
            }
            GameEventDo<WD> gameEventDo = new GameEventDo<>();
            gameEventDo.setWheelDos(wheelDos);
            gameEventDo.setEventId(eventDo.getEventId());
            gameEventDo.setOperatorRecordId(eventDo.getId());
            gameEventDo.setChannelId(eventDo.getChannelId());
            // 生成投注记录
            BetRecordDo<R> betRecordDo = this.rBetRecordDo(betDo, gameEventDo);
            betRecordDo.setIeId(eventDo.getEventId());
            // 构建并保存干预记录
            Long detailId = this.saveEventDetail(userInfo, betRecordDo, gameEventDo);

            return DynamicOperationCheckResultDo.<R>builder()
                    .eventResult(Boolean.TRUE)
                    .dynamicOperationEvent(eventDo)
                    .operationRecordDetailId(detailId)
                    .betRecordDo(betRecordDo)
                    .build();
        }, String.format(LOCK_FAIL_TIP, lockName));
    }

    public abstract BetResultDo<BR> saveBetRecord(BetRecordDo<R> rBetRecordDo);

    public abstract BetRecordDo<R> buildBetRecord(Long betRecordId, BetDo<BD> betDo, List<R> list);

    public abstract BetResultDo<BR> betForFreeWheel(BetDo<BD> betDo);


    public List<WheelDo<WD>> getWheelDos(BetDo<BD> betDo, Long interventionAmount) {
        //计算干预金额是投注金额的多少倍,派奖金额应该是betAmount+干预金额,
        Long betPayAmount = this.getBetAndPayAmount(betDo).getValue();
        long interveneBalance = interventionAmount + betPayAmount;

        //计算最大倍数
        double multiple = ((double) interveneBalance / betPayAmount);

        // 根据倍数找最接近的
        Double closest = findClosest(multiple);
        if (closest == null) {
            return null;
        }
        switch (betDo.getBetType()) {
            // 倍数计算存储时*100,查询时需要*100
            case NORMAL_BET:
                return this.normalPayMultipleConfig(0, closest);
            case BUY_BET:
                return this.normalPayMultipleConfig(1, closest);

            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
    }


    private DynamicOperationEventDo getEventDo(BetDo<BD> betDo) {

        UserInfo userInfo = UserInfoContext.getUserInfo();
        DynamicOperationQuery query = DynamicOperationQuery.builder()
                .playerName(userInfo.getPlayerName())
                .operatorGameId(this.getGame().getGameInfo().getId())
                .eventKind(this.eventKindEnum(betDo.getBetType()).getCode())
                .eventStatus(DynamicOperationEventStatusEnum.NONE.getCode())
                .build();
        return dynamicOperationManager.getBuyUserNameAndTime(query, this.getGame().getGameInfo().getGameType());
    }


    private Long saveEventDetail(UserInfo userInfo, BetRecordDo<R> betRecordDo, GameEventDo eventDo) {
        //扣减流水
        DynamicOperationEventDetailDo detailDo = new DynamicOperationEventDetailDo();
        detailDo.setId(generateBetRecordId());
        detailDo.setOperatorRecordId(eventDo.getOperatorRecordId());
        detailDo.setEventId(eventDo.getEventId());
        detailDo.setBetRecordId(betRecordDo.getId());
        detailDo.setPayOutAmount(betRecordDo.getPayOutAmount());
        detailDo.setBetTime(betRecordDo.getCreateTime());
        detailDo.setPayOutTime(betRecordDo.getCreateTime());
        detailDo.setPlayerName(userInfo.getPlayerName());
        detailDo.setPlayerId(userInfo.getPlayerId());
        detailDo.setUserId(userInfo.getId());
        detailDo.setChannelId(eventDo.getChannelId());
        long currentMilli = DateUtil.getCurrentMilli();
        detailDo.setCreateTime(currentMilli);
        detailDo.setUpdateTime(currentMilli);
        detailDo.setBetStatus(BetStatusEnum.ING);
        return dynamicOperationEventDetailService.add(detailDo, this.getGame().getGameInfo().getGameType());
    }

    public abstract Pair<Long, Long> getBetAndPayAmount(BetDo<BD> betDo);

    public abstract DynamicOperationEventKindEnum eventKindEnum(BetTypeEnum betType);

    public abstract List<WheelDo<WD>> convertWheelDoList(List<Object> wheelConfig);

    private List<WheelDo<WD>> normalPayMultipleConfig(Integer configType, double multiple) {
        DynamicOperationConfigQuery configQuery = DynamicOperationConfigQuery.builder()
                .gameId(this.getGame().getGameInfo().getGameType().getId())
                .configType(configType)
                .multiple(multiple).build();
        List<DynamicOperationConfigDo> configDetail = dynamicOperationEventConfigService.getConfigDetail(configQuery, this.getGame().getGameInfo().getGameType());

        if (CollectionUtils.isEmpty(configDetail)) {
            log.error("normalPayMultipleConfig result null,configType:{},multiple:{}", configType, multiple);
            return null;
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(configDetail.size());
        DynamicOperationConfigDo dynamicOperationConfigDo = configDetail.get(randomIndex);
        return this.convertWheelDoList(dynamicOperationConfigDo.getDetail());
    }

    public static Double findClosest(double input) {
        int left = 0;
        int right = arr.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] <= input) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result == -1 ? null : arr[result];
    }

}

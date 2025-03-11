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
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationCheckResultDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.manager.AbstractDynamicOperationManager;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventSubStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.dynamicEventThreadPoolExecutor;

/**
 * 动态运营下注策略
 */
@Slf4j
@Service
public abstract class DynamicOperationBetStrategy<R extends ToVoConverter, BD, BR extends ToVoConverter, WD extends SymbolDo> extends AbstractBetStrategy<R, BD, BR> {
    @Autowired
    private DistributeLock distributeLock;
    private static final String LOCK_FAIL_TIP = "Get lock[%s] fail";
    @Autowired
    @Qualifier(dynamicEventThreadPoolExecutor)
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private RedisTemplate redisTemplate;

    public abstract AbstractDynamicOperationManager getDynamicOperationManager();

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
            BetSingleWalletResultDo resultDo = saveBetRecordAndBetWithPayout(betRecordDo, betRecordDo.getGameRecordList().get(0).hasRewardFreeSpins());
            return this.buildResult(betRecordDo, resultDo);
        } else {
            BetRecordDo<R> betRecordDo = checkResult.getBetRecordDo();
            // 调用派彩
            BetSingleWalletResultDo resultDo = saveBetRecordAndBetWithPayout(betRecordDo, betRecordDo.getGameRecordList().get(0).hasRewardFreeSpins());
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            BetStatusEnum betStatusEnum = BetStatusEnum.FAIL;
            if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
                betStatusEnum = BetStatusEnum.SUCCESS;
            }
            // 命中的事件信息
            DynamicOperationEventDo eventDo = checkResult.getDynamicOperationEvent();
            // 异步上报、更新状态
            this.asyncEventDetailUpdate(eventDo, betStatusEnum, checkResult.getOperationRecordDetailId(), betRecordDo.getBetId(), checkResult.getEventAmount());
            return this.buildResult(betRecordDo, resultDo);
        }
    }

    public void asyncEventDetailUpdate(DynamicOperationEventDo eventDo, BetStatusEnum betStatus, Long detailId, String betId, Long eventAmount) {
        threadPoolExecutor.execute(() -> {
            // 判断是否完成
            DynamicOperationEventSubStatusEnum eventSubStatusEnum = null;
            DynamicOperationEventStatusEnum eventStatusEnum = DynamicOperationEventStatusEnum.NONE;
            if (DateUtil.getCurrentMilli() > eventDo.getExpiredTime()) {
                // 过期
                eventSubStatusEnum = DynamicOperationEventSubStatusEnum.FINISH_EXPIRED;
                eventStatusEnum = DynamicOperationEventStatusEnum.FINISH;
            }
            Long finishBonus = eventDo.getFinishBonus() + eventAmount;
            if (eventDo.getMinBonus() <= finishBonus) {
                eventSubStatusEnum = DynamicOperationEventSubStatusEnum.FINISH_NORMAL;
                eventStatusEnum = DynamicOperationEventStatusEnum.FINISH;
            }

            DynamicOperationEventDetailDo eventDetailUpdate = DynamicOperationEventDetailDo.builder()
                    .id(detailId)
                    .betStatus(betStatus)
                    .betId(betId)
                    .build();
            DynamicOperationEventDo eventDoUpdate = DynamicOperationEventDo.builder()
                    .id(eventDo.getId())
                    .finishBonus(BetStatusEnum.SUCCESS == betStatus ? finishBonus : null)
                    .eventStatus(eventStatusEnum)
                    .eventSubStatus(eventSubStatusEnum)
                    .build();


            getDynamicOperationManager().updateEventDetail(eventDetailUpdate, eventDoUpdate);
            // 执行上报
            if (DynamicOperationEventSubStatusEnum.finish(eventSubStatusEnum)) {
                // 如果完成执行上报
                getDynamicOperationManager().asyncReportEvent(eventDo.getId());
            }
        });
    }


    private BetRecordDo<R> rBetRecordDo(BetDo<BD> betDo, GameEventDo<WD> gameEventDo) {
        //获取投注记录
        long betRecordId = generateBetRecordId();
        Pair<Long, Long> betAndPayAmount = this.getBetAndPayAmount(betDo);
        List<R> list = getGame().play(GamePlayDo.<WD>builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betAndPayAmount.getLeft())
                .payAmount(betAndPayAmount.getValue())
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
                log.warn("DynamicOperationBetStrategy.getWheelDos is null,eventRecordId:{},InterventionAmount:{}", eventDo.getId(), eventDo.getInterventionAmount());
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
            gameEventDo.setGameId(eventDo.getGameId());
            // 生成投注记录
            BetRecordDo<R> betRecordDo = this.rBetRecordDo(betDo, gameEventDo);
            betRecordDo.setIeId(eventDo.getEventId());
            // 构建并保存干预记录
            Long detailId = this.saveEventDetail(userInfo, betRecordDo, gameEventDo);

            return DynamicOperationCheckResultDo.<R>builder()
                    .eventResult(Boolean.TRUE)
                    .dynamicOperationEvent(eventDo)
                    .operationRecordDetailId(detailId)
                    .eventAmount(betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount())
                    .betRecordDo(betRecordDo)
                    .build();
        }, String.format(LOCK_FAIL_TIP, lockName));
    }

    public abstract BetResultDo<BR> buildResult(BetRecordDo<R> rBetRecordDo, BetSingleWalletResultDo resultDo);

    public abstract BetRecordDo<R> buildBetRecord(Long betRecordId, BetDo<BD> betDo, List<R> list);

    public abstract BetResultDo<BR> betForFreeWheel(BetDo<BD> betDo);


    public List<WheelDo<WD>> getWheelDos(BetDo<BD> betDo, Long interventionAmount) {
        // 计算干预金额是投注金额的多少倍,派奖金额应该是betAmount+干预金额,
        /**
         * (x+y)/x <1.1
         * 下注金额 =  x
         * 赠送金额 =  y
         * 那么y < 0.1x 则赠送金额y小于下注金额x10% 就会出现赠送不出去情况
         * 每次下注最小为1,购买轮下注金额的35倍,如果赠送金额<3.5 就会出现无法赠送的情况
         */


        Long betPayAmount = this.getBetAndPayAmount(betDo).getValue();

        if (this.getBetAndPayAmount(betDo).getKey() <= this.getGame().getGameInfo().getGameType().getMinBetAmount()) {
            // 如果投注金额等于最小的投注金额,就需要判断是否能送出去
            if (interventionAmount < 0.1 * betPayAmount) {
                // 上报
                // this.getDynamicOperationManager().reportEvent();
            }
        }

        long interveneBalance = interventionAmount + betPayAmount;
        //计算最大倍数
        double multiple = ((double) interveneBalance / betPayAmount);
        // 根据倍数找最接近的
        Double closest = findClosest(multiple);
        if (closest == null) {
            return null;
        }
        switch (betDo.getBetType()) {
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
        return getDynamicOperationManager().getEffectedEvent(userInfo.getPlayerName(), betDo.getBetType());
    }


    private Long saveEventDetail(UserInfo userInfo, BetRecordDo<R> betRecordDo, GameEventDo eventDo) {
        //扣减流水
        DynamicOperationEventDetailDo detailDo = new DynamicOperationEventDetailDo();
        detailDo.setId(generateBetRecordId());
        detailDo.setEventRecordId(eventDo.getOperatorRecordId());
        detailDo.setEventId(eventDo.getEventId());
        detailDo.setBetRecordId(betRecordDo.getId());
        detailDo.setBetAmount(betRecordDo.getPayAmount());
        detailDo.setPayOutAmount(betRecordDo.getPayOutAmount());
        detailDo.setEventAmount(betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount());
        detailDo.setBetTime(betRecordDo.getCreateTime());
        detailDo.setPayOutTime(betRecordDo.getCreateTime());
        detailDo.setPlayerName(userInfo.getPlayerName());
        detailDo.setPlayerId(userInfo.getPlayerId());
        detailDo.setUserId(userInfo.getId());
        detailDo.setGameId(eventDo.getGameId());
        detailDo.setChannelId(eventDo.getChannelId());
        long currentMilli = DateUtil.getCurrentMilli();
        detailDo.setCreateTime(currentMilli);
        detailDo.setUpdateTime(currentMilli);
        detailDo.setBetStatus(BetStatusEnum.ING);
        return getDynamicOperationManager().addEventDetail(detailDo);
    }

    public abstract Pair<Long, Long> getBetAndPayAmount(BetDo<BD> betDo);


    public abstract List<WheelDo<WD>> convertWheelDoList(List<Object> wheelConfig);

    private List<WheelDo<WD>> normalPayMultipleConfig(Integer configType, double multiple) {
        DynamicOperationConfigQuery configQuery = DynamicOperationConfigQuery.builder()
                .gameId(this.getGame().getGameInfo().getGameType().getId())
                .configType(configType)
                .multiple(multiple).build();
        List<List<DynamicOperationConfigDo>> configDetailGroup = getDynamicOperationManager().getConfigDetail(configQuery);
        if (CollectionUtils.isEmpty(configDetailGroup)) {
            log.error("normalPayMultipleConfig result null,configType:{},multiple:{}", configType, multiple);
            return null;
        }
        // 获取分组下config配置,避免玩家相同倍数连续命中同样的滚轮
        String incrementKey = String.format(Constants.DYNAMIC_OPERATION_CONFIG_INCREMENT_KEY,
                this.getGame().getGameInfo().getId(),
                UserInfoContext.getUserInfo().getId(), configType, multiple);
        Long increment = redisTemplate.opsForValue().increment(incrementKey);
        if (increment >= configDetailGroup.size()) {
            increment = 1L;
            redisTemplate.opsForValue().set(incrementKey, increment);
        }
        List<DynamicOperationConfigDo> configDetail = configDetailGroup.get((int) (increment - 1));
        int randomIndex = ThreadLocalRandom.current().nextInt(configDetail.size());
        DynamicOperationConfigDo dynamicOperationConfigDo = configDetail.get(randomIndex);
        return this.convertWheelDoList(dynamicOperationConfigDo.getDetail());
    }

    public Double findClosest(double input) {
        if (input < 1.1) {
            return null;
        }
        List<Double> arr = getDynamicOperationManager().getConfigMultipleArray();
        int left = 0;
        int right = arr.size() - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr.get(mid) <= input) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result == -1 ? null : arr.get(result);
    }

}

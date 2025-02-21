package com.wegame.capi.biz.module.base.bet.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.manager.DynamicOperationManager;
import com.wegame.capi.biz.service.DynamicOperationEventConfigService;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 动态运营下注策略
 */
@Service
public abstract class DynamicOperationBetStrategy<R extends ToVoConverter, BD, BR extends ToVoConverter, WD extends SymbolDo> extends AbstractBetStrategy<R, BD, BR> {
    @Autowired
    private DynamicOperationManager dynamicOperationManager;

    @Autowired
    private DynamicOperationEventDetailService dynamicOperationEventDetailService;
    @Autowired
    private DynamicOperationEventConfigService dynamicOperationEventConfigService;


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
        UserInfo userInfo = UserInfoContext.getUserInfo();

        // 判断是否存在干预事件
        DynamicOperationEventDo eventDo = this.getEventDo(betDo);
        GameEventDo<WD> gameEventDo = new GameEventDo<>();
        if (eventDo != null) {
            //构建并获取Wheel
            gameEventDo.setWheelDos(this.getWheelDos(betDo, eventDo));
            gameEventDo.setEventId(eventDo.getEventId());
            gameEventDo.setOperatorRecordId(eventDo.getId());
        }
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

        // 构建投注记录并派奖
        BetRecordDo<R> rBetRecordDo = this.buildBetRecord(betRecordId, betDo, list);
        // 异步更新干预事件结果
        Long detailId = null;
        if (eventDo != null) {
            // 构建并保存干预记录
            detailId = this.saveEventDetail(userInfo, betRecordId, rBetRecordDo.getBetId(), rBetRecordDo.getPayOutAmount(), gameEventDo);
        }
        //调用派彩并返回记录
        BetResultDo<BR> brBetResultDo = this.saveBetRecord(rBetRecordDo);
        if (detailId != null) {
            // 异步上报
            dynamicOperationManager.asyncEventReport(eventDo, detailId, this.getGame().getGameInfo().getGameType());
        }
        return brBetResultDo;

    }

    public abstract BetResultDo<BR> saveBetRecord(BetRecordDo<R> rBetRecordDo);

    public abstract BetRecordDo<R> buildBetRecord(Long betRecordId, BetDo<BD> betDo, List<R> list);

    public abstract BetResultDo<BR> betForFreeWheel(BetDo<BD> betDo);


    public List<WheelDo<WD>> getWheelDos(BetDo<BD> betDo, DynamicOperationEventDo operationEventDo) {
        //计算干预金额是投注金额的多少倍,派奖金额应该是betAmount+干预金额,
        Long betPayAmount = this.getBetAndPayAmount(betDo).getValue();
        Long interveneBalance = operationEventDo.getBonusBalance() + betPayAmount;

        //计算最大倍数
        Integer multiple = Math.toIntExact((interveneBalance / betPayAmount) > 100 ? 100 : (interveneBalance / betPayAmount));
        switch (betDo.getBetType()) {
            case NORMAL_BET:
                return this.normalPayMultipleConfig(0, multiple);
            case BUY_BET:
                return this.normalPayMultipleConfig(1, multiple);

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


    private Long saveEventDetail(UserInfo userInfo, Long betRecordId, String betId, Long totalPayOutAmount, GameEventDo eventDo) {
        //扣减流水
        DynamicOperationEventDetailDo detailDo = new DynamicOperationEventDetailDo();
        detailDo.setId(generateBetRecordId());
        detailDo.setOperatorRecordId(eventDo.getOperatorRecordId());
        detailDo.setEventId(eventDo.getEventId());
        detailDo.setBetId(betId);
        detailDo.setBetRecordId(betRecordId);
        detailDo.setPayOutAmount(totalPayOutAmount);
        detailDo.setPlayerName(userInfo.getPlayerName());
        detailDo.setUserId(userInfo.getId());
        long currentMilli = DateUtil.getCurrentMilli();
        detailDo.setCreateTime(currentMilli);
        detailDo.setUpdateTime(currentMilli);
        detailDo.setBetStatus(BetStatusEnum.ING);
        return dynamicOperationEventDetailService.add(detailDo, this.getGame().getGameInfo().getGameType());
    }

    public abstract Pair<Long, Long> getBetAndPayAmount(BetDo<BD> betDo);

    public abstract DynamicOperationEventKindEnum eventKindEnum(BetTypeEnum betType);

    public abstract List<WheelDo<WD>> convertWheelDoList(String wheelConfig);

    private List<WheelDo<WD>> normalPayMultipleConfig(Integer configType, Integer multiple) {
        DynamicOperationConfigQuery configQuery = DynamicOperationConfigQuery.builder()
                .gameId(this.getGame().getGameInfo().getGameType().getId())
                .configType(configType)
                .multiple(10).build();
        List<DynamicOperationConfigDo> configDetail = dynamicOperationEventConfigService.getConfigDetail(configQuery, this.getGame().getGameInfo().getGameType());


        int randomIndex = ThreadLocalRandom.current().nextInt(configDetail.size());
        DynamicOperationConfigDo dynamicOperationConfigDo = configDetail.get(randomIndex);
        return this.convertWheelDoList(dynamicOperationConfigDo.getDetail());
    }


}

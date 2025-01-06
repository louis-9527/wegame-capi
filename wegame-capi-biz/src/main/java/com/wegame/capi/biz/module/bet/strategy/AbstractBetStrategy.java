package com.wegame.capi.biz.module.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.converter.GameRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.GameStatusEnum;
import com.wegame.capi.common.utils.GameAmountUtil;
import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象
 */
@Component
public abstract class AbstractBetStrategy<T, R extends Converter> implements BetStrategy<T, R> {

    @Autowired
    IdGeneratorHelper idGeneratorHelper;
    // todo 删除 依赖
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    public BetRecordService betRecordService;
    @Autowired
    private SingleWalletApi singleWalletApi;

    public abstract Game getGame();

    public BetRecordDo getBetRecord(BetDo<T> betReqDo, Long betAmount, Long payAmount, List<GameBetResultDetailDo> resultDetails) {
        //组装投注记录
        long id = idGeneratorHelper.nextId();
        return BetRecordDo.builder()
                .id(id)
                .pId(UserInfoContext.getUserInfo().getPlatformId())
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerId(UserInfoContext.getUserInfo().getPlayerId())
                .userId(Long.valueOf(UserInfoContext.getUserInfo().getId()))
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .gameId(betReqDo.getGameId())
                .betSerialNo(String.valueOf(id))
                .betType(betReqDo.getBetType().getCode())
                .betSubType(betReqDo.getBetSubType().getCode())
                .betAmount(betAmount)
                .payOutAmount(payAmount)
                .status(GameStatusEnum.ING.getCode())
                .detail(resultDetails)
                .build();
    }



    /**
     * 保存投注记录
     *
     * @return 玩家余额
     */
    public Long saveBetRecordAndPayOut(BetRecordDo betRecordDo) {
        //组装中奖数据
        BetRecordPo gameRecordPo = BetRecordConverter.doToPo(betRecordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(gameRecordPo, ErrorCode.ERR_INTER);
        gameRecordPo.setId(betRecordDo.getId());
        gameRecordPo.setBetSerialNo(String.valueOf(betRecordDo.getId()));
        BetRecordPo insert = mongoTemplate.insert(gameRecordPo);
        //调用单钱包  注释规范 //后加空格
        BetWithPayoutRes betWithPayoutRes = new BetWithPayoutRes();// this.betWithPayout(gameRecordPo);
        //判断返回结果
        String betId = betWithPayoutRes.getBetId();
        Query query = new Query(Criteria.where("id").is(insert.getId()));
        // 构建更新操作
        Update update = new Update();
        update.set("betId", betId);
        update.set("status", GameStatusEnum.SUCCESS.getCode());
        update.set("updateTime", new Date());
        //更新记录
        mongoTemplate.updateFirst(query, update, BetRecordPo.class);
        return GameAmountUtil.toLong(betWithPayoutRes.getCredit());
    }

    /**
     * 调用单钱包
     *
     * @param gameRecordPo
     * @return
     */

    private BetWithPayoutRes betWithPayout(BetRecordPo gameRecordPo) {
        //调用单钱包
        BetWithPayoutReq payoutReq = BetWithPayoutReq.builder()
                .pId(gameRecordPo.getPId())
                .channelId(gameRecordPo.getChannelId())
                .playerName(gameRecordPo.getPlayerName())
                .refId(gameRecordPo.getBetSerialNo())
                .createTime(DateUtil.toUTC(new Date()))
                .betAmount(GameAmountUtil.toBigDecimal(gameRecordPo.getBetAmount()))
                .payoutAmount(GameAmountUtil.toBigDecimal(gameRecordPo.getPayOutAmount()))
                .betDetails(BetWithPayoutReq.BetDetails.builder()
                        .gameId(gameRecordPo.getGameId())
                        .roundNo(gameRecordPo.getBetSerialNo())
                        .wagersDate(DateUtil.getCurrentTimestamp())
                        .settlementDate(DateUtil.getCurrentTimestamp())
                        .isFreeRound(BetTypeEnum.FREE_SPINS_BET == BetTypeEnum.valueOfCode(gameRecordPo.getBetType()))
                        .jackpotDetails(new BetWithPayoutReq.BetDetails.JackpotDetails())
                        .build())
                .build();
        return singleWalletApi.betWithPayout(payoutReq);
    }
}

package com.wegame.capi.biz.module.bet.strategy;

import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.GameStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象<br/>
 * 1、获取具体游戏
 * 2、
 */
@Component
public abstract class AbstractBetStrategy<T, R extends Converter> implements BetStrategy<T, R> {


    @Autowired
    public IdGeneratorHelper idGeneratorHelper;
    @Autowired
    public BetRecordService betRecordService;
    @Autowired
    private SingleWalletService singleWalletService;

    public abstract Game getGame();

    /**
     * 调用单钱包
     *
     * @param gameRecordPo
     * @return
     */

    private BetWithPayoutRes betWithPayout(BetRecordPo gameRecordPo) {
        // 调用单钱包
        BetWithPayoutReq payoutReq = BetWithPayoutReq.builder()
                .pId(gameRecordPo.getPId())
                .channelId(gameRecordPo.getChannelId())
                .playerName(gameRecordPo.getPlayerName())
                .refId(gameRecordPo.getBetSerialNo())
                .createTime(DateUtil.toUTC(new Date()))
                .betAmount(AmountUtil.toBigDecimal(gameRecordPo.getBetAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(gameRecordPo.getPayOutAmount()))
                .betDetails(BetWithPayoutReq.BetDetails.builder()
                        .gameId(gameRecordPo.getGameId())
                        .roundNo(gameRecordPo.getBetSerialNo())
                        .wagersDate(DateUtil.getCurrentTimestamp())
                        .settlementDate(DateUtil.getCurrentTimestamp())
                        .isFreeRound(BetTypeEnum.FREE_SPINS_BET == BetTypeEnum.valueOfCode(gameRecordPo.getBetType()))
                        .jackpotDetails(new BetWithPayoutReq.BetDetails.JackpotDetails())
                        .build())
                .build();
        return singleWalletService.betWithPayout(payoutReq);
    }
}

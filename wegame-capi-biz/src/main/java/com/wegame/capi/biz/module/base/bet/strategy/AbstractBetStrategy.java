package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.common.utils.AmountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象<br/>
 * 1、获取具体游戏
 * 2、
 */
@Component
public abstract class AbstractBetStrategy<T, R extends ToVoConverter> implements BetStrategy<T, R> {

    @Autowired
    public IdGeneratorHelper idGeneratorHelper;
    @Autowired
    public BetRecordService betRecordService;
    @Autowired
    private SingleWalletService singleWalletService;

    public abstract Game getGame();

    public long generateRecordId() {
        return idGeneratorHelper.nextId();
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

}

package com.wegame.capi.biz.module.bet.strategy;

import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.common.enums.BetStatusEnum;
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

    public long getRecordId() {
        return idGeneratorHelper.nextId();
    }

    /**
     * 保存投注记录并派奖
     *
     * @param betRecordDo
     * @return
     */
    public Long saveBetRecordAndPayOut(BetRecordDo betRecordDo) {
        betRecordService.add(betRecordDo);
        // 调用单钱包
        SingleWalletBetWithPayoutRes betWithPayoutRes = SingleWalletBetWithPayoutRes.builder().betStatus(BetStatusEnum.SUCCESS).build();//singleWalletService.betWithPayout(betRecordDo);
        // 判断返回结果
        String betId = betWithPayoutRes.getBetId();
        // 更新记录
        BetRecordDo updateRecord = BetRecordDo.builder().id(betRecordDo.getId()).betId(betId).status(betWithPayoutRes.getBetStatus().getCode()).build();
        betRecordService.update(updateRecord);
        return AmountUtil.toLong(betWithPayoutRes.getCredit());
    }

}

package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousBuyFreeSpinsBetDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousBuyFreeSpinsBetResultDo;
import com.wegame.capi.common.utils.GameAmountUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mariaMakilingBuyFreeSpinsBetStrategy")
public class MariaMakilingBuyFreeSpinsBetStrategy
        extends AbstractMariaMakilingBetStrategy<MysteriousBuyFreeSpinsBetDo, MysteriousBuyFreeSpinsBetResultDo> {

    @Override
    public BetResultDo<MysteriousBuyFreeSpinsBetResultDo> bet(BetDo<MysteriousBuyFreeSpinsBetDo> betDo) {
        // todo 判断是否免费轮 标识

        // 获取轮盘结果,必须包含免费轮
        GameBetResultDetailDo betResult = super.getResultDetail(betDo.getGameId(), betDo.getBetType(), betDo.getBetSubType(), betDo.getBetDetail().getBetAmount());
        // 获取免费轮
        List<GameBetResultDetailDo> resultDetails = super.getResultDetail(betDo.getGameId(), betDo.getBetType(), betDo.getBetSubType(), betDo.getBetDetail().getBetAmount(), Boolean.TRUE);
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(resultDetails.size());
        for (GameBetResultDetailDo resultDetail : resultDetails) {
            if (resultDetail.getRewardAmount() != null) {
                rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            }
            freeSpinIds.add(resultDetail.getId());
        }

        resultDetails.add(betResult);
        //保存投注记录并派奖
        BetRecordDo betRecord = super.getBetRecord(betDo, betDo.getBetDetail().getPayAmount(), rewardAmount, resultDetails);
        Long credit = super.saveBetRecordAndPayOut(betRecord);
        return BetResultDo.<MysteriousBuyFreeSpinsBetResultDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .credit(credit)
                .resultDetail(MysteriousBuyFreeSpinsBetResultDo.builder()
                        .freeSpinIds(freeSpinIds)
                        .resultDetail(betResult)
                        .build())
                .build();
    }
}

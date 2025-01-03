package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.biz.domain.business.bet.GameBetRecordDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;
import com.wegame.capi.biz.domain.business.bet.MysteriousBuyFreeSpinsBetResultDo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mariaMakilingBuyFreeSpinsBetStrategy")
public class MariaMakilingBuyFreeSpinsBetStrategy extends AbstractMariaMakilingBetStrategy<BetDo.MysteriousBuyFreeSpinsBetDo, MysteriousBuyFreeSpinsBetResultDo> {

    @Override
    public BetResultDo<MysteriousBuyFreeSpinsBetResultDo> bet(BetDo<BetDo.MysteriousBuyFreeSpinsBetDo> betDo) {
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
        GameBetRecordDo betRecord = super.getBetRecord(betDo, betDo.getBetDetail().getPayAmount(), rewardAmount, resultDetails);
        super.saveBetRecordAndPayOut(betRecord);
        return BetResultDo.<MysteriousBuyFreeSpinsBetResultDo>builder()
                .gameRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(MysteriousBuyFreeSpinsBetResultDo.builder()
                        .freeSpinIds(freeSpinIds)
                        .resultDetailDo(betResult)
                        .build())
                .build();
    }
}

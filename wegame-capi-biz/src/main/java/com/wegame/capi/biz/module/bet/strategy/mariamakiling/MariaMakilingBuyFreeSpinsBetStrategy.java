package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBuyFreeSpinsBetDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingGameRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBuyFreeSpinsBetResultDo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mariaMakilingBuyFreeSpinsBetStrategy")
public class MariaMakilingBuyFreeSpinsBetStrategy extends AbstractMariaMakilingBetStrategy<MariaMakilingBuyFreeSpinsBetDo, MariaMakilingBuyFreeSpinsBetResultDo> {

    private final IdGeneratorHelper idGeneratorHelper;

    public MariaMakilingBuyFreeSpinsBetStrategy(IdGeneratorHelper idGeneratorHelper) {
        super();
        this.idGeneratorHelper = idGeneratorHelper;
    }

    @Override
    public BetResultDo<MariaMakilingBuyFreeSpinsBetResultDo> bet(BetDo<MariaMakilingBuyFreeSpinsBetDo> betDo) {
        MariaMakilingBuyFreeSpinsBetDo betDetail = betDo.getBetDetail();
        if (betDetail.getBetRecordId() != null && betDetail.getFreeSpinsId() != null) {
            //查询免费轮详情
            MariaMakilingGameRecordDetailDo gameBetResultDetailDo = betRecordService.getResultDetail(betDetail.getBetRecordId(), betDetail.getFreeSpinsId());
            return BetResultDo.<MariaMakilingBuyFreeSpinsBetResultDo>builder()
                    .betRecordId(betDetail.getBetRecordId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .resultDetail(MariaMakilingBuyFreeSpinsBetResultDo.builder()
                            .resultDetail(gameBetResultDetailDo)
                            .build())
                    .build();
        }
        // 获取轮盘结果,必须包含免费轮
        MariaMakilingGameRecordDetailDo betResult = super.getGameBetResult(betDo.getBetType(), betDo.getBetSubType(), betDo.getBetDetail().getBetAmount());
        // 获取免费轮
        List<MariaMakilingGameRecordDetailDo> resultDetails = super.getGameBetResultList(betDo.getBetType(), betDo.getBetSubType(), betDo.getBetDetail().getBetAmount());
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(resultDetails.size());
        for (MariaMakilingGameRecordDetailDo resultDetail : resultDetails) {
            if (resultDetail.getRewardAmount() != null) {
                rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            }
            freeSpinIds.add(resultDetail.getId());
        }

        resultDetails.add(betResult);
        // 保存投注记录并派奖
        long betRecordId = idGeneratorHelper.nextId();
        BetRecordDo betRecord = BetRecordBuilder.build(betDo, betRecordId, betDo.getBetDetail().getPayAmount(), rewardAmount, resultDetails);
        Long credit = super.saveBetRecordAndPayOut(betRecord);
        return BetResultDo.<MariaMakilingBuyFreeSpinsBetResultDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .credit(credit)
                .resultDetail(MariaMakilingBuyFreeSpinsBetResultDo.builder()
                        .freeSpinIds(freeSpinIds)
                        .resultDetail(betResult)
                        .build())
                .build();
    }
}

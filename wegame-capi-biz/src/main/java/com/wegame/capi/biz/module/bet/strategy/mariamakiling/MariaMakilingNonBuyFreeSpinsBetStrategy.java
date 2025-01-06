package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingNonBuyFreeSpinsBetDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingNonBuyFreeSpinsBetResultDo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mariaMakilingNonBuyFreeSpinsBetStrategy")
public class MariaMakilingNonBuyFreeSpinsBetStrategy
        extends AbstractMariaMakilingBetStrategy<MariaMakilingNonBuyFreeSpinsBetDo, MariaMakilingNonBuyFreeSpinsBetResultDo> {

    @Override
    public BetResultDo<MariaMakilingNonBuyFreeSpinsBetResultDo> bet(BetDo<MariaMakilingNonBuyFreeSpinsBetDo> betDo) {
        // todo 判断是否免费轮 标识
        MariaMakilingNonBuyFreeSpinsBetDo betDetail = betDo.getBetDetail();
        if (betDetail.getBetRecordId() != null && betDetail.getFreeSpinsId() != null) {
            // 查询免费轮详情
            GameBetResultDetailDo gameBetResultDetailDo = super.queryBetFreeSpin(betDetail.getBetRecordId(), betDetail.getFreeSpinsId());
            return BetResultDo.<MariaMakilingNonBuyFreeSpinsBetResultDo>builder()
                    .betRecordId(betDetail.getBetRecordId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .resultDetail(MariaMakilingNonBuyFreeSpinsBetResultDo.builder()
                            .resultDetail(gameBetResultDetailDo)
                            .build())
                    .build();
        }

        Long recordId = Long.valueOf(betDo.getBetDetail().getBetRecordId());
        BetRecordDo betRecordDo = super.betRecordService.get(recordId);
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        // todo 核销：检查获得免费轮标识是否被核销
        // 获取游戏结果
        List<GameBetResultDetailDo> resultDetails = super.getResultDetail(betDo.getGameId(), betDo.getBetType(), betDo.getBetSubType(), 0L, Boolean.TRUE);
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(resultDetails.size());
        for (GameBetResultDetailDo resultDetail : resultDetails) {
            if (resultDetail.getRewardAmount() != null) {
                rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            }
            freeSpinIds.add(resultDetail.getId());
        }
        // 获取投注结果
        BetRecordDo betRecord = super.getBetRecord(betDo, 0L, rewardAmount, resultDetails);
        // 保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        return BetResultDo.<MariaMakilingNonBuyFreeSpinsBetResultDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(MariaMakilingNonBuyFreeSpinsBetResultDo.builder().freeSpinIds(freeSpinIds).build())
                .build();
    }
}

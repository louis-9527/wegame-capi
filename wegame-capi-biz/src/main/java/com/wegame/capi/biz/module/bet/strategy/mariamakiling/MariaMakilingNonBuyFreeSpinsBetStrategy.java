package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.biz.domain.business.bet.GameBetRecordDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;
import com.wegame.capi.biz.domain.business.bet.MysteriousNonBuyFreeSpinsBetResultDo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordPo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mariaMakilingNonBuyFreeSpinsBetStrategy")
public class MariaMakilingNonBuyFreeSpinsBetStrategy 
        extends AbstractMariaMakilingBetStrategy<BetDo.MysteriousNonBuyFreeSpinsBetDo, MysteriousNonBuyFreeSpinsBetResultDo> {

    @Override
    public BetResultDo<MysteriousNonBuyFreeSpinsBetResultDo> bet(BetDo<BetDo.MysteriousNonBuyFreeSpinsBetDo> betDo) {
        Long recordId = Long.valueOf(betDo.getBetDetail().getGameRecordId());
        GameRecordPo gameRecord = super.mongoTemplate.findById(recordId, GameRecordPo.class);
        AssertUtil.assertNotNull(gameRecord, ErrorCode.ERR_PARAM);

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
        //获取投注结果
        GameBetRecordDo betRecord = super.getBetRecord(betDo, 0L, rewardAmount, resultDetails);
        //保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        return BetResultDo.<MysteriousNonBuyFreeSpinsBetResultDo>builder()
                .gameRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(MysteriousNonBuyFreeSpinsBetResultDo.builder().freeSpinIds(freeSpinIds).build())
                .build();
    }
}

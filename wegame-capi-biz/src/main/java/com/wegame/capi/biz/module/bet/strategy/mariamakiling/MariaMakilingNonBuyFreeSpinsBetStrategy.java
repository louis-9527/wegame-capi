package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousNonBuyFreeSpinsBetDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousNonBuyFreeSpinsBetResultDo;
import com.wegame.capi.biz.service.BetRecordService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mariaMakilingNonBuyFreeSpinsBetStrategy")
public class MariaMakilingNonBuyFreeSpinsBetStrategy
        extends AbstractMariaMakilingBetStrategy<MysteriousNonBuyFreeSpinsBetDo, MysteriousNonBuyFreeSpinsBetResultDo> {

    @Override
    public BetResultDo<MysteriousNonBuyFreeSpinsBetResultDo> bet(BetDo<MysteriousNonBuyFreeSpinsBetDo> betDo) {
        // todo 判断是否免费轮 标识


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
        //获取投注结果
        BetRecordDo betRecord = super.getBetRecord(betDo, 0L, rewardAmount, resultDetails);
        //保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        return BetResultDo.<MysteriousNonBuyFreeSpinsBetResultDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(MysteriousNonBuyFreeSpinsBetResultDo.builder().freeSpinIds(freeSpinIds).build())
                .build();
    }
}

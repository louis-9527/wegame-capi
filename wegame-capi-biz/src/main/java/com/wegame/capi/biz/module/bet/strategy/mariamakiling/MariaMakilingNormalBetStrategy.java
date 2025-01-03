package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousNormalBetDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousNormalBetResultDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import org.springframework.stereotype.Component;
@Component("mariaMakilingNormalBetStrategy")
public class MariaMakilingNormalBetStrategy
        extends AbstractMariaMakilingBetStrategy<MysteriousNormalBetDo, MysteriousNormalBetResultDo> {
    @Override
    public BetResultDo<MysteriousNormalBetResultDo> bet(BetDo<MysteriousNormalBetDo> betDo) {
        // 获取游戏结果
        GameBetResultDetailDo resultDetail = super.getResultDetail(betDo.getGameId(), betDo.getBetType(), betDo.getBetSubType(), betDo.getBetDetail().getBetAmount());
        // 生成投注结果
        BetRecordDo betRecord = super.getBetRecord(betDo, resultDetail.getBetAmount(), resultDetail.getRewardAmount(), Lists.newArrayList(resultDetail));
        // 保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        // 组装返回结果
        return BetResultDo.<MysteriousNormalBetResultDo>builder()
                .gameRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(MysteriousNormalBetResultDo.builder()
                        .resultDetail(resultDetail)
                        .build())
                .build();
    }
}

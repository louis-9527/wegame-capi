package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.module.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractMariaMakilingBetStrategy<T, R extends Converter> extends AbstractBetStrategy<T, R> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;

    @Override
    public Game getGame() {
        return mariaMakilingGame;
    }


    //////////////////////  =============
    //    /**
    //     * 获取单个结果
    //     *
    //     * @param betType
    //     * @param betSubType
    //     * @param betAmount
    //     * @return
    //     */
    //    public MariaMakilingBetRecordDetailDo getGameBetResult(BetTypeEnum betType, BetSubTypeEnum betSubType, Long betAmount) {
    //        List<MariaMakilingBetRecordDetailDo> resultDetail = this.getGameBetResult(betType, betSubType, betAmount, false);
    //        return resultDetail.get(0);
    //    }
    //
    //    /**
    //     * 获取多个游戏结果
    //     *
    //     * @param betType       游戏类型
    //     * @param betSubType    游戏子类型
    //     * @param betAmount     投注金额
    //     * @param freeSpinsFlag 是否免费轮,如果是怎会获取多条记录
    //     * @return
    //     */
    //    public List<MariaMakilingBetRecordDetailDo> getGameBetResult(BetTypeEnum betType, BetSubTypeEnum betSubType, Long betAmount, Boolean freeSpinsFlag) {
    //        GamePlayDo gamePlay = GamePlayDo.builder()
    //                .gameType(this.mariaMakilingGame.getGameInfo().getGameType())
    //                .betType(betType)
    //                .betSubType(betSubType)
    //                .betAmount(betAmount)
    //                .freeSpinsFlag(freeSpinsFlag)
    //                .build();
    //        GameRecordDo gamePlayResult = getGame().play(gamePlay);
    //        AssertUtil.assertNotNull(gamePlayResult, ErrorCode.ERR_INTER);
    //        // 滚轮中奖列表
    //        List<WheelRewardDo> wheelRewardList = gamePlayResult.getWheelRewardList();
    //        List<MariaMakilingBetRecordDetailDo> resultDetails = new ArrayList<>();
    //        wheelRewardList.forEach(rewardResultDo -> {
    //            resultDetails.add(this.buildBetResult(rewardResultDo, betAmount));
    //        });
    //        return resultDetails;
    //    }
    //
    //
    //    /**
    //     * 生成中奖线
    //     *
    //     * @param rewardLineList
    //     * @return
    //     */
    //    private List<MariaMakilingBetRecordDetailDo.RewardLineResultDo> getRewardLineResults(List<RewardLineDo> rewardLineList) {
    //        if (CollectionUtils.isEmpty(rewardLineList)) {
    //            return new ArrayList<>();
    //        }
    //        List<MariaMakilingBetRecordDetailDo.RewardLineResultDo> rewardResults = new ArrayList<>();
    //        rewardLineList.forEach(tt ->
    //                rewardResults.add(MariaMakilingBetRecordDetailDo.RewardLineResultDo.builder()
    //                        .symbol(tt.getSymbol())
    //                        .odds(tt.getOdds())
    //                        .multiple(tt.getMultiple())
    //                        .rewardAmount(tt.getRewardAmount())
    //                        .rewardIndexList(tt.getSymbolIndexList())
    //                        .rewardSymbolList(tt.getSymbolList())
    //                        .build()));
    //        return rewardResults;
    //    }
    //
    //
    //    private MariaMakilingBetRecordDetailDo buildBetResult(WheelRewardDo rewardResultDo, Long betAmount) {
    //        MariaMakilingBetRecordDetailDo build = MariaMakilingBetRecordDetailDo.builder()
    //                .id(rewardResultDo.getWheelIndex())
    //                .gameResult(rewardResultDo.getWheel().getSymbolArr())
    //                .betAmount(betAmount)
    //                .build();
    //        RewardResultDo rewardResult = rewardResultDo.getRewardResult();
    //        if (rewardResult != null) {
    //            build.setRewardAmount(rewardResult.getTotalRewardAmount());
    //            build.setMultiple(rewardResult.getTotalMultiple());
    //            build.setPrizeType(rewardResult.getRewardSubType());
    //            build.setFreeSpinsFlag(rewardResult.isFreeSpin());
    //            build.setRewardResults(this.getRewardLineResults(rewardResult.getRewardLineList()));
    //        }
    //        return build;
    //    }
}

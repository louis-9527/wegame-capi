package com.wegame.capi.biz.module.malakas.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MalakasBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MalakasBetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.malakas.game.MalakasGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Malakas游戏投注抽象接口
 */
public abstract class AbstractMalakasBetStrategy extends AbstractBetStrategy<MalakasBetDo, MalakasBetRecordDetailDo> {

    @Autowired
    MalakasGame malakasGame;

    @Override
    public Game<MalakasGameRecordDo> getGame() {
        return malakasGame;
    }

    @Override
    public BetResultDo<MalakasBetRecordDetailDo> bet(BetDo<MalakasBetDo> betDo) {
        if (BetSubTypeEnum.isFreeSpins(betDo.getBetSubType().getCode())) {
            // 免费轮投注
            return betForFreeSpins(betDo);
        } else {
            // 非免费轮投注
            return betForNonFreeSpins(betDo);
        }
    }

    /**
     * 免费轮投注
     *
     * @param betDo
     * @return
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForFreeSpins(BetDo<MalakasBetDo> betDo) {
        MalakasBetDo betDetail = betDo.getBetDetail();
        UserInfo userInfo = UserInfoContext.getUserInfo();

        BetRecordDo<MalakasBetRecordDetailDo> betRecordDo = betRecordService.get(betDetail.getBetRecordId(), userInfo.getPlayerId(), getGame().getGameInfo().getGameType());
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);

        MalakasBetRecordDetailDo betRecordDetailDo = betRecordDo.getDetail();
        AssertUtil.assertNotNull(betRecordDetailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotEmpty(betRecordDetailDo.getGameRecordList(), ErrorCode.ERR_PARAM);

        List<MalakasGameRecordDo> gameRecordList = betRecordDetailDo.getGameRecordList();
        // 匹配-免费轮游戏记录
        List<MalakasGameRecordDo> gameRecord = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getBatchId(), betDetail.getFreeSpinsId()))
                .collect(Collectors.toList());


        return BetResultDo.<MalakasBetRecordDetailDo>builder()
                .betRecordId(betDetail.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(gameRecord.stream().filter(item -> item.getRewardResult() != null)
                        .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                        .sum())
                .resultDetail(MalakasBetRecordDetailDo.builder().gameRecordList(gameRecord).build())
                .build();
    }

    /**
     * 非免费轮投注
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForNonFreeSpins(BetDo<MalakasBetDo> betDo) {
        long betRecordId = generateRecordId();
        List<MalakasGameRecordDo> list = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .betRecordId(betRecordId)
                .freeSpinsFlag(false) // todo fix
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);

        MalakasGameRecordDo malakasGameRecordDo = list.get(0);
        // 过来.所有batchId 相同的数据
        List<MalakasGameRecordDo> filterList = list.stream().filter(tt -> malakasGameRecordDo.getId().longValue() == tt.getBatchId()).collect(Collectors.toList());
        // 构建投注记录
//
//        long totalRewardAmount = 0L;
//        for (MalakasGameRecordDo recordDo : filterList) {
//            RewardResultDo<MalakasSymbolDo> rewardResult = recordDo.getRewardResult();
//            if (rewardResult != null) {
//                totalRewardAmount = totalRewardAmount+ rewardResult.getTotalRewardAmount();
//            }
//
//        }
//
//        BetRecordBuilder.build(betRecordId,
//                betDo,
//                betDo.getBetDetail().getBetAmount(),
//                betDo.getBetDetail().getPayAmount(),
//                totalRewardAmount,
//
//        )
        MalakasBetRecordDetailDo resultDetail = MalakasBetRecordDetailDo.builder()
                .gameRecordList(filterList)
                .build();
        BetRecordDo<MalakasBetRecordDetailDo> betRecord = BetRecordBuilder.build(generateRecordId(),
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                filterList.stream().filter(item -> item.getRewardResult() != null)
                        .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                        .sum(),
                resultDetail);
        // 保存投注记录并派奖
        Long userCredit = saveBetRecordAndBetWithPayout(betRecord);
        // 组装返回结果
        return BetResultDo.<MalakasBetRecordDetailDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .credit(userCredit)
                .overallRewardAmount(betRecord.getPayOutAmount())
                .resultDetail(resultDetail)
                .build();
    }

    /**
     * 免费轮
     *
     * @param betDo
     * @return
     */
    private List<MalakasGameRecordDo> freeSpinsPlayGame(BetDo<MalakasBetDo> betDo) {
        return getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(BetSubTypeEnum.valueOfFreeSpinsBetSubType(betDo.getBetType()))
                .betAmount(betDo.getBetDetail().getBetAmount())
                .freeSpinsFlag(true)
                .build());
    }

    public void freeSpinsAfterHandle(BetDo<MalakasBetDo> betDo, MalakasBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        // do nothing
    }

}

package com.wegame.capi.biz.module.malakas.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MalakasBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MalakasBetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MalakasRecordManager;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.malakas.game.MalakasGame;
import com.wegame.capi.biz.module.mariamakiling.bet.strategy.MariaMakilingBetStrategy;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Malakas游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMalakasBetStrategy extends AbstractBetStrategy<MalakasGameRecordDo, MalakasBetDo, MalakasBetRecordDetailDo>
        implements MariaMakilingBetStrategy<MalakasBetDo, MalakasBetRecordDetailDo> {

    @Autowired
    MalakasGame malakasGame;
    @Autowired
    MalakasRecordManager malakasRecordManager;

    @Override
    public Game<MalakasGameRecordDo> getGame() {
        return malakasGame;
    }

    @Override
    public AbstractRecordManager<MalakasGameRecordDo> getRecordManager() {
        return malakasRecordManager;
    }

    @Override
    public BetResultDo<MalakasBetRecordDetailDo> bet(BetDo<MalakasBetDo> betDo) {
        if (!BetSubTypeEnum.isFreeSpins(betDo.getBetSubType().getCode())) {
            // 非免费轮投注(正常投注、购买投注)
            return betForNonFreeSpins(betDo);
        } else {
            // 免费轮投注
            return betForFreeSpins(betDo);
        }
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForNonFreeSpins(BetDo<MalakasBetDo> betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        List<MalakasGameRecordDo> list = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .betRecordId(betRecordId)
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        Long batchId = list.get(0).getBatchId();
        List<MalakasGameRecordDo> firstBatchList = list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
        MalakasBetRecordDetailDo resultDetail = MalakasBetRecordDetailDo.builder().gameRecordList(firstBatchList).build();
        // 2、构建投注记录
        BetRecordDo<MalakasGameRecordDo> betRecord = BetRecordBuilder.build(betRecordId,
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                firstBatchList.stream()
                        .filter(item -> item.getRewardResult() != null)
                        .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                        .sum(),
                list);
        // 3、保存投注记录并派奖
        Long userCredit = saveBetRecordAndBetWithPayout(betRecord, hasRewardFreeWheel(firstBatchList));
        // 4、组装返回结果
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
     * 免费轮投注
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForFreeSpins(BetDo<MalakasBetDo> betDo) {
        MalakasBetDo betDetail = betDo.getBetDetail();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        List<MalakasGameRecordDo> gameRecordList = getRecordManager().listGameRecord(
                GameRecordQuery.builder()
                        .batchId(betDo.getBetDetail().getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .orderByClause("_id")
                        .direction(Sort.Direction.ASC)
                        .build(), getGame().getGameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        return BetResultDo.<MalakasBetRecordDetailDo>builder()
                .betRecordId(betDetail.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(gameRecordList.stream().filter(item -> item.getRewardResult() != null)
                        .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                        .sum())
                .resultDetail(MalakasBetRecordDetailDo.builder()
                        .gameRecordList(gameRecordList)
                        .build())
                .build();
    }

    private boolean hasRewardFreeWheel(List<MalakasGameRecordDo> firstBatchList) {
        MalakasGameRecordDo gameRecord = firstBatchList.stream()
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }

    public void freeSpinsAfterHandle(BetDo<MalakasBetDo> betDo, MalakasBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        // do nothing
    }
}

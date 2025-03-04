package com.wegame.capi.biz.module.malakas.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MalakasBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.domain.biz.bet.MalakasBetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MalakasRecordManager;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.malakas.game.MalakasGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

/**
 * Malakas游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMalakasBetStrategy extends AbstractBetStrategy<MalakasGameRecordDo, MalakasBetDo, MalakasBetRecordDetailDo>
        implements MalakasBetStrategy<MalakasBetDo, MalakasBetRecordDetailDo> {

    @Autowired
    MalakasGame malakasGame;
    @Autowired
    MalakasRecordManager malakasRecordManager;
    @Autowired
    MalakasConfig malakasConfig;

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
        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
            // 非免费轮投注(正常投注、购买投注)
            return betForNonFreeWheel(betDo);
        } else {
            // 免费轮投注
            return betForFreeWheel(betDo);
        }
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForNonFreeWheel(BetDo<MalakasBetDo> betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        List<MalakasGameRecordDo> list = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .payAmount(betDo.getBetDetail().getPayAmount())
                .betRecordId(betRecordId)
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        List<MalakasGameRecordDo> firstBatchList = getFirstBatchGameRecordList(list);
        // 2、构建投注记录
        //// 总中奖金额
        long totalRewardAmount = list.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        BetRecordDo<MalakasGameRecordDo> betRecord = BetRecordBuilder.build(betRecordId,
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                maxAmountControl(totalRewardAmount, betDo.getBetDetail().getPayAmount(), malakasConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetDetail().getPayAmount(), malakasConfig.getRewardMaxMultiple()),
                list);
        BetSingleWalletResultDo resultDo = saveBetRecordAndBetWithPayout(betRecord, hasRewardFreeWheel(firstBatchList));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);

        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            //        // 4、组装返回结果
            //// 合计中奖总金额
            long overallRewardAmount = firstBatchList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
            return BetResultDo.<MalakasBetRecordDetailDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .credit(resultDo.getCredit())
                    .overallRewardAmount(maxAmountControl(overallRewardAmount, betDo.getBetDetail().getPayAmount(), malakasConfig.getRewardMaxMultiple()))
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(MalakasBetRecordDetailDo.builder().gameRecordList(firstBatchList).build())
                    .build();
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.FAIL)) {
            throw new BusinessException(ErrorCode.ERR_INTER, resultDo.getReason());
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.ING)) {
            throw new BusinessException(ErrorCode.ERR_NET);
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForFreeWheel(BetDo<MalakasBetDo> betDo) {
        MalakasBetDo betDetail = betDo.getBetDetail();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        List<MalakasGameRecordDo> gameRecordList = getRecordManager().listGameRecord(
                GameRecordQuery.builder()
                        .betRecordId(betDo.getBetDetail().getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .batchId(betDo.getBetDetail().getGameRecordId())
                        .build(), getGame().getGameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MalakasGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetResultDo.<MalakasBetRecordDetailDo>builder()
                .betRecordId(betDetail.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(MalakasBetRecordDetailDo.builder().gameRecordList(gameRecordList).build())
                .build();
    }

    private boolean hasRewardFreeWheel(List<MalakasGameRecordDo> firstBatchList) {
        MalakasGameRecordDo gameRecord = firstBatchList.stream()
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }

    private List<MalakasGameRecordDo> getFirstBatchGameRecordList(List<MalakasGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    public void freeSpinsAfterHandle(BetDo<MalakasBetDo> betDo, MalakasBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        // do nothing
    }
}

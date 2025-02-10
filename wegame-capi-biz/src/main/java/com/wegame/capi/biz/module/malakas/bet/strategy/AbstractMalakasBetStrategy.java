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
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
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
public abstract class AbstractMalakasBetStrategy extends AbstractBetStrategy<MalakasBetDo, MalakasGameRecordDo> {

        @Autowired
        MalakasGame malakasGame;

        @Override
        public Game<MalakasGameRecordDo> getGame() {
            return malakasGame;
        }

        @Override
        public BetResultDo<MalakasGameRecordDo> bet(BetDo<MalakasBetDo> betDo) {
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
        private BetResultDo<MalakasGameRecordDo> betForFreeSpins(BetDo<MalakasBetDo> betDo) {
            MalakasBetDo betDetail = betDo.getBetDetail();
            UserInfo userInfo = UserInfoContext.getUserInfo();
            BetRecordDo<MalakasBetRecordDetailDo> betRecordDo = betRecordService.get(betDetail.getBetRecordId(), userInfo.getPlayerId(), getGame().getGameInfo().getGameType());
            AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
            MalakasBetRecordDetailDo betRecordDetailDo = betRecordDo.getDetail();
            AssertUtil.assertNotNull(betRecordDetailDo, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotEmpty(betRecordDetailDo.getGameRecordList(), ErrorCode.ERR_PARAM);
            List<MalakasGameRecordDo> gameRecordList = betRecordDetailDo.getGameRecordList();
            //            // 匹配-免费轮游戏记录 todo fix
            //            MalakasGameRecordDo gameRecord = gameRecordList
            //                    .stream()
            //                    .filter(item -> Objects.equals(item.getIndex(), betDetail.getFreeSpinsId()))
            //                    .findFirst()
            //                    .orElse(null);
            //            // 统计-当前轮次及之前记录奖励金额之和
            //            Long overallRewardAmount = Optional.ofNullable(gameRecordList)
            //                    .orElse(Collections.emptyList())
            //                    .stream()
            //                    .filter(item -> item.getRewardResult() != null && item.getIndex() <= betDetail.getFreeSpinsId())
            //                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
            //                    .sum();
            // 后处理-免费轮
            //            freeSpinsAfterHandle(betDo, betRecordDetailDo, overallRewardAmount);
            // 返回
            return BetResultDo.<MalakasGameRecordDo>builder()
                    .betRecordId(betDetail.getBetRecordId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .overallRewardAmount(0L) // todo fix
                    .resultDetail(null) // todo fix
                    .build();
        }

        /**
         * 非免费轮投注
         */
        private BetResultDo<MalakasGameRecordDo> betForNonFreeSpins(BetDo<MalakasBetDo> betDo) {
            List<MalakasGameRecordDo> list = getGame().play(GamePlayDo.builder()
                    .gameType(betDo.getGameType())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .betAmount(betDo.getBetDetail().getBetAmount())
                    .freeSpinsFlag(false) // todo fix
                    .build());
            AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
            // 判断-是否中免费轮
            //            if (gameRecord.hasRewardFreeSpins()) {
            //                // 执行免费轮投注
            //                List<MalakasGameRecordDo> gameRecordList = freeSpinsPlayGame(betDo);
            //                // 组装-免费轮IDS
            //                gameRecord.setFreeSpinIds(Optional.ofNullable(gameRecordList)
            //                        .orElse(Collections.emptyList())
            //                        .stream()
            //                        .map(MalakasGameRecordDo::getIndex)
            //                        .collect(Collectors.toList()));
            //                list.addAll(gameRecordList);
            //            }
            // 构建投注记录
            BetRecordDo<MalakasBetRecordDetailDo> betRecord = BetRecordBuilder.build(generateRecordId(),
                    betDo,
                    betDo.getBetDetail().getBetAmount(),
                    betDo.getBetDetail().getPayAmount(),
                    list.stream().filter(item -> item.getRewardResult() != null)
                            .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                            .sum(),
                    MalakasBetRecordDetailDo.builder()
                            .gameRecordList(list)
                            .build());
            // 保存投注记录并派奖
            Long userCredit = saveBetRecordAndBetWithPayout(betRecord);
            // 组装返回结果
            return BetResultDo.<MalakasGameRecordDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .credit(userCredit)
                    .overallRewardAmount(null) // todo
                    .resultDetail(null) // todo
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

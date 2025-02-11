package com.wegame.capi.biz.builder;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;

import java.util.List;

public class BetRecordBuilder {

        public static <T> BetRecordDo<T> build(Long betRecordId,
                                               BetDo betDo,
                                               Long betAmount,
                                               Long payAmount,
                                               Long payOutAmount,
                                               List<T> gameRecordList) {

            Operator operator = OperatorContext.getOperator();
            AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
            long currentMilli = DateUtil.getCurrentMilli();

            return BetRecordDo.<T>builder()
                    .id(betRecordId)
                    .pId(UserInfoContext.getUserInfo().getPId())
                    .channelId(UserInfoContext.getUserInfo().getChannelId())
                    .playerId(UserInfoContext.getUserInfo().getPlayerId())
                    .userId(Long.valueOf(UserInfoContext.getUserInfo().getId()))
                    .playerName(UserInfoContext.getUserInfo().getPlayerName())
                    .gameId(betDo.getGameId())
                    .betSerialNo(String.valueOf(betRecordId))
                    .betType(betDo.getBetType().getCode())
                    .betSubType(betDo.getBetSubType().getCode())
                    .betAmount(betAmount)
                    .payAmount(payAmount)
                    .payOutAmount(payOutAmount)
                    .status(BetStatusEnum.ING.getCode())
                    .creator(operator.getName())
                    .updater(operator.getName())
                    .createTime(currentMilli)
                    .updateTime(currentMilli)
                    .gameRecordList(gameRecordList)
                    .build();
        }

        //    public static <T> BetRecordDo<T> build(Long betRecordId, BetDo betDo, Long betAmount, Long payAmount, Long payOutAmount, List<T> gameRecordList) {
        //
        //        Operator operator = OperatorContext.getOperator();
        //        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        //        long currentMilli = DateUtil.getCurrentMilli();
        //
        //        return BetRecordDo.builder()
        //                .id(betRecordId)
        //                .pId(UserInfoContext.getUserInfo().getPId())
        //                .channelId(UserInfoContext.getUserInfo().getChannelId())
        //                .playerId(UserInfoContext.getUserInfo().getPlayerId())
        //                .userId(Long.valueOf(UserInfoContext.getUserInfo().getId()))
        //                .playerName(UserInfoContext.getUserInfo().getPlayerName())
        //                .gameId(betDo.getGameId())
        //                .betSerialNo(String.valueOf(betRecordId))
        //                .betType(betDo.getBetType().getCode())
        //                .betSubType(betDo.getBetSubType().getCode())
        //                .betAmount(betAmount)
        //                .payAmount(payAmount)
        //                .payOutAmount(payOutAmount)
        //                .status(BetStatusEnum.ING.getCode())
        //                .creator(operator.getName())
        //                .updater(operator.getName())
        //                .createTime(currentMilli)
        //                .updateTime(currentMilli)
        //                .(gameRecordList)
        //                .build();
        //    }

}

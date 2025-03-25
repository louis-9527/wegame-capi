package com.wegame.capi.biz.module.mariamakiling.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.game.*;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_SCATTER;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("mariaMakilingGameEventPlay")
public class MariaMakilingGameEventPlay extends AbstractGame<MariaMakilingGameRecordDo> {

    private static final Long GAME_RECORD_DEFAULT_PARENT_ID = 0L;
    @Autowired
    RewardHandler<SymbolDo> mariaMakilingRewardHandler;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;


    @Override
    public List<MariaMakilingGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        GameInfoDo gameInfo = MARIA_MAKILING_GAME_INFO;

        GameEventDo gameEventDo = gamePlay.getGameEventDo();
        List<OperationEventConfigRecordDo> gameMultipleRecordDos = operationEventConfigRecordService.listByBetRecordId(gameEventDo.getConfigId(), OperationEventTypeEnum.MARIA_MAKILING);

        // 获取首轮(parent ==0 and index =0)
        OperationEventConfigRecordDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));


        // 生成首轮结果
        long firstGameRecordId = generateGameRecordId();
        WheelDo<SymbolDo> wheel = this.convertWheel(firstRecord.getWheel());
        RewardResultDo<SymbolDo> firstWheelRewardResult = mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, BigDecimal.ZERO));
        long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
        List<MariaMakilingGameRecordDo> freeWheelGameRecordList = new ArrayList<>();
        List<Long> freeWheelGameRecordIds = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(firstRecord.getGameRecordIds())){
            List<Long> gameRecordIds = firstRecord.getGameRecordIds();
            Map<Long, OperationEventConfigRecordDo> multipleRecordMap = gameMultipleRecordDos.stream().collect(Collectors.toMap(OperationEventConfigRecordDo::getId, p -> p));

            for (Long recordId : gameRecordIds) {
                OperationEventConfigRecordDo multipleRecordDo = multipleRecordMap.get(recordId);
                WheelDo<SymbolDo> wheelRecord = this.convertWheel(multipleRecordDo.getWheel());

                RewardResultDo<SymbolDo> rewardResultDo = mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheelRecord, BigDecimal.ZERO));
                overallRewardAmount = overallRewardAmount + rewardResultDo.getTotalRewardAmount();
                MariaMakilingGameRecordDo gameRecord = this.buildGameRecord(
                        userInfo,
                        generateGameRecordId(),
                        gamePlay.getBetRecordId(),
                        wheelRecord,
                        rewardResultDo,
                        null,
                        overallRewardAmount,
                        false,
                        false);
                freeWheelGameRecordList.add(gameRecord);
                freeWheelGameRecordIds.add(gameRecord.getId());
            }
        }


        // 构建首轮
        MariaMakilingGameRecordDo firstGameRecord = this.buildGameRecord(userInfo,
                firstGameRecordId,
                gamePlay.getBetRecordId(),
                wheel,
                firstWheelRewardResult,
                freeWheelGameRecordIds,
                firstWheelRewardResult.getTotalRewardAmount(),
                false,
                firstWheelRewardResult.isHasRewardFreeWheel()
        );
        List<MariaMakilingGameRecordDo> ret = new ArrayList<>();
        ret.add(firstGameRecord);
        if (CollectionUtils.isNotEmpty(freeWheelGameRecordList)) {
            ret.addAll(freeWheelGameRecordList);
        }

        return ret;
    }




    private MariaMakilingGameRecordDo buildGameRecord(UserInfo userInfo,
                                                     Long gameRecordId,
                                                     Long betRecordId,
                                                     WheelDo<SymbolDo> wheel,
                                                     RewardResultDo<SymbolDo> rewardResult,
                                                     List<Long> freeSpinGameRecordIds,
                                                     Long TotalRewardAmount,
                                                     Boolean accumulateReachMaxMultiple,
                                                     Boolean lastGameFlag) {
        return MariaMakilingGameRecordDo.builder()
                .id(gameRecordId)
                .parentId(GAME_RECORD_DEFAULT_PARENT_ID)
                .betRecordId(betRecordId)
                .batchId(0L)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(freeSpinGameRecordIds)
                .overallRewardAmount(TotalRewardAmount)
                .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                .lastGameFlag(lastGameFlag)
                .build();
    }



    private WheelDo<SymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<SymbolDo>>() {});
    }
}

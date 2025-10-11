package com.wegame.capi.biz.module.mariamakiling.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetContextDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.MariaMakilingBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MariaMakilingOperationEventManager;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.mariamakiling.bet.MariaMakilingBetHelper;
import com.wegame.capi.biz.module.mariamakiling.game.MariaMakilingGameContext;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;

/**
 * MariaMakiling类游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMariaMakilingBetStrategy
        extends AbstractCommonBetStrategy<MariaMakilingGameRecordDo, MariaMakilingGameRecordDo>
        implements MariaMakilingBetStrategy<MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingGameContext mariaMakilingGameContext;
    @Autowired
    MariaMakilingBetRecordManager mariaMakilingBetRecordManager;
    @Autowired
    MariaMakilingBetHelper mariaMakilingBetHelper;
    @Autowired
    @Qualifier("mariaMakilingOperationEventManager")
    MariaMakilingOperationEventManager mariaMakilingOperationEventManager;

    @Override
    public AbstractGameContext<MariaMakilingGameRecordDo> gameContext() {
        return mariaMakilingGameContext;
    }

    @Override
    public AbstractBetRecordManager<MariaMakilingGameRecordDo> betRecordManager() {
        return mariaMakilingBetRecordManager;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return mariaMakilingOperationEventManager;
    }

    @Override

    public BetHelper<MariaMakilingGameRecordDo, MariaMakilingGameRecordDo> betHelper() {
        return mariaMakilingBetHelper;
    }

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> bet(BetContextDo betContextDo) {
        if (!BetSubTypeEnum.isFreeWheelQuery(betContextDo.getBetSubType().getCode())) {
            // 非免费轮投注流程
            //// 1、是否使用星卡
            if (isCanStarCardBet(betContextDo)) {
                // 1.1 是，则星卡投注流程
                return betForStartCard(betContextDo);
            } else if (isCanFreeSpinBet(betContextDo)) {
                // 1.1 是，则预定投注流程
                return betForFreeSpin(betContextDo);
            } else {
                // 1.2 否，则走其他投注流程
                // 1.2.1、判断能否走干预事件投注
                if (isCanOperationEventBet(betContextDo)) {
                    // 1.2.11 命中干预事件：走干预事件投注
                    return betForOperationEvent(betContextDo);
                } else {
                    // 1.2.12 未命中干预事件：走正常投注。
                    return betForNonFreeWheel(betContextDo); // todo 重命名
                }
            }
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheelQuery(betContextDo);
        }
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForFreeWheelQuery(BetContextDo betContextDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<MariaMakilingGameRecordDo> gameRecordList = mariaMakilingBetRecordManager.listGameRecordWithCache(GameRecordQuery.builder()
                        .betRecordId(betContextDo.getBetRecordId())
                        .cacheKey(String.format("%s_%s", betContextDo.getBetRecordId(), userInfo.getPlayerId()))
                        .fromPrimary(true)
                        .build(),
                gameContext().gameInfo().getGameType());
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MariaMakilingGameRecordDo::getIndex))
                .collect(Collectors.toList());
        // 匹配-免费轮游戏记录
        MariaMakilingGameRecordDo matchGameRecord = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getId(), betContextDo.getGameRecordId()))
                .findFirst()
                .orElse(null);
        AssertUtil.assertNotNull(matchGameRecord, ErrorCode.ERR_PARAM, "NOT FOUND GAME RECORD");
        // 后处理-免费轮
        MariaMakilingBetRecordDetailDo detailDo = MariaMakilingBetRecordDetailDo.builder()
                .gameRecordList(gameRecordList)
                .build();
        freeWheelAfterHandle(betContextDo, detailDo, matchGameRecord.getOverallRewardAmount());
        // 返回
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betStatus(BetStatusEnum.SUCCESS)
                .betRecordId(betContextDo.getBetRecordId())
                .gameId(betContextDo.getGameId())
                .betType(betContextDo.getBetType())
                .betSubType(betContextDo.getBetSubType())
                .overallRewardAmount(matchGameRecord.getOverallRewardAmount())
                .resultDetail(matchGameRecord)
                .build();
    }

    /**
     * 非免费轮投注
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForNonFreeWheel(BetContextDo betContextDo) {
        long betRecordId = generateBetRecordId();
        buildGamePlayType(betContextDo);
        List<MariaMakilingGameRecordDo> list = gameContext().play(build(betContextDo, betRecordId, null));
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        // 构建投注记录
        BetRecordDo<MariaMakilingGameRecordDo> betRecord = mariaMakilingBetHelper.buildBetRecord(betRecordId, betContextDo, list);
        // 保存投注记录并派奖
        boolean syncOpt = mariaMakilingBetHelper.hasRewardFreeWheel(list);
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betContextDo, betRecord, syncOpt);
        return mariaMakilingBetHelper.handleBetResult(betRecord, resultDo);
    }

    public void freeWheelAfterHandle(BetContextDo betContextDo, MariaMakilingBetRecordDetailDo detailDo, Long totalRewardAmount) {
        // do nothing
    }
}

package com.wegame.capi.biz.module.malakas.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.malakas.reward.MalakasRewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.postProcessPlaceholderSymbol;

@Slf4j
@Component("malakasGameEventPlay")
public class MalakasGameEventPlay extends AbstractGame<MalakasGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = MALAKAS_GAME_INFO;

    @Autowired
    MalakasRewardHandler malakasRewardHandler;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<MalakasGameRecordDo> play(BetDo betDo, GamePlayDo gamePlay) {
        List<OperationEventConfigRecordDo> configList = operationEventConfigRecordService.listByBetRecordId(gamePlay.getGameEventDo().getConfigId(), OperationEventTypeEnum.MALAKAS_DYNAMIC);
        AssertUtil.assertNotEmpty(configList, ErrorCode.ERR_INTER);
        // 预处理：排序升序（batchId， index）
        configList = configList.stream()
                .sorted(Comparator.comparing(OperationEventConfigRecordDo::getBatchId).thenComparing(OperationEventConfigRecordDo::getIndex)) // 第二排序（次，仅主字段相同时生效）
                .collect(Collectors.toList());
        Map<Long, Long> idMap = buildIdMap(configList);
        return convertTemplateList(gamePlay, configList, idMap);
    }

    private List<MalakasGameRecordDo> convertTemplateList(GamePlayDo gamePlayDo, List<OperationEventConfigRecordDo> configList, Map<Long, Long> idMap) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();;
        Long maxBatchId = Collections.max(configList, Comparator.comparing(OperationEventConfigRecordDo::getBatchId)).getBatchId();
        BigDecimal totalMultiple = BigDecimal.ZERO;
        boolean resetTotalMultipleFlag = false;
        for (OperationEventConfigRecordDo configItem : CollectionUtils.emptyIfNull(configList)) {
            boolean lastGameFlag = Objects.equals(configItem.getBatchId(), maxBatchId);
            MalakasGameRecordDo gameRecordDo = this.buildGameRecord(gamePlayDo, configItem, totalMultiple, idMap, lastGameFlag);
            if (!resetTotalMultipleFlag && gameRecordDo.getRewardResult().isHasRewardFreeWheel()) {
                // 第一次命中免费了， 重置倍率为0
                resetTotalMultipleFlag = true;
                totalMultiple = BigDecimal.ZERO;
            } else {
                totalMultiple = gameRecordDo.getRewardResult().getTotalMultiple();
            }
            // todo delete log
            log.info("betRecordId: {}, gameRecordId: {}, malakas totalMultiple: {}", gamePlayDo.getBetRecordId(), gameRecordDo.getId(), totalMultiple);
            ret.add(gameRecordDo);
        }
        // 占位元素-将占位元素设置为空:
        postProcessPlaceholderSymbol(ret);
        // 返回
        return ret;
    }

    private Map<Long, Long> buildIdMap(List<OperationEventConfigRecordDo> configRecordList) {
        Map<Long, Long> idMap = new HashMap<>();;
        idMap.put(RECORD_DEFAULT_PARENT_ID, RECORD_DEFAULT_PARENT_ID);
        for (OperationEventConfigRecordDo item : CollectionUtils.emptyIfNull(configRecordList)) {
            if (!idMap.containsKey(item.getId())) {
                idMap.put(item.getId(), snowflakeIdGenerator.nextId());
            }
        }
        return idMap;
    }

    private MalakasGameRecordDo buildGameRecord(GamePlayDo gamePlay, OperationEventConfigRecordDo config, BigDecimal totalMultiple, Map<Long, Long> idMap, boolean lastGameFlag) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        WheelDo<MalakasSymbolDo> wheel = convertWheel(config.getWheel());
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, totalMultiple));
        Long id = idMap.get(config.getId());
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        Long parentId = idMap.get(config.getParentId());
        AssertUtil.assertNotNull(parentId, ErrorCode.ERR_INTER);
        Long batchId = idMap.get(config.getBatchId());
        AssertUtil.assertNotNull(batchId, ErrorCode.ERR_INTER);
        return MalakasGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(gamePlay.getBetRecordId())
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(config.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(Optional.ofNullable(config.getGameRecordIds())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(idMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .lastGameFlag(lastGameFlag)
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    private WheelDo<MalakasSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<MalakasSymbolDo>>() {
        });
    }
}
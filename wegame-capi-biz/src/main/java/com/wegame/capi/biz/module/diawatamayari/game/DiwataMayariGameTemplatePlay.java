package com.wegame.capi.biz.module.diawatamayari.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.template.malakas.DiwataBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariSymbolUtil;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.client.enums.TemplateTypeEnum.DIWATA_MAYARI;
import static com.wegame.capi.common.Constants.*;


@Slf4j
@Component("diwataMayariGameTemplatePlay")
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariGameTemplatePlay extends AbstractGame<DiwataMayariGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO;

    @Autowired
    DiwataMayariRewardHandler diwataMayariRewardHandler;

    @Autowired
    BetRecordTemplateEsService<DiwataBetRecordTemplateEsDo> betRecordTemplateEsService;
    @Autowired
    private DiwataMayariConfig diwataMayariConfig;

    @Override
    public List<DiwataMayariGameRecordDo> play(GamePlayDo gamePlay) {
        List<DiwataMayariGameRecordDo> ret = new ArrayList<>();
        List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay);
        // 获取首轮(batchId == 0 and index == 0)
        GameRecordTemplateDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        long firstGameRecordId = super.generateGameRecordId();
        DiwataMayariGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord, BigDecimal.ZERO);
        ret.add(firstGameRecord);
        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<GameRecordTemplateDo>> groupedByBatchId = gameMultipleRecordDos.stream()
                .collect(Collectors.groupingBy(GameRecordTemplateDo::getBatchId));

        // 获取首批次并排序
        List<GameRecordTemplateDo> recordBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(GameRecordTemplateDo::getIndex))
                .collect(Collectors.toList());
        // 处理每个批次
        DiwataMayariGameRecordDo lastGameRecord = firstGameRecord;

        for (GameRecordTemplateDo recordDo : recordBatchList) {
            if (recordDo.getParentId() != 0L) {
                // 处理首轮普通轮
                long gameRecordId = generateGameRecordId();
                DiwataMayariGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(), lastGameRecord.getBatchId(), recordDo, BigDecimal.ZERO);
                ret.add(gameRecord);
                lastGameRecord = gameRecord;
            }

            if (CollectionUtils.isEmpty(recordDo.getGameRecordIds())) {
                continue;
            }
            // 处理免费轮
            processFreeRounds(ret, gamePlay, recordDo, lastGameRecord, groupedByBatchId);
        }
        // 设置结束
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        return ret;
    }

    private List<GameRecordTemplateDo> getGameRecordTemplate(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(gamePlay.getBetType());
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);

        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(DIWATA_MAYARI, gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), DiwataBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("betRecordId:{}", betRecordId);
        List<GameRecordTemplateDo> configRecordList;
        DiwataBetRecordTemplateEsDo template;
        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.DIWATA_MAYARI, gamePlay.getBetType(), templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存
            if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.NORMAL_BET)) {
                template = betRecordTemplateEsService.getTemplateWithLocalCache(DIWATA_MAYARI, betRecordId, DiwataBetRecordTemplateEsDo.class);
            } else if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
                template = betRecordTemplateEsService.getTemplateWithRedisCache(DIWATA_MAYARI, betRecordId, DiwataBetRecordTemplateEsDo.class);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(DIWATA_MAYARI, betRecordId, DiwataBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        if (CollectionUtils.isEmpty(configRecordList)) {
            log.error("template is empty, betType:{}, range start: [], end: {}", gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        }
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        return configRecordList;
    }

    private Long getRandomElement(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betTypeEnum) {
        String templateMultipleRange;
        if (Objects.equals(betTypeEnum, BetTypeEnum.NORMAL_BET)) {
            templateMultipleRange = randomService.randomKeyByWeight(diwataMayariConfig.getTemplateRangeConfig().getMainGame());
        } else {
            templateMultipleRange = randomService.randomKeyByWeight(diwataMayariConfig.getTemplateRangeConfig().getFreeGame());
        }
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }


    /**
     * 递归处理免费轮，确保同层级先处理完，再进入下一层
     */
    private void processFreeRounds(
            List<DiwataMayariGameRecordDo> ret,
            GamePlayDo gamePlay,
            GameRecordTemplateDo recordDo,
            DiwataMayariGameRecordDo parent,
            Map<Long, List<GameRecordTemplateDo>> groupedByBatchId) {
        Map<GameRecordTemplateDo, DiwataMayariGameRecordDo> nextLevelGameRecordMap = new HashMap<>();

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            List<GameRecordTemplateDo> currentLevelRecords = groupedByBatchId.get(batchId);
            currentLevelRecords.sort(Comparator.comparing(GameRecordTemplateDo::getIndex));
            // 处理当前层级的每个记录
            DiwataMayariGameRecordDo preGameRecord = null;
            for (GameRecordTemplateDo record : currentLevelRecords) {
                BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
                long gameRecordId = generateGameRecordId();
                DiwataMayariGameRecordDo gameRecord = null;
                if (record.getIndex() == 0) {
                    gameRecordIds.add(gameRecordId);
                    gameRecord = processGameRecord(gamePlay, gameRecordId, parent.getId(), gameRecordId, record, totalMultipleAmount);
                    ret.add(gameRecord);
                } else {
                    gameRecord = processGameRecord(gamePlay, gameRecordId, preGameRecord.getId(), preGameRecord.getBatchId(), record, totalMultipleAmount);
                    ret.add(gameRecord);
                }
                preGameRecord = gameRecord;
                if (CollectionUtils.isNotEmpty(record.getGameRecordIds())) {
                    nextLevelGameRecordMap.put(record, gameRecord);
                }
            }
        }
        parent.setGameRecordIds(gameRecordIds);
        for (Map.Entry<GameRecordTemplateDo, DiwataMayariGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            GameRecordTemplateDo key = entry.getKey();
            DiwataMayariGameRecordDo value = entry.getValue();
            processFreeRounds(ret, gamePlay, key, value, groupedByBatchId);
        }
    }

    /**
     * 构建并处理投注记录
     */
    private DiwataMayariGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            GameRecordTemplateDo recordDo,
            BigDecimal totalMultiple) {

        // 确定 `TotalMultiple` 参数
        WheelDo<DiwataMayariSymbolDo> diwataMayariSymbolWheel = this.convertWheel(recordDo.getWheel());
        DiwataMayariSymbolDo[][] symbolArr = diwataMayariSymbolWheel.getSymbolArr();
        // 处理空值
        diwataMayariSymbolWheel.setSymbolArr(DiwataMayariSymbolUtil.handleSymbolNullArr(symbolArr));
        RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, diwataMayariSymbolWheel, totalMultiple));
        // 构建时还原
        diwataMayariSymbolWheel.setSymbolArr(symbolArr);
        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, diwataMayariSymbolWheel, rewardResult);


    }

    private DiwataMayariGameRecordDo buildGameRecord(Long id,
                                                     Long parentId,
                                                     Long betRecordId,
                                                     Long batchId,
                                                     WheelDo<DiwataMayariSymbolDo> wheel,
                                                     RewardResultDo<DiwataMayariSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return DiwataMayariGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .build();
    }

    private WheelDo<DiwataMayariSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<DiwataMayariSymbolDo>>() {
        });
    }
}
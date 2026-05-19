package com.wegame.capi.biz.module.diawatamayari.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.config.domain.common.CommonTemplateConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.malakas.DiwataBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractGameTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateIndexTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.common.Constants.DIWATA_MAYARI_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.utils.ListUtil.getRandomElement;

@Slf4j
@Component("diwataMayariGameTemplatePlay")
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariGameTemplatePlay extends AbstractGameTemplatePlay<DiwataMayariGameRecordDo, DiwataBetRecordTemplateEsDo> {

    @Autowired
    DiwataMayariConfig diwataMayariConfig;
    @Autowired
    DiwataMayariGameCommonTemplatePlay diwataMayariGameCommonTemplatePlay;

    @Override
    public TemplateGame templateGame() {
        return diwataMayariGameCommonTemplatePlay;
    }

    @Override
    public DiwataBetRecordTemplateEsDo getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        Pair<String, TemplateMultipleRange> pair = getTemplateMultipleRangeWithBloodPoolControl(betDo, gamePlayDo, diwataMayariConfig.getCommonTemplateConfig());
        AssertUtil.assertNotNull(pair, ErrorCode.ERR_INTER);
        String templateIndexName = pair.getKey();
        AssertUtil.assertStrNotEmpty(templateIndexName, ErrorCode.ERR_INTER);
        TemplateMultipleRange multipleRange = pair.getValue();
        AssertUtil.assertNotNull(multipleRange, ErrorCode.ERR_INTER);
        gamePlayDo.setTemplateMultipleRange(multipleRange);
        List<Long> betRecordIdList = betRecordTemplateService.listIdByMultipleRange(templateIndexName, gamePlayDo.getBetType(), multipleRange.getStart(), multipleRange.getEnd());
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("match template, templateIndexName: {}, betRecordId:{}", templateIndexName, betRecordId);
        if (diwataMayariConfig.getCommonTemplateConfig().supportWarmUp(gamePlayDo.getBetType(), multipleRange)) {
            return betRecordTemplateService.getTemplateWithLocalCache(templateIndexName, betRecordId, DiwataBetRecordTemplateEsDo.class);
        } else {
            return betRecordTemplateService.findById(templateIndexName, betRecordId, DiwataBetRecordTemplateEsDo.class);
        }
    }

    //@Override
    //public List<DiwataMayariGameRecordDo> play(BetDo betDo, GamePlayDo gamePlay) {
    //    List<DiwataMayariGameRecordDo> ret = new ArrayList<>();
    //    List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay);
    //    // 获取首轮(batchId == 0 and index == 0)
    //    GameRecordTemplateDo firstRecord = gameMultipleRecordDos.stream()
    //            .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
    //            .findFirst()
    //            .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));
    //
    //    // 处理首轮
    //    long firstGameRecordId = super.generateGameRecordId();
    //    DiwataMayariGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord, BigDecimal.ZERO);
    //    ret.add(firstGameRecord);
    //    // 按 batchId 分组 & 记录 ID 映射
    //    Map<Long, List<GameRecordTemplateDo>> groupedByBatchId = gameMultipleRecordDos.stream()
    //            .collect(Collectors.groupingBy(GameRecordTemplateDo::getBatchId));
    //
    //    // 获取首批次并排序
    //    List<GameRecordTemplateDo> recordBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
    //            .stream()
    //            .sorted(Comparator.comparing(GameRecordTemplateDo::getIndex))
    //            .collect(Collectors.toList());
    //    // 处理每个批次
    //    DiwataMayariGameRecordDo lastGameRecord = firstGameRecord;
    //
    //    for (GameRecordTemplateDo recordDo : recordBatchList) {
    //        if (recordDo.getParentId() != 0L) {
    //            // 处理首轮普通轮
    //            long gameRecordId = generateGameRecordId();
    //            DiwataMayariGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(), lastGameRecord.getBatchId(), recordDo, lastGameRecord.getRewardResult().getTotalMultiple());
    //            ret.add(gameRecord);
    //            lastGameRecord = gameRecord;
    //        }
    //
    //        if (CollectionUtils.isEmpty(recordDo.getGameRecordIds())) {
    //            continue;
    //        }
    //        // 处理免费轮
    //        processFreeRounds(ret, gamePlay, recordDo, lastGameRecord, groupedByBatchId);
    //    }
    //    // 设置结束
    //    if (CollectionUtils.isNotEmpty(ret)) {
    //        ret.get(ret.size() - 1).setLastGameFlag(true);
    //    }
    //    // 结果验证数据添加
    //    //// 总中奖金额
    //    long totalRewardAmount = ret.stream()
    //            .filter(item -> Objects.nonNull(item.getRewardResult()))
    //            .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
    //            .sum();
    //    gamePlay.setOverallRewardAmount(totalRewardAmount);
    //    return ret;
    //}
    //
    //private List<GameRecordTemplateDo> getGameRecordTemplate(GamePlayDo gamePlay) {
    //    DiwataBetRecordTemplateEsDo template = getTemplateEsDo(gamePlay);
    //    AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
    //    List<GameRecordTemplateDo> configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
    //    });
    //    if (CollectionUtils.isEmpty(configRecordList)) {
    //        log.error("template is empty, betType:{}, template multiple:{}", gamePlay.getBetType(), template.getMultiple());
    //    }
    //    AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
    //    return configRecordList;
    //}
    //
    //private DiwataBetRecordTemplateEsDo getTemplateEsDo(GamePlayDo gamePlay) {
    //    UserInfo userInfo = UserInfoContext.getUserInfo();
    //    AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
    //    AbTypeEnum abType = AbTypeEnum.valueOfId(userInfo.getUt());
    //    TemplateTypeEnum templateType = diwataMayariConfig.getTemplateRangeConfig().getTemplateMapping().get(abType);
    //    AssertUtil.assertNotNull(templateType, ErrorCode.ERR_INTER);
    //    log.info("abType: {}, templateName: {}", abType, templateType.getBetRecordTemplateName());
    //    TemplateMultipleRange templateMultipleRange = this.getTemplateMultipleRange(templateType, gamePlay.getBetType());
    //    AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
    //    AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
    //    AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);
    //    List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), DiwataBetRecordTemplateEsDo.class);
    //    AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
    //    Long betRecordId = getRandomElement(betRecordIdList);
    //    log.info("hit template betRecordId: {}", betRecordId);
    //    DiwataBetRecordTemplateEsDo template;
    //    if (diwataMayariConfig.getTemplateRangeConfig().supportWarmUp(gamePlay.getBetType(), templateMultipleRange)) {
    //        // 已经被预热的倍率，直接走缓存
    //        // todo delete log
    //        log.info("support warm template. load from local cache. betType: {}; templateType: {}. templateMultipleRange: {}", gamePlay.getBetType(), templateType, templateMultipleRange);
    //        template = betRecordTemplateEsService.getTemplateWithLocalCache(templateType, betRecordId, DiwataBetRecordTemplateEsDo.class);
    //    } else {
    //        // 没有被预热的倍率，需要重新查询
    //        template = betRecordTemplateEsService.findById(templateType, betRecordId, DiwataBetRecordTemplateEsDo.class);
    //    }
    //    // 结果验证数据添加
    //    gamePlay.setTemplateId(betRecordId);
    //    gamePlay.setTemplateMultipleRange(templateMultipleRange);
    //    return template;
    //}
    //
    //private TemplateMultipleRange getTemplateMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType) {
    //    AssertUtil.assertNotNull(templateType, ErrorCode.ERR_INTER);
    //    AssertUtil.assertNotNull(betType, ErrorCode.ERR_INTER);
    //    Map<BetTypeEnum, Map<String, Integer>> map = diwataMayariConfig.getTemplateRangeConfig().getTemplateRangeMap().get(templateType);
    //    AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
    //    Map<String, Integer> weightMap = map.get(betType);
    //    String templateMultipleRange = randomService.randomKeyByWeight(weightMap);
    //    log.info("hit templateType: {}, betType: {}, templateMultipleRange: {}", templateType.name(), betType, templateMultipleRange);
    //    AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
    //    return TemplateMultipleRange.build(templateMultipleRange);
    //}
    //
    ///**
    // * 递归处理免费轮，确保同层级先处理完，再进入下一层
    // */
    //private void processFreeRounds(
    //        List<DiwataMayariGameRecordDo> ret,
    //        GamePlayDo gamePlay,
    //        GameRecordTemplateDo recordDo,
    //        DiwataMayariGameRecordDo parent,
    //        Map<Long, List<GameRecordTemplateDo>> groupedByBatchId) {
    //    Map<GameRecordTemplateDo, DiwataMayariGameRecordDo> nextLevelGameRecordMap = new HashMap<>();
    //
    //    List<Long> gameRecordIds = new ArrayList<>();
    //    for (Long batchId : recordDo.getGameRecordIds()) {
    //        List<GameRecordTemplateDo> currentLevelRecords = groupedByBatchId.get(batchId);
    //        currentLevelRecords.sort(Comparator.comparing(GameRecordTemplateDo::getIndex));
    //        // 处理当前层级的每个记录
    //        DiwataMayariGameRecordDo preGameRecord = null;
    //        for (GameRecordTemplateDo record : currentLevelRecords) {
    //            BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
    //            long gameRecordId = generateGameRecordId();
    //            DiwataMayariGameRecordDo gameRecord = null;
    //            if (record.getIndex() == 0) {
    //                gameRecordIds.add(gameRecordId);
    //                gameRecord = processGameRecord(gamePlay, gameRecordId, parent.getId(), gameRecordId, record, totalMultipleAmount);
    //                ret.add(gameRecord);
    //            } else {
    //                gameRecord = processGameRecord(gamePlay, gameRecordId, preGameRecord.getId(), preGameRecord.getBatchId(), record, totalMultipleAmount);
    //                ret.add(gameRecord);
    //            }
    //            preGameRecord = gameRecord;
    //            if (CollectionUtils.isNotEmpty(record.getGameRecordIds())) {
    //                nextLevelGameRecordMap.put(record, gameRecord);
    //            }
    //        }
    //    }
    //    parent.setGameRecordIds(gameRecordIds);
    //    for (Map.Entry<GameRecordTemplateDo, DiwataMayariGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
    //        GameRecordTemplateDo key = entry.getKey();
    //        DiwataMayariGameRecordDo value = entry.getValue();
    //        processFreeRounds(ret, gamePlay, key, value, groupedByBatchId);
    //    }
    //}
    //
    ///**
    // * 构建并处理投注记录
    // */
    //private DiwataMayariGameRecordDo processGameRecord(
    //        GamePlayDo gamePlay,
    //        long gameRecordId,
    //        long parentId,
    //        long batchId,
    //        GameRecordTemplateDo recordDo,
    //        BigDecimal totalMultiple) {
    //
    //    // 确定 `TotalMultiple` 参数
    //    WheelDo<DiwataMayariSymbolDo> diwataMayariSymbolWheel = this.convertWheel(recordDo.getWheel());
    //    DiwataMayariSymbolDo[][] symbolArr = diwataMayariSymbolWheel.getSymbolArr();
    //    // 处理空值
    //    diwataMayariSymbolWheel.setSymbolArr(DiwataMayariSymbolUtil.handleSymbolNullArr(symbolArr));
    //    RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, diwataMayariSymbolWheel, totalMultiple));
    //    // 构建时还原
    //    diwataMayariSymbolWheel.setSymbolArr(symbolArr);
    //    return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, diwataMayariSymbolWheel, rewardResult);
    //
    //
    //}
    //
    //private DiwataMayariGameRecordDo buildGameRecord(Long id,
    //                                                 Long parentId,
    //                                                 Long betRecordId,
    //                                                 Long batchId,
    //                                                 WheelDo<DiwataMayariSymbolDo> wheel,
    //                                                 RewardResultDo<DiwataMayariSymbolDo> rewardResult) {
    //    UserInfo userInfo = UserInfoContext.getUserInfo();
    //    AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
    //    return DiwataMayariGameRecordDo.builder()
    //            .id(id)
    //            .parentId(parentId)
    //            .betRecordId(betRecordId)
    //            .batchId(batchId)
    //            .playerId(userInfo.getPlayerId())
    //            .playerName(userInfo.getPlayerName())
    //            .index(wheel.getIndex())
    //            .freeWheelFlag(wheel.isFreeFlag())
    //            .wheel(wheel)
    //            .rewardResult(rewardResult)
    //            .gameRecordIds(null)
    //            .lastGameFlag(false)
    //            .creator(userInfo.getPlayerId())
    //            .createTime(DateUtil.getCurrentMilli())
    //            .updater(userInfo.getPlayerId())
    //            .updateTime(DateUtil.getCurrentMilli())
    //            .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
    //            .build();
    //}
    //
    //private WheelDo<DiwataMayariSymbolDo> convertWheel(Object obj) {
    //    return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<DiwataMayariSymbolDo>>() {
    //    });
    //}
}
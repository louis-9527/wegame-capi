package com.wegame.capi.biz.module.sirena.game;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.SirenaConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.SirenaGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.domain.biz.template.sirena.SirenaBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.manager.SirenaBetRecordManager;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import com.wegame.capi.biz.module.sirena.SirenaSymbolUtil;
import com.wegame.capi.biz.module.sirena.reward.SirenaRewardHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.SirenaTagEnum;
import com.wegame.capi.common.enums.TemplateMultipleWarmUpEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TtlUtil;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.SIRENA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("sirenaGameTemplatePlay")
@ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
public class SirenaGameTemplatePlay extends AbstractGame<SirenaGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final int RECORD_FIRST_INDEX = 0;
    private static final GameInfoDo gameInfo = SirenaGameHelper.SIRENA_GAME_INFO;
    private static final Long NON_NEXT_TEMPLATE_ID = 0L;
    private static final TemplateTypeEnum DEFAULT_TEMPLATE = TemplateTypeEnum.SIRENA;

    @Autowired
    SirenaRewardHandler sirenaRewardHandler;
    @Autowired
    SirenaConfig sirenaConfig;
    @Autowired
    private SirenaBetRecordManager sirenaBetRecordManager;

    @Autowired
    BetRecordTemplateEsService<SirenaBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<SirenaGameRecordDo> play(BetDo betDo, GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<GameRecordTemplateDo> configRecordList = null;
        if (Objects.nonNull(gamePlay.getContinuePlayContext()) && Objects.nonNull(gamePlay.getContinuePlayContext().getParentBetRecordId())) {
            Pair<TemplateTypeEnum, Long> pair = loadTemplateFromContext(gamePlay);
            AssertUtil.assertNotNull(pair, ErrorCode.ERR_PARENT_BET_RECORD_ID);
            log.info("loadTemplateByParent: templateIndex : {}, templateId : {}", pair.getLeft(), pair.getRight());
            configRecordList = loadTemplateFromId(pair.getKey(), pair.getValue());
        } else {
            configRecordList = getTemplateConfigList(betDo, gamePlay);
        }
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER, "gameMultipleRecordDos is empty");
        List<SirenaGameRecordDo> ret = new ArrayList<>();
        // 获取首轮(parentId == 0 and index == 0（sirena 所有index都是0）)
        GameRecordTemplateDo firstRecord = configRecordList.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 按 batchId 分组,batchId和gameRecordId是同一个值。sirena没有消除轮，一个batchId只对应一条记录
        Map<Long, GameRecordTemplateDo> batchIdMap = configRecordList.stream()
                .collect(Collectors.toMap(GameRecordTemplateDo::getBatchId, recordDo -> recordDo, (recordDo1, recordDo2) -> recordDo1));

        // 处理首轮
        SirenaGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, super.generateGameRecordId(), RECORD_DEFAULT_PARENT_ID, firstRecord);
        ret.add(firstGameRecord);
        if (CollectionUtil.isNotEmpty(firstRecord.getGameRecordIds())) {
            // 首轮有免费轮
            LinkedList<SirenaEventFreeWheelHandleDo> taskLinkedList = new LinkedList<>();
            taskLinkedList.addLast(new SirenaEventFreeWheelHandleDo(firstGameRecord, firstRecord.getGameRecordIds()));
            // 迭代替代递归处理
            this.loopHandle(taskLinkedList, ret, gamePlay, batchIdMap);
        }

        // 设置结束
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        // 结果验证数据添加
        //// 总中奖金额
        long totalRewardAmount = ret.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        gamePlay.setOverallRewardAmount(totalRewardAmount);
        return ret;
    }

    private void loopHandle(LinkedList<SirenaEventFreeWheelHandleDo> taskLinkedList, List<SirenaGameRecordDo> ret, GamePlayDo gamePlay, Map<Long, GameRecordTemplateDo> batchIdMap) {
        // 迭代替代递归
        while (!taskLinkedList.isEmpty()) {
            // 逐个处理
            SirenaEventFreeWheelHandleDo task = taskLinkedList.pollFirst();
            SirenaGameRecordDo parentGameRecord = task.getParentGameRecord();
            for (Long oldSubGameRecordId : task.getOldSubGameRecordIds()) {
                GameRecordTemplateDo configRecordDo = batchIdMap.get(oldSubGameRecordId);
                AssertUtil.assertNotNull(configRecordDo, ErrorCode.ERR_INTER, "configRecordDo is null of batchId " + oldSubGameRecordId);
                SirenaGameRecordDo gameRecordDo = this.processGameRecord(gamePlay, super.generateGameRecordId(), parentGameRecord.getId(), configRecordDo);
                // 添加新的gameRecordId
                parentGameRecord.addGameRecordId(gameRecordDo.getId());
                ret.add(gameRecordDo);
                // 如果还有免费轮，则加入任务
                if (CollectionUtil.isNotEmpty(configRecordDo.getGameRecordIds())) {
                    taskLinkedList.addLast(new SirenaEventFreeWheelHandleDo(gameRecordDo, configRecordDo.getGameRecordIds()));
                }
            }
        }
    }

    private Pair<TemplateTypeEnum, Long> loadTemplateFromContext(GamePlayDo gamePlay) {
        if (gamePlay.getBetSubType() == BetSubTypeEnum.NORMAL_BET
                && gamePlay.getContinuePlayContext() != null && gamePlay.getContinuePlayContext().getParentBetRecordId() != null) {
            // 验证
            Long parentBetRecordId = gamePlay.getContinuePlayContext().getParentBetRecordId();
            long oneMonthAgoMillis = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli();

            BetRecordQuery betRecordQuery = BetRecordQuery.builder()
                    .playerId(UserInfoContext.getUserInfo().getPlayerId())
                    .startCreateTime(oneMonthAgoMillis)
                    .orderByClause("createTime")
                    .limit(1).build();
            BetRecordDo oneBetRecordByTimeLast = sirenaBetRecordManager.findLastOneGameRecord(betRecordQuery, GameTypeEnum.SIRENA);
            if (oneBetRecordByTimeLast == null
                    || parentBetRecordId.longValue() != oneBetRecordByTimeLast.getId().longValue()
                    || !BetSubTypeEnum.NORMAL_BET.getCode().equals(oneBetRecordByTimeLast.getBetSubType())
                    || gamePlay.getBetAmount().longValue() != oneBetRecordByTimeLast.getBetAmount().longValue()) {
                if (Objects.isNull(oneBetRecordByTimeLast)) {
                    log.info("oneBetRecordByTimeLast is null");
                } else {
                    log.info("oneBetRecordByTimeLast={}", oneBetRecordByTimeLast);
                }
                throw new BusinessException(ErrorCode.ERR_PARENT_BET_RECORD_ID);
            }
            AssertUtil.assertNotNull(oneBetRecordByTimeLast.getTemplateType(), ErrorCode.ERR_INTER);
            TemplateTypeEnum templateTypeEnum = TemplateTypeEnum.valueOf(oneBetRecordByTimeLast.getTemplateType());
            AssertUtil.assertNotNull(templateTypeEnum,ErrorCode.ERR_INTER);
            Long nextTemplateId = oneBetRecordByTimeLast.getNextTemplateId();
            AssertUtil.assertNotNull(nextTemplateId,ErrorCode.ERR_INTER);
            AssertUtil.assertTrue(!Objects.equals(nextTemplateId, NON_NEXT_TEMPLATE_ID),ErrorCode.ERR_INTER);
            return Pair.of(templateTypeEnum, oneBetRecordByTimeLast.getNextTemplateId());
        }
        return null;
    }

    private List<GameRecordTemplateDo> loadTemplateFromId(TemplateTypeEnum templateType, Long nextTemplateId) {
        SirenaBetRecordTemplateEsDo template = betRecordTemplateEsService.findById(templateType, nextTemplateId, SirenaBetRecordTemplateEsDo.class);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        List<GameRecordTemplateDo> configRecordList = decompressGameRecordList(template);
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        return configRecordList;
    }

    private List<GameRecordTemplateDo> getTemplateConfigList(BetDo betDo, GamePlayDo gamePlay) {
        // 1 加载倍率范围

        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(gamePlay.getBetType());
        log.info("random templateMultipleRange: {}", templateMultipleRange);
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);

        SirenaTagEnum tag = getTagEnum(gamePlay);
        AssertUtil.assertNotNull(tag, ErrorCode.ERR_INTER);
        TemplateTypeEnum templateType = DEFAULT_TEMPLATE;
        AssertUtil.assertNotNull(templateType, ErrorCode.ERR_INTER);
        log.info("random templateType: {}", templateType);
        // 2 加载记录idList
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByTagAndAvgMultipleRange(templateType, tag.getCode(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), SirenaBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);

        // 3 随机一个betRecordId
        Long betRecordId = getRandomElement(betRecordIdList);

        // 4 加载模板 & 解压
        log.info("templatePlay: templateIndex: {}, tag: {}, templateId: {}, range: {}-{}", templateType.getBetRecordTemplateName(),tag.getCode(), betRecordId,  templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        SirenaBetRecordTemplateEsDo template = loadTemplate(tag, templateType, betRecordId, templateMultipleRange);
        log.info("templatePlay: tag: {}, avgMultiple:{}, multiple:{},", template.getTag(), template.getAvgMultiple(), template.getMultiple());
        List<GameRecordTemplateDo> configRecordList = decompressGameRecordList(template);
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        gamePlay.setTemplateId(betRecordId);
        gamePlay.setTemplateMultipleRange(templateMultipleRange);
        if (Objects.nonNull(template.getNextBetRecordId()) && !Objects.equals(template.getNextBetRecordId(), NON_NEXT_TEMPLATE_ID)) {
            betDo.setNextTemplateId(template.getNextBetRecordId());
            betDo.setTemplateType(templateType.name());
        }
        return configRecordList;
    }

    private List<GameRecordTemplateDo> decompressGameRecordList(SirenaBetRecordTemplateEsDo templateEsDo) {
        return FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(templateEsDo.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
    }

    private SirenaBetRecordTemplateEsDo loadTemplate(SirenaTagEnum tag, TemplateTypeEnum templateType, Long templateId, TemplateMultipleRange templateMultipleRange) {
        SirenaBetRecordTemplateEsDo template;
        TemplateMultipleWarmUpEnum warmUpEnum = TemplateMultipleWarmUpEnum.valueOfCode(templateType.getGameType());
        AssertUtil.assertNotNull(warmUpEnum, ErrorCode.ERR_INTER);
        if (warmUpEnum.canWarmUp(tag.getCode(), templateMultipleRange.getEnd())) {
            // 已经被预热的倍率，直接走缓存
            template = betRecordTemplateEsService.getTemplateWithLocalCache(templateType, templateId, SirenaBetRecordTemplateEsDo.class);
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(templateType, templateId, SirenaBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }

    private SirenaTagEnum getTagEnum(GamePlayDo gamePlay) {
        if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
            return SirenaTagEnum.SIRENA_BUY_BET;
        }
        if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.NORMAL_BET)) {
            return SirenaTagEnum.SIRENA_FIRST_ROUND;
        }
        return null;
    }

    /**
     * 构建并处理投注记录
     */
    private SirenaGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            GameRecordTemplateDo recordDo) {

        WheelDo<SirenaSymbolDo> sirenaSymbolWheel = this.convertWheel(recordDo.getWheel());
        RewardResultDo<SirenaSymbolDo> rewardResult = sirenaRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, sirenaSymbolWheel, BigDecimal.ZERO));

        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), sirenaSymbolWheel, rewardResult);
    }

    private SirenaGameRecordDo buildGameRecord(Long id,
                                               Long parentId,
                                               Long betRecordId,
                                               WheelDo<SirenaSymbolDo> wheel,
                                               RewardResultDo<SirenaSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return SirenaGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(id)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    private WheelDo<SirenaSymbolDo> convertWheel(Object obj) {
        WheelDo<SirenaSymbolDo> wheelDo =  JSON.parseObject(com.alibaba.fastjson.JSON.toJSONString(obj), new TypeReference<WheelDo<SirenaSymbolDo>>() {});
        SirenaSymbolUtil.handleWild(wheelDo.getSymbolArr());
        return wheelDo;
    }

    private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betTypeEnum) {
        String templateMultipleRange;
        SirenaConfig.TemplateRangeConfig config = sirenaConfig.getTemplateRangeConfig();
        if (Objects.equals(betTypeEnum, BetTypeEnum.NORMAL_BET)) {
            templateMultipleRange = randomService.randomKeyByWeight(config.getMainGame());
        } else {
            templateMultipleRange = randomService.randomKeyByWeight(config.getFreeGame());
        }
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }

    private Long getRandomElement(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    @Data
    @SuperBuilder
    private static class SirenaEventFreeWheelHandleDo {

        public SirenaEventFreeWheelHandleDo(SirenaGameRecordDo parentGameRecord, List<Long> oldSubGameRecordIds) {
            AssertUtil.assertNotNull(parentGameRecord, ErrorCode.ERR_INTER, "parentGameRecord is null");
            AssertUtil.assertTrue(CollectionUtil.isNotEmpty(oldSubGameRecordIds), ErrorCode.ERR_INTER, "oldSubGameRecordIds is empty");
            this.parentGameRecord = parentGameRecord;
            this.oldSubGameRecordIds = oldSubGameRecordIds;
        }

        // parentRecord
        private SirenaGameRecordDo parentGameRecord;
        // 时间配置中的子gameRecordId列表
        private List<Long> oldSubGameRecordIds;
    }
}
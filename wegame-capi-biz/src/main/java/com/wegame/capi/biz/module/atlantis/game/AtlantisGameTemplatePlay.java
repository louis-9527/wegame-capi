package com.wegame.capi.biz.module.atlantis.game;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.AtlantisGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.bizevent.GameBizEventDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.template.atlantis.AtlantisBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.atlantis.AtlantisGameHelper;
import com.wegame.capi.biz.module.atlantis.reward.AtlantisRewardHandler;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.BizEventEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.enums.TemplateTypeEnum.ATLANTIS;

@Slf4j
@Component("atlantisGameTemplatePlay")
@ConditionalOnProperty(name = Constants.ATLANTIS_PROP, havingValue = TRUE_VAL)
public class AtlantisGameTemplatePlay extends AbstractGame<AtlantisGameRecordDo> {
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = AtlantisGameHelper.ATLANTIS_GAME_INFO;

    @Autowired
    AtlantisRewardHandler atlantisRewardHandler;
    @Autowired
    AtlantisConfig atlantisConfig;
    @Autowired
    BetRecordTemplateEsService<AtlantisBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<AtlantisGameRecordDo> play(BetDo bet, GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        TemplateMultipleRange templateMultipleRange = this.getTemplateMultipleRange(gamePlay);

        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(ATLANTIS, gamePlay.getBetType(), this.getTemplateBetSubType(gamePlay.getBetSubType()), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), AtlantisBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = this.getRandomElement(betRecordIdList);
        log.info("betRecordId:{}", betRecordId);
        List<GameRecordTemplateDo> configRecordList;
        AtlantisBetRecordTemplateEsDo template;

        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.ATLANTIS, gamePlay.getBetType(), this.getTemplateBetSubType(gamePlay.getBetSubType()), templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存.atlantis只存在本地缓存
            template = betRecordTemplateEsService.getTemplateWithLocalCache(ATLANTIS, betRecordId, AtlantisBetRecordTemplateEsDo.class);
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(ATLANTIS, betRecordId, AtlantisBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        if (CollectionUtils.isEmpty(configRecordList)) {
            log.error("template is empty, betType:{}, range start: {}, end: {}", gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        }

        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER, "configRecordList is empty");
        List<AtlantisGameRecordDo> ret = Lists.newArrayList();
        // 获取首轮(parentId == 0 and index == 0)
        GameRecordTemplateDo firstRecord = configRecordList.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<GameRecordTemplateDo>> groupedByBatchId = configRecordList.stream()
                .collect(Collectors.groupingBy(GameRecordTemplateDo::getBatchId));

        List<GameRecordTemplateDo> recordBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(GameRecordTemplateDo::getIndex))
                .collect(Collectors.toList());

        // 处理首轮-返回最后一回合的信息
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        OneWheelPlayResult firstWheelPlayResult = this.oneWheelPlay(gamePlay, recordBatchList, RECORD_DEFAULT_PARENT_ID, ret, BigDecimal.ZERO, totalRewardAmount, new AtomicInteger(0));

        // 处理免费轮
        if (Objects.nonNull(firstWheelPlayResult) && firstWheelPlayResult.hasFreeWheel()) {
            this.freeWheelPlay(totalRewardAmount, gamePlay, ret, firstWheelPlayResult, groupedByBatchId);
        }
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }


        // 结果验证数据添加
        gamePlay.setTemplateId(betRecordId);
        gamePlay.setOverallRewardAmount(totalRewardAmount.get());
        gamePlay.setTemplateMultipleRange(templateMultipleRange);
        return ret;
    }

    /**
     * 免费轮玩
     *
     * @param totalRewardAmount    累计中奖金额
     * @param gamePlay             play
     * @param ret                  返回
     * @param firstWheelPlayResult 首轮结果
     */
    private void freeWheelPlay(AtomicLong totalRewardAmount, GamePlayDo gamePlay, List<AtlantisGameRecordDo> ret, OneWheelPlayResult firstWheelPlayResult, Map<Long, List<GameRecordTemplateDo>> groupedByBatchId) {
        LinkedList<OneWheelPlayResult> queue = new LinkedList<>();
        queue.addLast(firstWheelPlayResult);
        // 免费轮单独算
        AtomicInteger totalRewardCount = new AtomicInteger(0);
        while (!queue.isEmpty()) {
            // 一个一个处理
            List<Long> configGameRecordIdList = firstWheelPlayResult.getConfigGameRecordIdList();
            Long parentId = firstWheelPlayResult.getLastRoundGameRecordId();
            for (Long configGameRecordId : configGameRecordIdList) {
                // 一轮记录信息
                List<GameRecordTemplateDo> recordBatchList = groupedByBatchId.getOrDefault(configGameRecordId, Collections.emptyList())
                        .stream()
                        .sorted(Comparator.comparing(GameRecordTemplateDo::getIndex))
                        .collect(Collectors.toList());

                // 处理一轮
                OneWheelPlayResult oneWheelPlayResult = this.oneWheelPlay(gamePlay, recordBatchList, parentId, ret, BigDecimal.ZERO, totalRewardAmount, totalRewardCount);
                // 有免费轮则继续处理
                if (Objects.nonNull(oneWheelPlayResult) && oneWheelPlayResult.hasFreeWheel()) {
                    queue.addLast(oneWheelPlayResult);
                }
            }
        }
    }

    /**
     * 单轮玩
     *
     * @param gamePlay              play
     * @param recordBatchList       统一批次config List.根据index排序的
     * @param recordDefaultParentId 默认父级ID
     * @param ret                   返回
     * @param totalMultiple         0
     * @param totalRewardAmount     累计中奖金额
     * @return OneWheelPlayResult
     */
    private OneWheelPlayResult oneWheelPlay(GamePlayDo gamePlay, List<GameRecordTemplateDo> recordBatchList, Long recordDefaultParentId, List<AtlantisGameRecordDo> ret, BigDecimal totalMultiple, AtomicLong totalRewardAmount, AtomicInteger totalRewardCount) {
        AssertUtil.assertNotEmpty(recordBatchList, ErrorCode.ERR_INTER, "recordBatchList is empty");
        // 定义返回需要的数据
        AtlantisGameRecordDo lastRoundGameRecord = null;
        Long parentId = recordDefaultParentId;
        Long batchId = null;
        // 处理回合
        for (int i = 0; i < recordBatchList.size(); i++) {
            // 拿到回合
            GameRecordTemplateDo round = recordBatchList.get(i);
            long roundGameRecordId = super.generateGameRecordId();
            // 首回合的recordId为batchId
            if (i == 0) {
                batchId = roundGameRecordId;
            }
            AtlantisGameRecordDo roundAtlantisGameRecordDo = this.processGameRecord(gamePlay, roundGameRecordId, parentId, batchId, round, totalMultiple, totalRewardAmount, totalRewardCount);
            // 添加到结果
            ret.add(roundAtlantisGameRecordDo);
            // 最后一回合的实际结果
            if (i == recordBatchList.size() - 1) {
                lastRoundGameRecord = roundAtlantisGameRecordDo;
            }
            // parentId
            parentId = roundGameRecordId;
        }

        // 返回
        return OneWheelPlayResult.builder()
                .lastRoundConfigRecord(recordBatchList.get(recordBatchList.size() - 1))
                .lastRoundGameRecord(lastRoundGameRecord)
                .build();

    }


    /**
     * 构建并处理投注记录
     */
    private AtlantisGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            GameRecordTemplateDo recordDo,
            BigDecimal totalMultiple,
            AtomicLong totalRewardAmount,
            AtomicInteger totalRewardCount) {

        // 获取滚轮
        WheelDo<SymbolDo> symbolWheel = this.convertWheel(recordDo.getWheel());
        // 获取事件
        List<GameBizEventDo> configGameEventList = this.getGameEventList(recordDo);
        // 处理空值
        RewardResultDo<SymbolDo> rewardResult = this.rewardHandler(gamePlay, symbolWheel, totalMultiple, totalRewardAmount, configGameEventList, totalRewardCount);
        // 构建时还原
        AtlantisGameRecordDo atlantisGameRecordDo = this.buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, symbolWheel, rewardResult);
        // 重新构建业务事件
        List<GameBizEventDo> gameEventList = this.reBuildBizEventList(configGameEventList, rewardResult);
        atlantisGameRecordDo.setBizEventList(gameEventList);
        return atlantisGameRecordDo;
    }

    private List<GameBizEventDo> getGameEventList(GameRecordTemplateDo recordDo) {
        if (Objects.isNull(recordDo.getExtInfo())) {
            return null;
        } else {
            Object extInfo = recordDo.getExtInfo();
            String jsonString = JSON.toJSONString(extInfo);
            return JSON.parseArray(jsonString, GameBizEventDo.class);
        }
    }

    private RewardResultDo<SymbolDo> rewardHandler(GamePlayDo gamePlay, WheelDo<SymbolDo> wheel, BigDecimal currentMultiple, AtomicLong totalRewardAmount, List<GameBizEventDo> gameEventList, AtomicInteger totalRewardCount) {
        // 中奖处理
        RewardHandleDo<SymbolDo> rewardHandle = RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, currentMultiple, totalRewardAmount.get(), totalRewardCount.get(), gameEventList);
        RewardResultDo<SymbolDo> rewardResult = atlantisRewardHandler.handle(rewardHandle);
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        if (CollectionUtil.isNotEmpty(rewardResult.getRewardLineDetailList())) {
            totalRewardCount.addAndGet(rewardResult.getRewardLineDetailList().size());
        }
        return rewardResult;
    }

    private List<GameBizEventDo> reBuildBizEventList(final List<GameBizEventDo> configGameEventList, final RewardResultDo<SymbolDo> rewardResult) {
        List<GameBizEventDo> effectBizEventList = rewardResult.getEffectBizEventList();
        // 是否包含黄金奶牛
        if (CollectionUtil.isEmpty(effectBizEventList)) {
            return configGameEventList;
        }
        // configGameEventList 为空说明模版有问题
        AssertUtil.assertNotEmpty(configGameEventList, ErrorCode.ERR_PARAM, "configGameEventList is empty");
        List<GameBizEventDo> rewardGoldenCowList = effectBizEventList.stream().filter(gameBizEventDo -> gameBizEventDo.getBizEventType() == BizEventEnum.EVENT_GOLDEN_COW).collect(Collectors.toList());
        List<GameBizEventDo> gameBizEventList = configGameEventList.stream().filter(gameBizEventDo -> gameBizEventDo.getBizEventType() != BizEventEnum.EVENT_GOLDEN_COW).collect(Collectors.toList());
        gameBizEventList.addAll(rewardGoldenCowList);
        gameBizEventList.sort(Comparator.comparing(GameBizEventDo::getSort));
        return gameBizEventList;
    }


    private BetSubTypeEnum getTemplateBetSubType(BetSubTypeEnum betSubType) {
        // normal 和 luck boy(EX_BET)一个模版，统一使用Normal
        if (betSubType == BetSubTypeEnum.EX_BET) {
            return BetSubTypeEnum.NORMAL_BET;
        }
        return betSubType;
    }

    private AtlantisGameRecordDo buildGameRecord(Long id,
                                                 Long parentId,
                                                 Long betRecordId,
                                                 Long batchId,
                                                 WheelDo<SymbolDo> wheel,
                                                 RewardResultDo<SymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);

        return AtlantisGameRecordDo.builder()
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
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    private TemplateMultipleRange getTemplateMultipleRange(GamePlayDo gamePlay) {
        LinkedHashMap<String, Integer> multipleRangeMap = atlantisConfig.getTemplateRangeConfig().getTemplateRangeMap().get(gamePlay.getBetSubType().getCode());
        AssertUtil.assertNotNull(multipleRangeMap, ErrorCode.ERR_INTER, "multipleRangeMap is null");
        String templateIdRange = randomService.randomKeyByWeight(multipleRangeMap);
        AssertUtil.assertStrNotEmpty(templateIdRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateIdRange);
    }

    private Long getRandomElement(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    private WheelDo<SymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<SymbolDo>>() {
        });
    }

    /**
     * 单轮玩结果
     */
    @Data
    @SuperBuilder
    public static class OneWheelPlayResult {
        /**
         * 最后一回合的配置模版记录
         */
        private GameRecordTemplateDo lastRoundConfigRecord;
        /**
         * 最后一回合的实际gameRecord
         */
        private AtlantisGameRecordDo lastRoundGameRecord;

        public boolean hasFreeWheel() {
            return Objects.nonNull(lastRoundConfigRecord) && CollectionUtil.isNotEmpty(this.lastRoundConfigRecord.getGameRecordIds());
        }

        public List<Long> getConfigGameRecordIdList() {
            return lastRoundConfigRecord.getGameRecordIds();
        }

        public Long getLastRoundGameRecordId() {
            return lastRoundGameRecord.getId();
        }

    }

}
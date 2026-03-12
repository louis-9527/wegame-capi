package com.wegame.capi.biz.module.lamang.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.config.domain.TemplateSelectionContext;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.lamang.LamangGameHelper;
import com.wegame.capi.biz.module.lamang.LamangSymbolUtil;
import com.wegame.capi.biz.module.lamang.reward.LamangRewardHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.AbTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.enums.TemplateTypeEnum.LAM_ANG;

@Slf4j
@Component("lamangGameTemplatePlay")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangGameTemplatePlay extends AbstractGame<LamangGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final int RECORD_FIRST_INDEX = 0;
    private static final GameInfoDo gameInfo = LamangGameHelper.LAMANG_GAME_INFO;

    @Autowired
    LamangRewardHandler lamangRewardHandler;
    @Autowired
    LamangConfig lamangConfig;

    @Autowired
    BetRecordTemplateEsService<LamangBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<LamangGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<GameRecordTemplateDo> configRecordList = getTemplateConfigList(gamePlay, userInfo);
        AssertUtil.assertNotNull(configRecordList, ErrorCode.ERR_INTER);

        List<LamangGameRecordDo> ret = new ArrayList<>();
        // 获取首轮(batchId == 0 and index == 0)
        GameRecordTemplateDo firstRecord = configRecordList.stream()
                .filter(record -> Objects.equals(RECORD_DEFAULT_PARENT_ID, record.getParentId()) && Objects.equals(RECORD_FIRST_INDEX, record.getIndex()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        long firstGameRecordId = super.generateGameRecordId();
        LamangGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord, null);
        ret.add(firstGameRecord);
        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<GameRecordTemplateDo>> groupedByBatchId = configRecordList.stream()
                .collect(Collectors.groupingBy(GameRecordTemplateDo::getBatchId));

        // 获取首批次并排序
        List<GameRecordTemplateDo> firstBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(GameRecordTemplateDo::getIndex))
                .collect(Collectors.toList());
        // 处理每个批次
        LamangGameRecordDo lastGameRecord = firstGameRecord;

        for (GameRecordTemplateDo recordDo : firstBatchList) {
            // 非首回合
            if (!Objects.equals(RECORD_DEFAULT_PARENT_ID, recordDo.getParentId())) {
                // 处理首轮普通轮
                long gameRecordId = generateGameRecordId();
                // 用上一回合的倍率
                LamangGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(), lastGameRecord.getBatchId(), recordDo, lastGameRecord.getRewardResult().getTotalMultiple());
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
        // 结果验证数据添加
        //// 总中奖金额
        long totalRewardAmount = ret.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        gamePlay.setOverallRewardAmount(totalRewardAmount);
        return ret;
    }

    private List<GameRecordTemplateDo> getTemplateConfigList(GamePlayDo gamePlay, UserInfo userInfo) {
        // 1 构建选择上下文，按策略链决定模板类型
        AbTypeEnum abType = AbTypeEnum.valueOfId(userInfo.getUt());
        AssertUtil.assertNotNull(abType, ErrorCode.ERR_NOT_LOGIN);
        TemplateSelectionContext ctx = TemplateSelectionContext.builder()
                .promotionType(gamePlay.getBetPromotionType())
                .abType(abType)
                .build();
        TemplateTypeEnum templateType = lamangConfig.getTemplateConfig().select(ctx);
        AssertUtil.assertNotNull(templateType, ErrorCode.ERR_INTER);
        log.info("templateType selected: {}, promotionType: {}, abType: {}", templateType, ctx.getPromotionType(), abType);

        // 2 加载倍率范围
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(templateType, gamePlay.getBetType());
        log.info("random templateMultipleRange: {}", templateMultipleRange);
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);

        // 3 加载记录idList
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(LAM_ANG, gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), LamangBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);

        // 4 随机一个betRecordId
        Long betRecordId = getRandomElement(betRecordIdList);

        // 5 加载模板 & 解压
        log.info("templatePlay: templateType: {}, betType: {}, templateId: {}, range: {}-{}",
                templateType.getBetRecordTemplateName(), gamePlay.getBetType().getCode(), betRecordId, templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        LamangBetRecordTemplateEsDo template = loadTemplate(gamePlay, templateType, betRecordId, templateMultipleRange);
        List<GameRecordTemplateDo> configRecordList = decompressGameRecordList(template);
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        gamePlay.setTemplateId(betRecordId);
        gamePlay.setTemplateMultipleRange(templateMultipleRange);
        return configRecordList;
    }

    private List<GameRecordTemplateDo> decompressGameRecordList(LamangBetRecordTemplateEsDo templateEsDo) {
        return FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(templateEsDo.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
    }

    private LamangBetRecordTemplateEsDo loadTemplate(GamePlayDo gamePlay, TemplateTypeEnum templateType, Long betRecordId, TemplateMultipleRange templateMultipleRange) {
        LamangBetRecordTemplateEsDo template;
        if (lamangConfig.getTemplateConfig().supportWarmUp(gamePlay.getBetType(), templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存
            template = betRecordTemplateEsService.getTemplateWithLocalCache(templateType, betRecordId, LamangBetRecordTemplateEsDo.class);
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(templateType, betRecordId, LamangBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }


    /**
     * 递归处理免费轮，确保同层级先处理完，再进入下一层
     */
    private void processFreeRounds(
            List<LamangGameRecordDo> ret,
            GamePlayDo gamePlay,
            GameRecordTemplateDo recordDo,
            LamangGameRecordDo parent,
            Map<Long, List<GameRecordTemplateDo>> groupedByBatchId) {
        Map<GameRecordTemplateDo, LamangGameRecordDo> nextLevelGameRecordMap = new HashMap<>();

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            List<GameRecordTemplateDo> currentLevelRecords = groupedByBatchId.get(batchId);
            currentLevelRecords.sort(Comparator.comparing(GameRecordTemplateDo::getIndex));
            // 处理当前层级的每个记录
            LamangGameRecordDo preGameRecord = null;
            for (GameRecordTemplateDo record : currentLevelRecords) {
                long gameRecordId = generateGameRecordId();
                LamangGameRecordDo gameRecord = null;
                if (record.getIndex() == 0) {
                    gameRecordIds.add(gameRecordId);
                    // 首回合倍率为空
                    gameRecord = processGameRecord(gamePlay, gameRecordId, parent.getId(), gameRecordId, record, null);
                    ret.add(gameRecord);
                } else {
                    // 后续回合倍率传入前一回合的倍率
                    BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
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
        for (Map.Entry<GameRecordTemplateDo, LamangGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            GameRecordTemplateDo key = entry.getKey();
            LamangGameRecordDo value = entry.getValue();
            processFreeRounds(ret, gamePlay, key, value, groupedByBatchId);
        }
    }

    /**
     * 构建并处理投注记录
     */
    private LamangGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            GameRecordTemplateDo recordDo,
            BigDecimal totalMultiple) {

        // 确定 `TotalMultiple` 参数
        WheelDo<LamangSymbolDo> lamangSymbolWheel = this.convertWheel(recordDo.getWheel());
        LamangSymbolDo[][] symbolArr = lamangSymbolWheel.getSymbolArr();
        // 处理空值
        lamangSymbolWheel.setSymbolArr(LamangSymbolUtil.copySymbolArrWithPlaceHolder(symbolArr));
        RewardResultDo<LamangSymbolDo> rewardResult = lamangRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, lamangSymbolWheel, totalMultiple));
        // 构建时还原
        lamangSymbolWheel.setSymbolArr(symbolArr);
        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, lamangSymbolWheel, rewardResult);
    }

    private LamangGameRecordDo buildGameRecord(Long id,
                                               Long parentId,
                                               Long betRecordId,
                                               Long batchId,
                                               WheelDo<LamangSymbolDo> wheel,
                                               RewardResultDo<LamangSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        int drawTimes = LamangSymbolUtil.countNewScatterAndScatter1InWheel(wheel.getSymbolArr());
        return LamangGameRecordDo.builder()
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
                .jackpotDrawTimes(drawTimes)
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    private WheelDo<LamangSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<LamangSymbolDo>>() {
        });
    }

    private TemplateMultipleRange getTemplateMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betTypeEnum) {
        String templateMultipleRange;
        LamangConfig.TemplateRangeConfig config = lamangConfig.getTemplateConfig().getTemplateRangeConfig(templateType);
        AssertUtil.assertNotNull(config, ErrorCode.ERR_INTER);
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
}
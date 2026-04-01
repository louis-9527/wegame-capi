package com.wegame.capi.biz.module.malakas.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.biz.module.malakas.reward.MalakasRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.handleSymbolNullArr;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.postProcessPlaceholderSymbol;

@Slf4j
@Component
public class MalakasGameCommonTemplatePlay implements TemplateGame<MalakasGameRecordDo, MalakasBetRecordTemplateEsDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = MALAKAS_GAME_INFO;

    @Autowired
    MalakasRewardHandler malakasRewardHandler;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public List<MalakasGameRecordDo> templatePlay(BetDo betDo, GamePlayDo gamePlayDo, MalakasBetRecordTemplateEsDo template) {
        AssertUtil.assertNotNull(gamePlayDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        List<GameRecordTemplateDo> templateList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        AssertUtil.assertNotEmpty(templateList, ErrorCode.ERR_INTER);
        // 预处理：排序升序（batchId， index）
        templateList = templateList.stream()
                .sorted(Comparator.comparing(GameRecordTemplateDo::getBatchId).thenComparing(GameRecordTemplateDo::getIndex)) // 第二排序（次，仅主字段相同时生效）
                .collect(Collectors.toList());
        Map<Long, Long> idMap = buildIdMap(templateList);
        return convertTemplateList(gamePlayDo, templateList, idMap);
    }

    private List<MalakasGameRecordDo> convertTemplateList(GamePlayDo gamePlayDo, List<GameRecordTemplateDo> templateList, Map<Long, Long> idMap) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();;
        Long maxBatchId = Collections.max(templateList, Comparator.comparing(GameRecordTemplateDo::getBatchId)).getBatchId();
        BigDecimal totalMultiple = BigDecimal.ZERO;
        boolean resetTotalMultipleFlag = false;
        for (GameRecordTemplateDo templateItem : CollectionUtils.emptyIfNull(templateList)) {
            boolean lastGameFlag = Objects.equals(templateItem.getBatchId(), maxBatchId);
            MalakasGameRecordDo gameRecordDo = this.buildGameRecord(gamePlayDo, templateItem, totalMultiple, idMap, lastGameFlag);
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
        // 计算总中奖金额（设置到上下文用于playResultValidate校验）
        gamePlayDo.setOverallRewardAmount(getTotalRewardAmount(ret));
        // 返回
        return ret;
    }

    private static long getTotalRewardAmount(List<MalakasGameRecordDo> ret) {
        return ret.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
    }

    private WheelDo<MalakasSymbolDo> convertWheel(Object obj) {
        WheelDo<MalakasSymbolDo> wheel = JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<MalakasSymbolDo>>() {
        });
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        // 空元素置为占位元素（以用于中奖计算）
        wheel.setSymbolArr(handleSymbolNullArr(wheel.getSymbolArr()));
        return wheel;
    }

    private Map<Long, Long> buildIdMap(List<GameRecordTemplateDo> configRecordList) {
        Map<Long, Long> idMap = new HashMap<>();;
        idMap.put(RECORD_DEFAULT_PARENT_ID, RECORD_DEFAULT_PARENT_ID);
        for (GameRecordTemplateDo item : CollectionUtils.emptyIfNull(configRecordList)) {
            if (!idMap.containsKey(item.getId())) {
                idMap.put(item.getId(), snowflakeIdGenerator.nextId());
            }
        }
        return idMap;
    }

    private MalakasGameRecordDo buildGameRecord(GamePlayDo gamePlay, GameRecordTemplateDo template, BigDecimal totalMultiple, Map<Long, Long> idMap, boolean lastGameFlag) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        WheelDo<MalakasSymbolDo> wheel = convertWheel(template.getWheel());
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, totalMultiple));
        Long id = idMap.get(template.getId());
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        Long parentId = idMap.get(template.getParentId());
        AssertUtil.assertNotNull(parentId, ErrorCode.ERR_INTER);
        Long batchId = idMap.get(template.getBatchId());
        AssertUtil.assertNotNull(batchId, ErrorCode.ERR_INTER);
        return MalakasGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(gamePlay.getBetRecordId())
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(template.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(Optional.ofNullable(template.getGameRecordIds())
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
}
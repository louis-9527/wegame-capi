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
    public SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public List<MalakasGameRecordDo> templatePlay(BetDo betDo, GamePlayDo gamePlayDo, MalakasBetRecordTemplateEsDo template) {
        AssertUtil.assertNotNull(gamePlayDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        List<GameRecordTemplateDo> configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        if (CollectionUtils.isEmpty(configRecordList)) {
            log.error("template is empty, betType:{}", gamePlayDo.getBetType());
        }
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        Map<Long, Long> idMap = new HashMap<>();
        idMap.put(RECORD_DEFAULT_PARENT_ID, RECORD_DEFAULT_PARENT_ID);
        //configRecordList = configRecordList.stream().sorted(Comparator.comparing(GameRecordTemplateDo::getBatchId)).sorted(Comparator.comparing(GameRecordTemplateDo::getIndex)).collect(Collectors.toList());
        configRecordList = configRecordList.stream()
                .sorted(
                        Comparator.comparing(GameRecordTemplateDo::getBatchId)  // 第一排序（主）
                                .thenComparing(GameRecordTemplateDo::getIndex) // 第二排序（次，仅主字段相同时生效）
                )
                .collect(Collectors.toList());
        for (GameRecordTemplateDo item : CollectionUtils.emptyIfNull(configRecordList)) {
            if (!idMap.containsKey(item.getId())) {
                idMap.putIfAbsent(item.getId(), generateGameRecordId());
            }
            //if (!idMap.containsKey(item.getParentId())) {
            //    idMap.putIfAbsent(item.getParentId(), Objects.equals(item.getParentId(), RECORD_DEFAULT_PARENT_ID) ? RECORD_DEFAULT_PARENT_ID : generateGameRecordId());
            //}
            //if (!idMap.containsKey(item.getBatchId())) {
            //    idMap.putIfAbsent(item.getBatchId(), generateGameRecordId());
            //}
            //for (Long gameRecordId : item.getGameRecordIds()) {
            //    if (!idMap.containsKey(gameRecordId)) {
            //        idMap.putIfAbsent(gameRecordId, generateGameRecordId());
            //    }
            //}
        }
        // 数据模型转换
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        Long maxBatchId = Collections.max(configRecordList, Comparator.comparing(GameRecordTemplateDo::getBatchId)).getBatchId();
        BigDecimal totalMultipleAmount = BigDecimal.ZERO;
        for (GameRecordTemplateDo templateItem : configRecordList) {
            boolean lastGameFlag = Objects.equals(templateItem.getBatchId(), maxBatchId);
            MalakasGameRecordDo gameRecordDo = this.buildGameRecord(gamePlayDo, templateItem, totalMultipleAmount, idMap, lastGameFlag);
            totalMultipleAmount = gameRecordDo.getRewardResult().getTotalMultiple();
            ret.add(gameRecordDo);
        }
        // 占位元素-将占位元素设置为空:
        postProcessPlaceholderSymbol(ret);
        return ret;
    }

    private MalakasGameRecordDo buildGameRecord(GamePlayDo gamePlay, GameRecordTemplateDo template, BigDecimal totalMultiple, Map<Long, Long> map, boolean lastGameFlag) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);

        WheelDo<MalakasSymbolDo> wheel = this.convertWheel(template.getWheel());
        // 处理空值
        wheel.setSymbolArr(handleSymbolNullArr(wheel.getSymbolArr()));
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, totalMultiple));

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long gameRecordId : CollectionUtils.emptyIfNull(template.getGameRecordIds())) {
            gameRecordIds.add(map.get(gameRecordId));
        }

        Long id = map.get(template.getId());
        if (Objects.isNull(template.getId())) {
            //log.error("get id is null template.id={}", template.getId());
        }
        Long parentId = map.get(template.getParentId());
        if (Objects.isNull(template.getParentId())) {
            //log.error("get id is null template.parentId={}", template.getParentId());
        }
        Long betRecordId = gamePlay.getBetRecordId();
        if (Objects.isNull(template.getBetRecordId())) {
            //log.error("get betRecordId is null template.betRecordId={}", template.getBetRecordId());
        }
        Long batchId = map.get(template.getBatchId());
        if (Objects.isNull(template.getBatchId())) {
            //log.error("get batchId is null template.batchId={}", template.getBatchId());
        }
        return MalakasGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(template.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(gameRecordIds)
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

    private long generateGameRecordId() {
        return snowflakeIdGenerator.nextId();
    }
}
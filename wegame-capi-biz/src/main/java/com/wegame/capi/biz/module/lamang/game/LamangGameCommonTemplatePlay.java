package com.wegame.capi.biz.module.lamang.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.IdHelper;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.biz.module.lamang.LamangGameHelper;
import com.wegame.capi.biz.module.lamang.LamangSymbolUtil;
import com.wegame.capi.biz.module.lamang.reward.LamangRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
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

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.getTotalRewardAmount;
import static com.wegame.capi.biz.module.lamang.LamangSymbolUtil.convertWheel;
import static com.wegame.capi.biz.module.lamang.LamangSymbolUtil.postProcessPlaceholderSymbol;
import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("lamangGameCommonTemplatePlay")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangGameCommonTemplatePlay implements TemplateGame<LamangGameRecordDo, LamangBetRecordTemplateEsDo> {


    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final int FIRST_WHEEL_INDEX = 0;
    private static final GameInfoDo gameInfo = LamangGameHelper.LAMANG_GAME_INFO;
    //
    //@Autowired
    //LamangConfig lamangConfig;
    //@Autowired
    //SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    IdHelper idHelper;
    @Autowired
    LamangRewardHandler lamangRewardHandler;

    @Override
    public List<LamangGameRecordDo> templatePlay(BetDo betDo, GamePlayDo gamePlayDo, LamangBetRecordTemplateEsDo template) {
        AssertUtil.assertNotNull(gamePlayDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        List<GameRecordTemplateDo> templateList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        AssertUtil.assertNotEmpty(templateList, ErrorCode.ERR_INTER);
        // 预处理：排序升序（batchId， index）
        templateList = templateList.stream()
                .sorted(Comparator.comparing(GameRecordTemplateDo::getBatchId).thenComparing(GameRecordTemplateDo::getIndex))
                .collect(Collectors.toList());
        // 模版列表转换
        return convertTemplateList(gamePlayDo, templateList);
    }

    private List<LamangGameRecordDo> convertTemplateList(GamePlayDo gamePlayDo, List<GameRecordTemplateDo> templateList) {
        Map<Long, Long> idMap = idHelper.buildIdMap(templateList);
        List<LamangGameRecordDo> ret = new ArrayList<>();
        Long maxBatchId = Collections.max(templateList, Comparator.comparing(GameRecordTemplateDo::getBatchId)).getBatchId();
        BigDecimal lastWheelMultiple = null;
        Long currentTotalRewardAmount = 0L;
        for (GameRecordTemplateDo templateItem : CollectionUtils.emptyIfNull(templateList)) {
            boolean lastGameFlag = Objects.equals(templateItem.getBatchId(), maxBatchId);
            LamangGameRecordDo recordDo = buildGameRecord(currentTotalRewardAmount, gamePlayDo, templateItem, lastWheelMultiple, idMap, lastGameFlag);
            lastWheelMultiple = recordDo.getRewardResult().getTotalMultiple();
            ret.add(recordDo);
            currentTotalRewardAmount = getTotalRewardAmount(ret);
        }
        // 占位元素-将占位元素设置为空:
        postProcessPlaceholderSymbol(ret);
        // 计算总中奖金额（设置到上下文用于playResultValidate校验）
        gamePlayDo.setOverallRewardAmount(getTotalRewardAmount(ret));
        // 返回
        return ret;
    }

    private LamangGameRecordDo buildGameRecord(Long currentTotalRewardAmount, GamePlayDo gamePlay, GameRecordTemplateDo template, BigDecimal lastWheelMultiple, Map<Long, Long> idMap, boolean lastGameFlag) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        WheelDo<LamangSymbolDo> wheel = convertWheel(template.getWheel());
        if (wheel.getIndex() == 0) {
            // 每一轮，首回合倍率初始化
            lastWheelMultiple = null;
        }
        RewardResultDo<LamangSymbolDo> rewardResult = lamangRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, lastWheelMultiple, currentTotalRewardAmount));
        Long id = idMap.get(template.getId());
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        Long parentId = idMap.get(template.getParentId());
        AssertUtil.assertNotNull(parentId, ErrorCode.ERR_INTER);
        Long batchId = idMap.get(template.getBatchId());
        AssertUtil.assertNotNull(batchId, ErrorCode.ERR_INTER);
        int jackpotDrawTimes = LamangSymbolUtil.countNewScatterAndScatter1InWheel(wheel.getSymbolArr());
        return LamangGameRecordDo.builder()
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
                .jackpotDrawTimes(jackpotDrawTimes)
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }
}
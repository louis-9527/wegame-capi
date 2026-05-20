package com.wegame.capi.biz.module.diawatamayari.game;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.template.malakas.DiwataBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.IdHelper;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariSymbolUtil;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
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

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO;
import static com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper.getTotalRewardAmount;
import static com.wegame.capi.biz.module.diawatamayari.DiwataMayariSymbolUtil.postProcessPlaceholderSymbol;
import static com.wegame.capi.common.Constants.DIWATA_MAYARI_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("diwataMayariGameCommonTemplatePlay")
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariGameCommonTemplatePlay implements TemplateGame<DiwataMayariGameRecordDo, DiwataBetRecordTemplateEsDo> {

    @Autowired
    IdHelper idHelper;
    @Autowired
    DiwataMayariRewardHandler diwataMayariRewardHandler;

    @Override
    public List<DiwataMayariGameRecordDo> templatePlay(BetDo betDo, GamePlayDo gamePlayDo, DiwataBetRecordTemplateEsDo template) {
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

    private List<DiwataMayariGameRecordDo> convertTemplateList(GamePlayDo gamePlayDo, List<GameRecordTemplateDo> templateList) {
        Map<Long, Long> idMap = idHelper.buildIdMap(templateList);
        List<DiwataMayariGameRecordDo> ret = new ArrayList<>();
        Long maxBatchId = Collections.max(templateList, Comparator.comparing(GameRecordTemplateDo::getBatchId)).getBatchId();
        BigDecimal totalMultiple;
        for (GameRecordTemplateDo templateItem : CollectionUtils.emptyIfNull(templateList)) {
            boolean lastGameFlag = Objects.equals(templateItem.getBatchId(), maxBatchId);
            WheelDo<DiwataMayariSymbolDo> wheel = DiwataMayariSymbolUtil.convertWheel(templateItem.getWheel());
            if (wheel.getIndex() == 0) {
                totalMultiple = BigDecimal.ZERO;
            } else {
                DiwataMayariGameRecordDo leastLast = ret.get(ret.size() - 1);
                totalMultiple = Objects.nonNull(leastLast) ? leastLast.getRewardResult().getTotalMultiple() : BigDecimal.ZERO;
            }
            ret.add(buildGameRecord(gamePlayDo, templateItem, wheel, totalMultiple, idMap, lastGameFlag));
        }
        // 占位元素-将占位元素设置为空:
        postProcessPlaceholderSymbol(ret);
        // 计算总中奖金额（设置到上下文用于playResultValidate校验）
        gamePlayDo.setOverallRewardAmount(getTotalRewardAmount(ret));
        // 返回
        return ret;
    }

    private DiwataMayariGameRecordDo buildGameRecord(GamePlayDo gamePlay, GameRecordTemplateDo template, WheelDo<DiwataMayariSymbolDo> wheel,
                                                     BigDecimal totalMultiple, Map<Long, Long> idMap, boolean lastGameFlag) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(build(DIWATA_MAYARI_GAME_INFO, gamePlay, wheel, totalMultiple));
        Long id = idMap.get(template.getId());
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        Long parentId = idMap.get(template.getParentId());
        AssertUtil.assertNotNull(parentId, ErrorCode.ERR_INTER);
        Long batchId = idMap.get(template.getBatchId());
        AssertUtil.assertNotNull(batchId, ErrorCode.ERR_INTER);
        return DiwataMayariGameRecordDo.builder()
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
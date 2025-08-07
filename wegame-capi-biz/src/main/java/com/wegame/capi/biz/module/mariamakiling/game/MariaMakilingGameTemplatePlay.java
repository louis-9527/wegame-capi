package com.wegame.capi.biz.module.mariamakiling.game;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.biz.service.template.GameRecordTemplateService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
import static com.wegame.capi.client.enums.TemplateTypeEnum.MARIA_MAKILING;
import static com.wegame.capi.common.Constants.CONST_1;
import static com.wegame.capi.common.Constants.CONST_100000;

@Slf4j
@Component("mariaMakilingGameTemplatePlay")
public class MariaMakilingGameTemplatePlay extends AbstractGame<MariaMakilingGameRecordDo> {

    private static final Long GAME_RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = MARIA_MAKILING_GAME_INFO;

    @Autowired
    RewardHandler<SymbolDo> mariaMakilingRewardHandler;
    @Autowired
    GameRecordTemplateService gameRecordTemplateService;
    @Autowired
    BetRecordTemplateService betRecordTemplateService;
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

    @Override
    public List<MariaMakilingGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        MariaMakilingConfig.TemplateMultipleRange templateConfig = getTemplateConfig();
        AssertUtil.assertNotNull(templateConfig, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateConfig.getMultipleRangeStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateConfig.getMultipleRangeEnd(), ErrorCode.ERR_INTER);
        List<Long> betRecordIdList = betRecordTemplateService.listIdByMultipleRange("6", "7", MARIA_MAKILING);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        List<GameRecordTemplateDo> configRecordList = gameRecordTemplateService.listByBetRecordId(betRecordId, MARIA_MAKILING);
        // 获取首轮(parent ==0 and index =0)
        GameRecordTemplateDo firstConfigRecord = configRecordList.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));
        // 生成首轮结果
        long firstGameRecordId = generateGameRecordId();
        WheelDo<SymbolDo> wheel = convertWheel(firstConfigRecord.getWheel());
        RewardResultDo<SymbolDo> firstWheelRewardResult = mariaMakilingRewardHandler.handle(build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO));
        long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
        List<MariaMakilingGameRecordDo> freeWheelGameRecordList = new ArrayList<>();
        List<Long> freeWheelGameRecordIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(firstConfigRecord.getGameRecordIds())) {
            List<Long> gameRecordIds = firstConfigRecord.getGameRecordIds();
            Map<Long, GameRecordTemplateDo> configRecordMap = configRecordList.stream()
                    .collect(Collectors.toMap(GameRecordTemplateDo::getId, p -> p));
            for (Long recordId : gameRecordIds) {
                GameRecordTemplateDo configRecord = configRecordMap.get(recordId);
                WheelDo<SymbolDo> wheelRecord = convertWheel(configRecord.getWheel());
                RewardResultDo<SymbolDo> rewardResultDo = mariaMakilingRewardHandler.handle(build(GAME_INFO, gamePlay, wheelRecord, BigDecimal.ZERO));
                overallRewardAmount = overallRewardAmount + rewardResultDo.getTotalRewardAmount();
                MariaMakilingGameRecordDo gameRecord = buildGameRecord(
                        userInfo,
                        generateGameRecordId(),
                        gamePlay.getBetRecordId(),
                        wheelRecord,
                        rewardResultDo,
                        null,
                        overallRewardAmount,
                        false,
                        false);
                freeWheelGameRecordList.add(gameRecord);
                freeWheelGameRecordIds.add(gameRecord.getId());
            }
        }
        // 构建首轮
        MariaMakilingGameRecordDo firstGameRecord = this.buildGameRecord(userInfo,
                firstGameRecordId,
                gamePlay.getBetRecordId(),
                wheel,
                firstWheelRewardResult,
                freeWheelGameRecordIds,
                firstWheelRewardResult.getTotalRewardAmount(),
                false,
                firstWheelRewardResult.isHasRewardFreeWheel()
        );
        List<MariaMakilingGameRecordDo> ret = new ArrayList<>();
        ret.add(firstGameRecord);
        if (CollectionUtils.isNotEmpty(freeWheelGameRecordList)) {
            ret.addAll(freeWheelGameRecordList);
        }
        return ret;
    }

    private MariaMakilingGameRecordDo buildGameRecord(UserInfo userInfo,
                                                      Long gameRecordId,
                                                      Long betRecordId,
                                                      WheelDo<SymbolDo> wheel,
                                                      RewardResultDo<SymbolDo> rewardResult,
                                                      List<Long> freeWheelGameRecordIds,
                                                      Long TotalRewardAmount,
                                                      Boolean accumulateReachMaxMultiple,
                                                      Boolean lastGameFlag) {
        return MariaMakilingGameRecordDo.builder()
                .id(gameRecordId)
                .parentId(GAME_RECORD_DEFAULT_PARENT_ID)
                .betRecordId(betRecordId)
                .batchId(0L)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(freeWheelGameRecordIds)
                .overallRewardAmount(TotalRewardAmount)
                .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                .lastGameFlag(lastGameFlag)
                .build();
    }

    private WheelDo<SymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<SymbolDo>>() {
        });
    }

    private MariaMakilingConfig.TemplateMultipleRange getTemplateConfig() {
        int randomNumber = randomService.randomInt(CONST_1, CONST_100000);
        Map<String, MariaMakilingConfig.TemplateMultipleRange> templateRangeConfigMap = mariaMakilingConfig.getTemplateRangeConfigMap();
        int startVal = 0;
        for (Map.Entry<String, MariaMakilingConfig.TemplateMultipleRange> entry : CollectionUtils.emptyIfNull(templateRangeConfigMap.entrySet())) {
            int i = NumberUtil.mul(new BigDecimal(entry.getKey()), new BigDecimal(CONST_100000)).intValue();
            if (randomNumber > startVal && randomNumber <= startVal + i) {
                return entry.getValue();
            } else {
                startVal += i;
            }
        }
        return null;
    }

    private static Long getRandomElement(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}

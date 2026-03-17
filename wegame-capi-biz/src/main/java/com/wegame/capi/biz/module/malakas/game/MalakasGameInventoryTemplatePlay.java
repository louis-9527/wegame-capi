package com.wegame.capi.biz.module.malakas.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.common.CommonInventoryControlConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractInventoryTemplatePlay;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.biz.service.inventory.InventoryService;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.*;
import static com.wegame.capi.common.utils.ListUtil.getRandomElement;

@Slf4j
@Component("malakasGameInventoryTemplatePlay")
@ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
public class MalakasGameInventoryTemplatePlay extends AbstractInventoryTemplatePlay<MalakasGameRecordDo> {

    private static final int MAX_RETRY_TIMES = 2;
    private static final String NO_REWARD_TEMPLATE_MULTIPLE_RANGE = "0_0";
    @Autowired
    RandomService randomService;
    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    MalakasGameCommonTemplatePlay malakasGameCommonTemplatePlay;
    @Autowired
    BetRecordTemplateEsService<MalakasBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<MalakasGameRecordDo> play(BetDo betDo, GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 设置-投注上下文（库存控制）
        CommonInventoryControlConfig inventoryControlConfig = malakasConfig.getInventoryControlConfig();
        // 匹配库存控制级别
        Long betAmount = gamePlay.getBetAmount();
        Map.Entry<InventoryTypeEnum, CommonInventoryControlConfig.InventoryConfigItem> matchInventory = inventoryControlConfig.getInventoryConfig().match(betAmount);
        AssertUtil.assertNotNull(matchInventory, ErrorCode.ERR_INTER, "bet amount error");
        betDo.getBetInventoryControl().setInventoryType(matchInventory.getKey());
        long inventoryContributeAmount = matchInventory.getValue().getInventoryInputRate().multiply(new BigDecimal(betAmount)).longValue();
        betDo.getBetInventoryControl().setInventoryContributionAmount(inventoryContributeAmount);
        log.info("match inventory info: betAmount: {}, inventoryType: {}, inventoryContributeAmount: {}", betAmount, matchInventory.getKey().getCode(), inventoryContributeAmount);
        Map<InventoryTypeEnum, Long> inventorySnapshot = inventoryService.getInventory(betDo.getGameType(), userInfo.getPId(), userInfo.getChannelId());
        Long inventoryValue = Objects.isNull(inventorySnapshot.get(matchInventory.getKey())) ?
                inventorySnapshot.get(matchInventory.getKey()) : 0L;
        CommonInventoryControlConfig.InventoryControlConfigItem inventoryControlConfigItem = matchInventory.getValue().matchControlConfig(inventoryValue, matchInventory.getValue());
        AssertUtil.assertNotNull(inventoryControlConfigItem, ErrorCode.ERR_INTER);
        MalakasBetRecordTemplateEsDo templateEsDo;
        TemplateMultipleRange templateMultipleRange;
       boolean orNotRewardBlank = orNotRewardBlank(gamePlay, inventoryControlConfig);
       if (orNotRewardBlank) {
           // 命中轮空：则不中奖
           templateMultipleRange = TemplateMultipleRange.build(NO_REWARD_TEMPLATE_MULTIPLE_RANGE);
           templateEsDo = getTemplate(gamePlay, templateMultipleRange);
       } else {
           // 未命中轮空：中奖
           //// 循环处理
           do {
               betDo.getBetInventoryControl().addRetryTimes();
               boolean winRet = judgeWinRet(inventoryControlConfigItem.getWinProbability());
               String rewardConfigId = inventoryControlConfigItem.getRewardConfigId(gamePlay.getBetType(), winRet);
               TreeMap<String, Integer> treeMap = inventoryControlConfig.getRewardConfig().getRewardConfigMap().get(rewardConfigId);
               templateMultipleRange = getTemplateMultipleRange(treeMap);
               templateEsDo = getTemplate(gamePlay, templateMultipleRange);
           } while (!deductInventory(betDo, userInfo, templateEsDo));
           ////
           if (betDo.getBetInventoryControl().getRetryTimes() > MAX_RETRY_TIMES) {
               // 扣减次数大于最大重试次数，走兜底逻辑
               String floorTemplateMultipleRangeStr = inventoryControlConfig.getRewardConfig().getRewardFloorOdds().get(gamePlay.getBetType());
               AssertUtil.assertStrNotEmpty(floorTemplateMultipleRangeStr, ErrorCode.ERR_INTER);
               templateMultipleRange = TemplateMultipleRange.build(floorTemplateMultipleRangeStr);
               templateEsDo = getTemplate(gamePlay, templateMultipleRange);
               log.info("reach max retryTimes, floor inventory flow, floorTemplateMultipleRangeStr: {}", floorTemplateMultipleRangeStr);
           }
       }
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateEsDo, ErrorCode.ERR_INTER);
        return malakasGameCommonTemplatePlay.commonTemplatePlay(betDo, gamePlay, templateMultipleRange, templateEsDo);
    }

    private boolean orNotRewardBlank(GamePlayDo gamePlay, CommonInventoryControlConfig inventoryControlConfig) {
        if (Objects.equals(BetTypeEnum.BUY_BET, gamePlay.getBetType())) {
            return false;
        }
        // 正常投注轮空率
        BigDecimal normalBetRewardBlankProbability = inventoryControlConfig.getRewardConfig().getRewardBlankProbability().get(gamePlay.getBetType());
        return judgeHitBlank(normalBetRewardBlankProbability);
    }

    private boolean deductInventory(BetDo betDo, UserInfo userInfo, MalakasBetRecordTemplateEsDo templateEsDo) {
        if (betDo.getBetInventoryControl().getRetryTimes() > MAX_RETRY_TIMES) {
            return true;
        } else {
            long deductInventoryVal = new BigDecimal(templateEsDo.getMultiple()).multiply(new BigDecimal(betDo.getBetAmount())).longValue();
            return inventoryService.deductInventory(betDo.getGameType(),
                    userInfo.getPId(),
                    userInfo.getChannelId(),
                    betDo.getBetInventoryControl().getInventoryType(),
                    deductInventoryVal);
        }
    }


    private boolean judgeWinRet(BigDecimal winProbability) {
        if (BigDecimal.ZERO.equals(winProbability)) {
            return false;
        } else if (BigDecimal.ONE.equals(winProbability)) {
            return true;
        } else {
            int randomVal = randomService.randomInt(CONST_1, CONST_10000);
            return winProbability.multiply(new BigDecimal(CONST_10000)).longValue() <= randomVal;
        }
    }

    private boolean judgeHitBlank(BigDecimal blankProbability) {
        int randomVal = randomService.randomInt(CONST_1, CONST_10000);
        return blankProbability.multiply(new BigDecimal(CONST_10000)).longValue() <= randomVal;
    }

    // todo 抽到 CommonTemplatePlay
    private TemplateMultipleRange getTemplateMultipleRange(TreeMap<String, Integer> treeMap) {
        String templateMultipleRange = randomService.randomKeyByWeight(treeMap);
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }

    private MalakasBetRecordTemplateEsDo getTemplate(GamePlayDo gamePlay, TemplateMultipleRange templateMultipleRange) {
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(TemplateTypeEnum.MALAKAS, gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), MalakasBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("template betRecordId:{}", betRecordId);
        MalakasBetRecordTemplateEsDo template;
        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.MALAKAS, gamePlay.getBetType(), templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存
            template = betRecordTemplateEsService.getTemplateWithLocalCache(TemplateTypeEnum.MALAKAS, betRecordId, MalakasBetRecordTemplateEsDo.class);
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(TemplateTypeEnum.MALAKAS, betRecordId, MalakasBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
        //        List<GameRecordTemplateDo> configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        //        });
        //        if (CollectionUtils.isEmpty(configRecordList)) {
        //            log.error("template is empty, betType:{}, range start: {}, end: {}", gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        //        }
        //        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
    }

}
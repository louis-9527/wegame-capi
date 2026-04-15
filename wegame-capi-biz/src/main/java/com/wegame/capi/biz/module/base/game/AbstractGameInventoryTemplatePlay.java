package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.common.CommonInventoryControlConfig;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetInventoryControlDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.biz.service.inventory.InventoryService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.CONST_1;
import static com.wegame.capi.common.Constants.CONST_10000;

/**
 * 游戏模版Play(库存控制)
 */
@Slf4j
public abstract class AbstractGameInventoryTemplatePlay<R extends ToVoConverter, T extends BetRecordTemplateEsDo> extends AbstractGameTemplatePlay<R, T> {

    public static final int MAX_RETRY_TIMES = 2;
    public static final int DEFAULT_RETRY_TIMES = -1;
    public static final String NO_REWARD_TEMPLATE_MULTIPLE_RANGE = "0_0";
    @Autowired
    RandomService randomService;
    @Autowired
    InventoryService inventoryService;

    /**
     * 通用库存控制配置
     */
    public abstract CommonInventoryControlConfig inventoryControlConfig();

    public abstract T getTemplateByRange(BetDo betDo, GamePlayDo gamePlayDo, TemplateMultipleRange templateMultipleRange);

    public abstract long getTotalRewardAmount(BetDo betDo, GamePlayDo gamePlayDo, List<R> gameRecordList);

    @Override
    public List<R> play(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 匹配-库存控制级别
        CommonInventoryControlConfig inventoryControlConfig = inventoryControlConfig();
        Long betAmount = gamePlayDo.getBetAmount();
        Map.Entry<InventoryTypeEnum, CommonInventoryControlConfig.InventoryConfigItem> matchInventory = inventoryControlConfig.getInventoryConfig().match(betAmount);
        AssertUtil.assertNotNull(matchInventory, ErrorCode.ERR_INTER, "betgit  amount error");
        long inventoryContributeAmount = matchInventory.getValue().getInventoryInputRate().multiply(new BigDecimal(gamePlayDo.getPayAmount())).longValue();
        String inventoryPoolName = getInventoryPoolName(betDo, userInfo, inventoryControlConfig);
        // 设置-投注上下文（库存控制）
        betDo.setBetInventoryControl(BetInventoryControlDo.builder()
                .inventoryType(matchInventory.getKey())
                .inventoryPoolName(inventoryPoolName)
                .inventoryContributionAmount(inventoryContributeAmount)
                .build());
        log.info("matchInventoryInfo: betType: {}, betAmount: {}, inventoryType: {}, poolName: {}, contributeAmount: {}",
                gamePlayDo.getBetType().getCode(),
                betAmount, matchInventory.getKey().getCode(),
                inventoryPoolName,
                inventoryContributeAmount);
        if (isHitRewardBlank(gamePlayDo, inventoryControlConfig)) {
            return handleOnHitRewardBlank(betDo, gamePlayDo);
        } else {
            return handleOnHitReward(userInfo, betDo, gamePlayDo, matchInventory);
        }
    }

    public boolean isHitRewardBlank(GamePlayDo gamePlay, CommonInventoryControlConfig inventoryControlConfig) {
        if (Objects.equals(BetTypeEnum.BUY_BET, gamePlay.getBetType())) {
            return false;
        }
        // 正常投注轮空率
        BigDecimal normalBetRewardBlankProbability = inventoryControlConfig.getRewardConfig().getRewardBlankProbability().get(gamePlay.getBetType());
        AssertUtil.assertNotNull(normalBetRewardBlankProbability, ErrorCode.ERR_INTER);
        int randomVal = randomService.randomInt(CONST_1, CONST_10000);
        return randomVal <= normalBetRewardBlankProbability.multiply(new BigDecimal(CONST_10000)).longValue();
    }

    public boolean judgeWinRet(BigDecimal winProbability) {
        if (BigDecimal.ZERO.compareTo(winProbability) == 0) {
            return false;
        } else if (BigDecimal.ONE.compareTo(winProbability) == 0) {
            return true;
        } else {
            int randomVal = randomService.randomInt(CONST_1, CONST_10000);
            return randomVal <= winProbability.multiply(new BigDecimal(CONST_10000)).longValue();
        }
    }

    private String getInventoryPoolName(BetDo betDo, UserInfo userInfo, CommonInventoryControlConfig inventoryControlConfig) {
        return inventoryControlConfig.getInventoryConfig().getInventoryPoolName(betDo.getBetType(), userInfo.getPId(), userInfo.getChannelId());
    }

    private List<R> handleOnHitRewardBlank(BetDo betDo, GamePlayDo gamePlayDo) {
        TemplateMultipleRange templateMultipleRange = TemplateMultipleRange.build(NO_REWARD_TEMPLATE_MULTIPLE_RANGE);
        gamePlayDo.setTemplateMultipleRange(templateMultipleRange);
        T templateEsDo = getTemplateByRange(betDo, gamePlayDo, templateMultipleRange);
        return templateGame().templatePlay(betDo, gamePlayDo, templateEsDo);
    }

    private List<R> handleOnHitReward(UserInfo userInfo,
                                      BetDo betDo,
                                      GamePlayDo gamePlayDo,
                                      Map.Entry<InventoryTypeEnum, CommonInventoryControlConfig.InventoryConfigItem> matchInventory) {
        // 匹配-库存
        Object currentInventory = inventoryService.getInventory(betDo.getGameType(),
                betDo.getBetInventoryControl().getInventoryPoolName(),
                matchInventory.getKey());
        long currentInventoryValue = Objects.nonNull(currentInventory) ? Long.valueOf(currentInventory.toString()) : 0L;
        CommonInventoryControlConfig.InventoryControlConfigItem inventoryControlConfigItem = matchInventory.getValue().matchControlConfig(betDo.getBetType(), currentInventoryValue);
        log.info("matchInventoryLevelInfo, currentInventory: {}, inventoryControlConfigItem: {}", currentInventory, inventoryControlConfigItem);
        AssertUtil.assertNotNull(inventoryControlConfigItem, ErrorCode.ERR_INTER);
        List<R> gameRecordList;
        boolean deductInventoryRet;
        int retryTimes = DEFAULT_RETRY_TIMES;
        //// 循环处理: 循环退出条件（扣减成功 || 达到最大重试次数）
        do {
            retryTimes ++;
            boolean winRet = judgeWinRet(inventoryControlConfigItem.getWinProbability());
            String rewardConfigId = inventoryControlConfigItem.getRewardConfigId(winRet);
            log.info("doWhile, rewardConfigInfo, betRecordId: {}, retryTimes: {}, rewardConfigId: {}, betType: {}", gamePlayDo.getBetRecordId(), retryTimes, rewardConfigId, gamePlayDo.getBetType().getCode());
            TreeMap<String, Integer> treeMap = inventoryControlConfig().getRewardConfig().getRewardConfigMap().get(gamePlayDo.getBetType()).get(rewardConfigId);
            TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(treeMap);
            gamePlayDo.setTemplateMultipleRange(templateMultipleRange);
            T templateEsDo = getTemplateByRange(betDo, gamePlayDo, templateMultipleRange);
            gameRecordList = templateGame().templatePlay(betDo, gamePlayDo, templateEsDo);
            deductInventoryRet = deductInventory(betDo, userInfo, getTotalRewardAmount(betDo, gamePlayDo, gameRecordList));
            if (deductInventoryRet) {
                // 扣减成功，退出循环
                break;
            }
        } while (retryTimes < MAX_RETRY_TIMES);
        if (deductInventoryRet) {
            // 扣减成功，直接返回结果
            return gameRecordList;
        } else {
            // 扣减失败，走兜底中奖逻辑
            return handleOnFloorReward(betDo, gamePlayDo, inventoryControlConfig());
        }
    }

    private List<R> handleOnFloorReward(BetDo betDo, GamePlayDo gamePlayDo, CommonInventoryControlConfig inventoryControlConfig) {
        String floorTemplateMultipleRangeStr = inventoryControlConfig.getRewardConfig().getRewardFloorMultiple().get(gamePlayDo.getBetType());
        AssertUtil.assertStrNotEmpty(floorTemplateMultipleRangeStr, ErrorCode.ERR_INTER);
        TemplateMultipleRange templateMultipleRange = TemplateMultipleRange.build(floorTemplateMultipleRangeStr);
        gamePlayDo.setTemplateMultipleRange(templateMultipleRange);
        T templateEsDo = getTemplateByRange(betDo, gamePlayDo, templateMultipleRange);
        log.info("betType: {}, reach max retryTimes, floor inventory flow, floorTemplateMultipleRangeStr: {}",
                gamePlayDo.getBetType().getCode(), floorTemplateMultipleRangeStr);
        return templateGame().templatePlay(betDo, gamePlayDo, templateEsDo);
    }

    private boolean deductInventory(BetDo betDo, UserInfo userInfo, Long totalRewardAmount) {
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(totalRewardAmount, ErrorCode.ERR_INTER);
        return inventoryService.deductInventory(betDo.getGameType(),
                betDo.getBetInventoryControl().getInventoryPoolName(),
                betDo.getBetInventoryControl().getInventoryType(),
                totalRewardAmount);
    }
}

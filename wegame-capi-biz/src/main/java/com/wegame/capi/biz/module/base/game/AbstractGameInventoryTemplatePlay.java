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

import static com.wegame.capi.biz.domain.biz.bet.BetInventoryControlDo.DEFAULT_RETRY_TIMES;
import static com.wegame.capi.common.Constants.CONST_1;
import static com.wegame.capi.common.Constants.CONST_10000;

/**
 * 游戏模版Play(库存控制)
 */
@Slf4j
public abstract class AbstractGameInventoryTemplatePlay<R extends ToVoConverter, T extends BetRecordTemplateEsDo> extends AbstractGameTemplatePlay<R, T> {

    public static final int MAX_RETRY_TIMES = 2;
    public static final String NO_REWARD_TEMPLATE_MULTIPLE_RANGE = "0_0";

    @Autowired
    RandomService randomService;
    @Autowired
    InventoryService inventoryService;

    /**
     * 统一库存控制配置
     */
    public abstract CommonInventoryControlConfig inventoryControlConfig();

    public abstract T getTemplateByRange(BetDo betDo, GamePlayDo gamePlayDo, TemplateMultipleRange templateMultipleRange);

    public abstract long getTotalRewardAmount(List<R> gameRecordList);

    @Override
    public T getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 匹配库存控制级别
        CommonInventoryControlConfig inventoryControlConfig = inventoryControlConfig();
        Long betAmount = gamePlayDo.getBetAmount();
        Map.Entry<InventoryTypeEnum, CommonInventoryControlConfig.InventoryConfigItem> matchInventory = inventoryControlConfig.getInventoryConfig().match(betAmount);
        AssertUtil.assertNotNull(matchInventory, ErrorCode.ERR_INTER, "bet amount error");
        long inventoryContributeAmount = matchInventory.getValue().getInventoryInputRate().multiply(new BigDecimal(betAmount)).longValue();
        // 设置-投注上下文（库存控制）
        betDo.setBetInventoryControl(BetInventoryControlDo.builder()
                .retryTimes(DEFAULT_RETRY_TIMES)
                .inventoryType(matchInventory.getKey())
                .inventoryContributionAmount(inventoryContributeAmount)
                .build());
        log.info("match inventory info: betAmount: {}, inventoryType: {}, inventoryContributeAmount: {}", betAmount, matchInventory.getKey().getCode(), inventoryContributeAmount);
        Map<InventoryTypeEnum, Long> inventorySnapshot = inventoryService.getInventory(betDo.getGameType(), getPoolName(userInfo));
        long currentInventoryValue = Objects.nonNull(inventorySnapshot.get(matchInventory.getKey())) ?
                inventorySnapshot.get(matchInventory.getKey()) : 0L;
        CommonInventoryControlConfig.InventoryControlConfigItem inventoryControlConfigItem = matchInventory.getValue().matchControlConfig(currentInventoryValue);
        AssertUtil.assertNotNull(inventoryControlConfigItem, ErrorCode.ERR_INTER);
        T templateEsDo;
        TemplateMultipleRange templateMultipleRange;
        boolean isHitRewardBlank = isHitRewardBlank(gamePlayDo, inventoryControlConfig);
        if (isHitRewardBlank) {
            // 命中轮空：则不中奖
            templateMultipleRange = TemplateMultipleRange.build(NO_REWARD_TEMPLATE_MULTIPLE_RANGE);
            templateEsDo = getTemplateByRange(betDo, gamePlayDo, templateMultipleRange);
        } else {
            // 未命中轮空：中奖
            //// 循环处理
            long totalRewardAmount;
            do {
                betDo.getBetInventoryControl().addRetryTimes();
                boolean winRet = judgeWinRet(inventoryControlConfigItem.getWinProbability());
                String rewardConfigId = inventoryControlConfigItem.getRewardConfigId(gamePlayDo.getBetType(), winRet);
                TreeMap<String, Integer> treeMap = inventoryControlConfig.getRewardConfig().getRewardConfigMap().get(rewardConfigId);
                templateMultipleRange = getTemplateMultipleRange(treeMap);
                templateEsDo = getTemplateByRange(betDo, gamePlayDo, templateMultipleRange);
                List<R> gameRecordList = templateGame().templatePlay(betDo, gamePlayDo, templateEsDo);
                totalRewardAmount = getTotalRewardAmount(gameRecordList);
            } while (!deductInventory(betDo, userInfo, templateEsDo, totalRewardAmount));
            ////
            if (betDo.getBetInventoryControl().getRetryTimes() > MAX_RETRY_TIMES) {
                // 扣减次数大于最大重试次数，走兜底逻辑
                String floorTemplateMultipleRangeStr = inventoryControlConfig.getRewardConfig().getRewardFloorMultiple().get(gamePlayDo.getBetType());
                AssertUtil.assertStrNotEmpty(floorTemplateMultipleRangeStr, ErrorCode.ERR_INTER);
                templateMultipleRange = TemplateMultipleRange.build(floorTemplateMultipleRangeStr);
                templateEsDo = getTemplateByRange(betDo, gamePlayDo, templateMultipleRange);
                log.info("reach max retryTimes, floor inventory flow, floorTemplateMultipleRangeStr: {}", floorTemplateMultipleRangeStr);
            }
        }
        // 设置倍率范围到play上下文， 用于后续统一数据验证
        gamePlayDo.setTemplateMultipleRange(templateMultipleRange);
        return templateEsDo;
    }

    /// 默认实现 /////////////
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
        if (BigDecimal.ZERO.equals(winProbability)) {
            return false;
        } else if (BigDecimal.ONE.equals(winProbability)) {
            return true;
        } else {
            int randomVal = randomService.randomInt(CONST_1, CONST_10000);
            return winProbability.multiply(new BigDecimal(CONST_10000)).longValue() <= randomVal;
        }
    }

    public boolean deductInventory(BetDo betDo, UserInfo userInfo, T templateEsDo, Long totalRewardAmount) {
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(totalRewardAmount, ErrorCode.ERR_INTER);
        if (betDo.getBetInventoryControl().getRetryTimes() > MAX_RETRY_TIMES) {
            return true;
        } else {
            // long deductInventoryVal = new BigDecimal(templateEsDo.getMultiple()).multiply(new BigDecimal(betDo.getBetAmount())).longValue();
            return inventoryService.deductInventory(betDo.getGameType(),
                    getPoolName(userInfo),
                    betDo.getBetInventoryControl().getInventoryType(),
                    totalRewardAmount);
        }
    }

    private String getPoolName(UserInfo userInfo) {
        return inventoryControlConfig().getInventoryConfig().getInventoryPoolName(userInfo.getPId(), userInfo.getChannelId());
    }
}

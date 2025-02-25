package com.wegame.capi.biz.module.ibongadarna.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component("ibongAdarnaRewardHandler")
public class IbongAdarnaRewardHandler extends AbstractRewardHandler<IbongSymbolDo> {
    @Autowired
    OddsCalculateStrategy ibongAdarnaOddsCalculateStrategy;
    @Autowired
    MultipleCalculateStrategy ibongAdarnaMultipleCalculateStrategy;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;

    public RewardResultDo<IbongSymbolDo> handle(RewardHandleDo<IbongSymbolDo> rewardHandle) {
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<IbongSymbolDo>> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        // 3、计算倍率->随机生成倍率
        BigDecimal totalMultiple = ibongAdarnaMultipleCalculateStrategy.calculateMultiple(rewardHandle, rewardLineList);
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.<IbongSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(totalMultiple)
                    .hasRewardFreeWheel(false)
                    .rewardLineDetailList(null)
                    .build();
        }

        // 4、中奖线处理
        // 中奖总金额
        long totalRewardAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDetailDo<IbongSymbolDo>> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo<IbongSymbolDo> rewardLine : CollectionUtils.emptyIfNull(rewardLineList)) {
            // 计算赔率
            BigDecimal odds = ibongAdarnaOddsCalculateStrategy.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率 * 倍率
            BigDecimal lineRewardAmount = this.calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, totalMultiple);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.<IbongSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardAmount(lineRewardAmount.longValue())
                    .odds(odds)
                    .multiple(totalMultiple)
                    .rewardLineType(rewardLine.getRewardLineType())
                    .rewardLineSubType(rewardLine.getRewardLineSubType())
                    .rewardLineCount(rewardLine.getRewardLineCount())
                    .rewardSymbol(rewardLine.getRewardSymbol())
                    .rewardSymbolList(rewardLine.getRewardSymbolList())
                    .build());
        }
        // 5.构建中奖结果
        return RewardResultDo.<IbongSymbolDo>builder()
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .betAmount(rewardHandle.getBetAmount())
                .totalRewardAmount(totalRewardAmount)
                .hasRewardFreeWheel(false)
                .rewardLineDetailList(rewardLineDetailList)
                .totalMultiple(totalMultiple)
                .build();
    }

    /**
     * 计算中奖线中奖金额
     * 中奖线中奖金额计算公式 = 投注金额 * 赔率 *中奖线数量 * 倍率
     *
     * @param rewardLine 中奖线
     * @param betAmount  投注金额
     * @param odds       赔率
     * @param multiple   倍率
     * @return 中奖金额
     */
    @Override
    public BigDecimal calculateLineRewardAmount(RewardLineDo rewardLine, Long betAmount, BigDecimal odds, BigDecimal multiple) {
        AssertUtil.assertTrue(rewardLine.isCommonReward(), ErrorCode.ERR_PARAM, "rewardLineType is illegal");
        return BigDecimal.valueOf(betAmount)
                .multiply(odds)
                .multiply(new BigDecimal("" + rewardLine.getRewardLineCount()))
                .multiply(multiple);
    }

    private List<RewardLineDo<IbongSymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<IbongSymbolDo> wheel) {
        IbongSymbolDo[][] symbolArr = wheel.getSymbolArr();
        List<RewardLineDo<IbongSymbolDo>> rewardLineDoList = Lists.newArrayList();
        // 获取固定线路
        Map<Integer, List<List<Integer>>> rewardFixedLinesConfigMap = ibongAdarnaConfig.getRewardFixedLinesConfig();
        rewardFixedLinesConfigMap.forEach((lineNo, lineIndexList) -> {
            Set<String> rewardSymbolDoSet = Sets.newHashSet();
            List<RewardSymbolDo<IbongSymbolDo>> rewardSymbolDoList = Lists.newArrayList();
            for (List<Integer> lineIndex : lineIndexList) {
                IbongSymbolDo symbolDo = symbolArr[lineIndex.get(0)][lineIndex.get(1)];
                rewardSymbolDoSet.add(symbolDo.getId());
                rewardSymbolDoList.add(RewardSymbolDo.<IbongSymbolDo>builder()
                        .symbol(symbolDo)
                        .index(IndexFactory.buildDo(lineIndex.get(0), lineIndex.get(1)))
                        .build());
            }
            // 当前线是否中奖
            if (hasReward(rewardSymbolDoSet, gameInfo)) {
                // 构建中奖线
                rewardLineDoList.add(buildRewardLineDo(rewardSymbolDoList, lineNo));
            }
        });
        // 返回中奖线列表
        return rewardLineDoList;
    }

    /**
     * 构建中间线
     *
     * @param rewardSymbolDoList 中奖元素列表
     * @param lineNo             线号
     * @return line
     */
    private RewardLineDo<IbongSymbolDo> buildRewardLineDo(List<RewardSymbolDo<IbongSymbolDo>> rewardSymbolDoList, Integer lineNo) {
        int totalRewardLineCount = 1;
        if (CollectionUtil.isEmpty(rewardSymbolDoList)) {
            totalRewardLineCount = 0;
        } else {
            for (RewardSymbolDo<IbongSymbolDo> symbol : CollectionUtil.emptyIfNull(rewardSymbolDoList)) {
                totalRewardLineCount = totalRewardLineCount * symbol.getSymbol().getBreakUp().getTimes();
            }
        }
        // 中奖元素选择
        RewardSymbolDo<IbongSymbolDo> rewardSymbolDo = rewardSymbolDoList.stream()
                .filter(symbol -> !symbol.getSymbol().isWildSymbol()).findFirst().orElse(rewardSymbolDoList.get(0));
        return RewardLineDo.<IbongSymbolDo>builder()
                .rewardLineType(RewardLineTypeEnum.THREE)
                .rewardLineSubType(RewardLineSubTypeEnum.COMMON)
                .rewardLineNo(lineNo)
                .rewardLineCount(totalRewardLineCount)
                .rewardSymbol(rewardSymbolDo.getSymbol())
                .rewardSymbolList(rewardSymbolDoList)
                .build();
    }

    /**
     * 判断是中奖线
     *
     * @param rewardSymbolDoSet
     * @return
     */
    private boolean hasReward(Set<String> rewardSymbolDoSet, GameInfoDo gameInfo) {
        if (CollectionUtil.isEmpty(rewardSymbolDoSet)) {
            return false;
        } else if (rewardSymbolDoSet.size() == 1) {
            return true;
        } else if (rewardSymbolDoSet.size() == 2) {
            return rewardSymbolDoSet.contains(SymbolEnum.Wild.getId());
        } else {
            return false;
        }
    }

    private void validate(RewardHandleDo<IbongSymbolDo> rewardHandle) {
        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getIndex(), ErrorCode.ERR_PARAM, "wheel index is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.IBONG_ADARNA), ErrorCode.ERR_PARAM, "just support game type of maria makiling");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
        for (int row = 0; row < gameInfo.getRow(); row++) {
            // 校验：滚轮列数
            AssertUtil.assertTrue(symbolArr[row].length == gameInfo.getColumn(), ErrorCode.ERR_PARAM, "symbol colum arr err");
            // 滚轮：所有元素不能为空
            for (SymbolDo symbolDo : symbolArr[row]) {
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_PARAM, "all symbol can not be null");
            }
        }
    }
}

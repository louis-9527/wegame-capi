package com.wegame.capi.biz.module.ibongadarna.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.ImmutableMap;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("ibongAdarnaRewardHandler")
public class IbongAdarnaRewardHandler extends AbstractRewardHandler<IbongSymbolDo> {

    // ONE_COLUM_INDEX,THREE_COLUM_INDEX,FIVE_COLUM_INDEX顺序不可更改
    private static final List<Integer> SCATTER_COLUM_INDEX_LIST = Arrays.asList(
            ONE_COLUM_INDEX,
            THREE_COLUM_INDEX,
            FIVE_COLUM_INDEX);

    private static final Map<String, SymbolEnum> WILD_SYMBOL_MAP = new ImmutableMap.Builder<String, SymbolEnum>()
            .put(SymbolEnum.Wild.getId(), SymbolEnum.Wild)
            .put(SymbolEnum.WildX2.getId(), SymbolEnum.WildX2)
            .put(SymbolEnum.WildX3.getId(), SymbolEnum.WildX3)
            .put(SymbolEnum.WildX5.getId(), SymbolEnum.WildX5)
            .put(SymbolEnum.WildX10.getId(), SymbolEnum.WildX10)
            .build();
    @Autowired
    OddsCalculateStrategy mariaMakilingOddsCalculateStrategy;
    @Autowired
    MultipleCalculateStrategy mariaMakilingMultipleCalculateStrategy;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;

    public RewardResultDo<IbongSymbolDo> handle(RewardHandleDo<IbongSymbolDo> rewardHandle) {
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<IbongSymbolDo>> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        // 3、计算倍率
        BigDecimal totalMultiple = mariaMakilingMultipleCalculateStrategy.calculateMultiple(rewardHandle, rewardLineList);
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
        List<RewardLineDetailDo<SymbolDo>> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo<SymbolDo> rewardLine : CollectionUtils.emptyIfNull(rewardLineList)) {
            // 计算赔率
            BigDecimal odds = mariaMakilingOddsCalculateStrategy.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
            BigDecimal lineRewardAmount = calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, totalMultiple);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.<SymbolDo>builder()
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
        RewardResultDo<SymbolDo> rewardResult = RewardResultDo.<SymbolDo>builder()
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .betAmount(rewardHandle.getBetAmount())
                .totalRewardAmount(totalRewardAmount)
                .hasRewardFreeWheel(hasRewardScatter(rewardLineList))
                .rewardLineDetailList(rewardLineDetailList)
                .totalMultiple(totalMultiple)
                .build();
        // 6.返回中奖信息
        return rewardResult;
    }

    private List<RewardLineDo<IbongSymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<IbongSymbolDo> wheel) {
        IbongSymbolDo[][] symbolArr = wheel.getSymbolArr();
        List<RewardLineDo<IbongSymbolDo>> rewardLineDoList = Lists.newArrayList();
        // 获取固定线路
        Map<Integer, List<List<Integer>>> rewardFixedLinesConfigMap = ibongAdarnaConfig.getRewardFixedLinesConfig();

        rewardFixedLinesConfigMap.forEach((lineNo, lineIndexList) -> {
            Set<IbongSymbolDo> rewardSymbolDoSet = Sets.newHashSet();
            List<IbongSymbolDo> rewardSymbolDoList = Lists.newArrayList();
            IbongSymbolDo preSymbolDo = null;
            for (List<Integer> lineIndex : lineIndexList) {
                IbongSymbolDo symbolDo = symbolArr[lineIndex.get(0)][lineIndex.get(1)];
                rewardSymbolDoList.add(symbolDo);
                rewardSymbolDoList.add(symbolDo);
            }

            // 当前线是否中奖


        });


        // 返回中奖线列表
        return null;
    }

    /**
     * 判断是中奖线
     *
     * @param rewardSymbolDoSet
     * @return
     */
    private boolean hasReward(Set<IbongSymbolDo> rewardSymbolDoSet) {
        return false;
    }


    private void validate(RewardHandleDo<IbongSymbolDo> rewardHandle) {
        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getIndex(), ErrorCode.ERR_PARAM, "wheel index is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.MARIA_MAKILING), ErrorCode.ERR_PARAM, "just support game type of maria makiling");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
        for (int row = 0; row < gameInfo.getRow(); row++) {
            // 校验：滚轮列数
            AssertUtil.assertTrue(symbolArr[row].length == gameInfo.getColumn(), ErrorCode.ERR_PARAM, "symbol arr err");
            for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
                // 校验：所有元素不能为空
                AssertUtil.assertNotNull(symbolArr[row][colum], ErrorCode.ERR_PARAM, "all symbol can not be null");
                // 校验：第一列不能包含wild
                if (Objects.equals(colum, ONE_COLUM_INDEX)) {
                    AssertUtil.assertTrue(!WILD_SYMBOL_MAP.containsKey(symbolArr[row][colum].getId()), ErrorCode.ERR_PARAM, "this first colum can not has wild");
                }
                // 校验：第二列 & 第4列不能包含scatter
                if (Objects.equals(colum, TWO_COLUM_INDEX) || Objects.equals(colum, FOUR_COLUM_INDEX)) {
                    AssertUtil.assertTrue(!StringUtils.equals(symbolArr[row][colum].getId(), SymbolEnum.Scatter.getId()), ErrorCode.ERR_PARAM, "this 2,3 colum can not has scatter");
                }
            }
        }
    }
}

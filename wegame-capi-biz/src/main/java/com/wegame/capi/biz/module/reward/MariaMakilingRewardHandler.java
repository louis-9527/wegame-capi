package com.wegame.capi.biz.module.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
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

import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("mariaMakilingRewardHandler")
public class MariaMakilingRewardHandler extends AbstractRewardHandler {

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

    public RewardResultDo handle(RewardHandleDo rewardHandle) {
        long current = System.currentTimeMillis();
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(BigDecimal.ZERO)
                    .hasRewardFreeSpin(false)
                    .rewardLineDetailList(null)
                    .build();
        }
        // 3、计算倍率
        BigDecimal totalMultiple = mariaMakilingMultipleCalculateStrategy.calculateMultiple(rewardHandle.getGameType(), rewardHandle.getWheel());
        // 4、中奖线处理
        // 中奖总金额
        long totalRewardAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDetailDo> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo rewardLine : CollectionUtils.emptyIfNull(rewardLineList)) {
            // 计算赔率
            BigDecimal odds = mariaMakilingOddsCalculateStrategy.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
            BigDecimal lineRewardAmount = calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, totalMultiple);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardAmount(lineRewardAmount.longValue())
                    .odds(odds)
                    .multiple(totalMultiple)
                    .symbol(rewardLine.getRewardSymbol())
                    .rewardLineType(rewardLine.getRewardLineType())
                    .rewardLineSubType(rewardLine.getRewardLineSubType())
                    .rewardLineCount(rewardLine.getRewardLineCount())
                    .build());
        }
        // 5.构建中奖结果
        RewardResultDo rewardResult = RewardResultDo.builder()
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .betAmount(rewardHandle.getBetAmount())
                .totalRewardAmount(totalRewardAmount)
                .hasRewardFreeSpin(hasRewardScatter(rewardLineList))
                .rewardLineDetailList(rewardLineDetailList)
                .totalMultiple(totalMultiple)
                .build();
        // 6.返回中奖信息
        log.info("reward cost time: {} ms", System.currentTimeMillis() - current);
        return rewardResult;
    }

    private List<RewardLineDo> calcRewardLineList(GameInfoDo gameInfo, WheelDo wheel) {
        SymbolDo[][] symbolArr = wheel.getSymbolArr();
        // Map<第一列元素, TreeMap<列序号，满足规则的元素列表>
        Map<SymbolDo, TreeMap<Integer, List<SymbolDo>>> map = new HashMap<>();
        // 目标元素集合targetSymbolSet
        Set<SymbolDo> targetSymbolSet = new HashSet<>();
        // 1、处理第一列
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            SymbolDo targetSymbol = symbolArr[row][ONE_COLUM_INDEX];
            targetSymbolSet.add(targetSymbol);
            if (!map.containsKey(targetSymbol)) {
                map.put(targetSymbol, new TreeMap<>());
            }
        }
        // 2、循环处理(从第1列开始处理)
        for (int colum = ONE_COLUM_INDEX; colum < gameInfo.getColumn(); colum++) {
            Integer currentColum = colum;
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                SymbolDo currentSymbol = symbolArr[row][currentColum];
                for (SymbolDo targetSymbol : CollectionUtils.emptyIfNull(targetSymbolSet)) {
                    if (targetSymbol.isScatterSymbol() && !SCATTER_COLUM_INDEX_LIST.contains(currentColum)) {
                        continue;
                    }
                    if (!isMatchTargetSymbol(currentSymbol, targetSymbol)) {
                        continue;
                    }
                    TreeMap<Integer, List<SymbolDo>> treeMap = map.get(targetSymbol);
                    if (canPutIn(treeMap, currentSymbol, currentColum)) {
                        if (Objects.isNull(treeMap.get(currentColum))) {
                            List<SymbolDo> list = new ArrayList<>();
                            list.add(symbolArr[row][colum]);
                            treeMap.put(currentColum, list);
                        } else {
                            treeMap.get(currentColum).add(symbolArr[row][colum]);
                        }
                    }
                }
            }
        }
        // 3、统计中奖线
        List<RewardLineDo> list = buildRewardLineList(map);
        // 4、返回中奖线列表
        return list;
    }

    private boolean canPutIn(TreeMap<Integer, List<SymbolDo>> treeMap, SymbolDo currentSymbol, Integer currentColum) {
        // 第一列必处理
        if (Objects.equals(currentColum, ONE_COLUM_INDEX)) {
            return true;
        }
        if (currentSymbol.isScatterSymbol()) {
            // scatter元素，判断（逻辑上的）上一列的列表元素是否为空： 说明，scatter只能出现在135列
            int preColumIndex = SCATTER_COLUM_INDEX_LIST.get(SCATTER_COLUM_INDEX_LIST.indexOf(currentColum) - 1);
            return CollectionUtils.isNotEmpty(treeMap.get(preColumIndex));
        } else {
            // 非scatter元素，判断上一列的列表是否为空
            return CollectionUtils.isNotEmpty(treeMap.get(currentColum - 1));
        }
    }

    private List<RewardLineDo> buildRewardLineList(Map<SymbolDo, TreeMap<Integer, List<SymbolDo>>> map) {
        List<RewardLineDo> list = new ArrayList<>();
        for (Map.Entry<SymbolDo, TreeMap<Integer, List<SymbolDo>>> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (MapUtils.isEmpty(entry.getValue())) {
                continue;
            }
            TreeMap<Integer, List<SymbolDo>> columMap = entry.getValue();
            if (columMap.size() < RewardLineTypeEnum.THREE.getNo()) {
                continue;
            }
            int totalLineCount = columMap.values()
                    .stream()
                    .map(item -> item.size())
                    .reduce(1, (a, b) -> a * b);
            list.add(RewardLineDo.builder()
                    .rewardSymbol(entry.getKey())
                    .rewardLineType(RewardLineTypeEnum.valueOfNo(columMap.size()))
                    .rewardLineSubType(entry.getKey().isScatterSymbol() ? RewardLineSubTypeEnum.SCATTER : RewardLineSubTypeEnum.COMMON)
                    .rewardLineCount(totalLineCount)
                    .build());
        }
        return list;
    }

    private boolean isMatchTargetSymbol(SymbolDo symbol, SymbolDo targetSymbol) {
        return Objects.equals(symbol, targetSymbol)
                || Objects.equals(symbol.getSymbolType(), SymbolTypeEnum.WILD)
                || Objects.equals(targetSymbol.getSymbolType(), SymbolTypeEnum.WILD);
    }

    private void validate(RewardHandleDo rewardHandle) {
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

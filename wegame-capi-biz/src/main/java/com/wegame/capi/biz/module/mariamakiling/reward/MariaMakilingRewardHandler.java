package com.wegame.capi.biz.module.mariamakiling.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.RandomService;
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
@Component("mariaMakilingRewardHandler")
public class MariaMakilingRewardHandler extends AbstractRewardHandler<SymbolDo> {

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
    RandomService randomService;
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

    public RewardResultDo<SymbolDo> handle(RewardHandleDo<SymbolDo> rewardHandle) {
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<SymbolDo>> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        // 3、计算倍率
        BigDecimal totalMultiple = mariaMakilingMultipleCalculateStrategy.calculateMultiple(rewardHandle, rewardLineList);
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.<SymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(totalMultiple)
                    .hasRewardFreeWheel(false)
                    .rewardFreeWheelTimes(0)
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
            BigDecimal lineRewardAmount = calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, totalMultiple.add(BigDecimal.ONE));
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
                .rewardFreeWheelTimes(getFreeWheelNumber())
                .rewardLineDetailList(rewardLineDetailList)
                .totalMultiple(totalMultiple)
                .build();
        // 6.返回中奖信息
        return rewardResult;
    }

    private List<RewardLineDo<SymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<SymbolDo> wheel) {
        SymbolDo[][] symbolArr = wheel.getSymbolArr();
        // Map<第一列元素, TreeMap<列序号，满足规则的元素列表>
        Map<SymbolDo, TreeMap<Integer, List<RewardSymbolDo<SymbolDo>>>> map = new HashMap<>();
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
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            Integer currentColum = column;
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                Integer currentRow = row;
                SymbolDo currentSymbol = symbolArr[row][currentColum];
                for (SymbolDo targetSymbol : CollectionUtils.emptyIfNull(targetSymbolSet)) {
                    if (targetSymbol.isScatterSymbol() && !SCATTER_COLUM_INDEX_LIST.contains(currentColum)) {
                        continue;
                    }
                    if (!isMatchTargetSymbol(currentSymbol, targetSymbol)) {
                        continue;
                    }
                    TreeMap<Integer, List<RewardSymbolDo<SymbolDo>>> treeMap = map.get(targetSymbol);
                    if (canPutIn(treeMap, currentSymbol, currentColum)) {
                        RewardSymbolDo<SymbolDo> symbolIndex = RewardSymbolDo.<SymbolDo>builder()
                                .symbol(currentSymbol)
                                .index(IndexFactory.buildDo(currentRow, currentColum))
                                .build();
                        if (Objects.isNull(treeMap.get(currentColum))) {
                            List<RewardSymbolDo<SymbolDo>> list = new ArrayList<>();
                            list.add(symbolIndex);
                            treeMap.put(currentColum, list);
                        } else {
                            treeMap.get(currentColum).add(symbolIndex);
                        }
                    }
                }
            }
        }
        // 3、统计中奖线
        List<RewardLineDo<SymbolDo>> list = buildRewardLineList(map);
        // 4、返回中奖线列表
        return list;
    }

    private boolean canPutIn(TreeMap<Integer, List<RewardSymbolDo<SymbolDo>>> treeMap, SymbolDo currentSymbol, Integer currentColum) {
        // 第一列必处理
        if (Objects.equals(currentColum, ONE_COLUM_INDEX)) {
            return true;
        }
        if (currentSymbol.isScatterSymbol()) {
            // scatter元素，判断（逻辑上的）上一列的列表元素是否为空： 说明，scatter只能出现在135列
            int preColumnIndex = SCATTER_COLUM_INDEX_LIST.get(SCATTER_COLUM_INDEX_LIST.indexOf(currentColum) - 1);
            return CollectionUtils.isNotEmpty(treeMap.get(preColumnIndex));
        } else {
            // 非scatter元素，判断上一列的列表是否为空
            return CollectionUtils.isNotEmpty(treeMap.get(currentColum - 1));
        }
    }

    private List<RewardLineDo<SymbolDo>> buildRewardLineList(Map<SymbolDo, TreeMap<Integer, List<RewardSymbolDo<SymbolDo>>>> map) {
        List<RewardLineDo<SymbolDo>> list = new ArrayList<>();
        for (Map.Entry<SymbolDo, TreeMap<Integer, List<RewardSymbolDo<SymbolDo>>>> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (MapUtils.isEmpty(entry.getValue())) {
                continue;
            }
            TreeMap<Integer, List<RewardSymbolDo<SymbolDo>>> columMap = entry.getValue();
            if (columMap.size() < RewardLineTypeEnum.THREE.getNo()) {
                continue;
            }
            int totalLineCount = columMap.values()
                    .stream()
                    .map(item -> item.size())
                    .reduce(1, (a, b) -> a * b);
            list.add(RewardLineDo.<SymbolDo>builder()
                    .rewardSymbol(entry.getKey())
                    .rewardLineType(RewardLineTypeEnum.valueOfNo(columMap.size()))
                    .rewardLineSubType(entry.getKey().isScatterSymbol() ? RewardLineSubTypeEnum.SCATTER : RewardLineSubTypeEnum.COMMON)
                    .rewardLineCount(totalLineCount)
                    .rewardSymbolList(columMap.values()
                            .stream()
                            .filter(CollectionUtils::isNotEmpty)
                            .flatMap(List::stream)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()))
                    .build());
        }
        return list;
    }

    private boolean isMatchTargetSymbol(SymbolDo symbol, SymbolDo targetSymbol) {
        return StringUtils.equals(symbol.getId(), targetSymbol.getId())
                || symbol.isWildSymbol()
                || targetSymbol.isWildSymbol();
    }

    private Integer getFreeWheelNumber() {
        int randomNumber = randomService.randomInt(1, 100);
        int startVal = 0;
        for (Map.Entry<Integer, Integer> entry : CollectionUtils.emptyIfNull(mariaMakilingConfig.getProbabilisticConfig().getFreeWheelTimesProbabilisticConfig().entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return entry.getKey();
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }

    private void validate(RewardHandleDo<SymbolDo> rewardHandle) {
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
            for (int column = 0; column < gameInfo.getColumn(); column++) {
                // 校验：所有元素不能为空
                AssertUtil.assertNotNull(symbolArr[row][column], ErrorCode.ERR_PARAM, "all symbol can not be null");
                // 校验：第一列不能包含wild
                if (Objects.equals(column, ONE_COLUM_INDEX)) {
                    AssertUtil.assertTrue(!WILD_SYMBOL_MAP.containsKey(symbolArr[row][column].getId()), ErrorCode.ERR_PARAM, "this first column can not has wild");
                }
                // 校验：第二列 & 第4列不能包含scatter
                if (Objects.equals(column, TWO_COLUM_INDEX) || Objects.equals(column, FOUR_COLUM_INDEX)) {
                    AssertUtil.assertTrue(!StringUtils.equals(symbolArr[row][column].getId(), SymbolEnum.Scatter.getId()), ErrorCode.ERR_PARAM, "this 2,3 column can not has scatter");
                }
            }
        }
    }
}

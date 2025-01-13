//package com.wegame.capi.biz.module.reward;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.NumberUtil;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Lists;
//import com.wegame.base.enums.ErrorCode;
//import com.wegame.base.exception.BusinessException;
//import com.wegame.base.utils.AssertUtil;
//import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
//import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
//import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
//import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
//import com.wegame.capi.biz.domain.biz.reward.RewardRuleSymbolDo;
//import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
//import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
//import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexFactory;
//import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
//import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
//import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
//import com.wegame.capi.biz.service.BetRecordService;
//import com.wegame.capi.client.enums.GameTypeEnum;
//import com.wegame.capi.common.enums.RewardLineTypeEnum;
//import com.wegame.capi.common.enums.RewardSubTypeEnum;
//import com.wegame.capi.common.enums.SymbolEnum;
//import com.wegame.capi.common.enums.SymbolTypeEnum;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.collections4.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//import static com.wegame.capi.common.constants.GameConstants.*;
//
//@Slf4j
//@Component("mariaMakilingRewardHandler2")
//public class MariaMakilingRewardHandler2 extends AbstractRewardHandler2 {
//
//    private static final List<Integer> SCATTER_COLUM_INDEX_LIST = Arrays.asList(
//            ONE_COLUM_INDEX,
//            THREE_COLUM_INDEX,
//            FIVE_COLUM_INDEX);
//    private static final Map<String, SymbolEnum> WILD_SYMBOL_MAP = new ImmutableMap.Builder<String, SymbolEnum>()
//            .put(SymbolEnum.Wild.getId(), SymbolEnum.Wild)
//            .put(SymbolEnum.WildX2.getId(), SymbolEnum.WildX2)
//            .put(SymbolEnum.WildX3.getId(), SymbolEnum.WildX3)
//            .put(SymbolEnum.WildX5.getId(), SymbolEnum.WildX5)
//            .put(SymbolEnum.WildX10.getId(), SymbolEnum.WildX10)
//            .build();
//    @Autowired
//    OddsCalculateStrategy mariaMakilingOddsCalculateStrategy;
//    @Autowired
//    MultipleCalculateStrategy mariaMakilingMultipleCalculateStrategy;
//    @Autowired
//    BetRecordService betRecordService;
//
//    @Override
//    public OddsCalculateStrategy oddsCalculateStrategy() {
//        return mariaMakilingOddsCalculateStrategy;
//    }
//
//    @Override
//    public MultipleCalculateStrategy multipleCalculateStrategy() {
//        return mariaMakilingMultipleCalculateStrategy;
//    }
//
//    @Override
//    public void validate(RewardHandleDo rewardHandle) {
//        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
//        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
//        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
//        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
//        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
//        AssertUtil.assertNotNull(rewardHandle.getWheel().getIndex(), ErrorCode.ERR_PARAM, "wheel index is null");
//        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
//        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.MARIA_MAKILING), ErrorCode.ERR_PARAM, "just support game type of maria makiling");
//        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
//        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
//        // 校验：滚轮行数
//        GameInfoDo gameInfo = rewardHandle.getGameInfo();
//        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
//        for (int row = 0; row < gameInfo.getRow(); row++) {
//            // 校验：滚轮列数
//            AssertUtil.assertTrue(symbolArr[row].length == gameInfo.getColumn(), ErrorCode.ERR_PARAM, "symbol arr err");
//            for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
//                // 校验：所有元素不能为空
//                AssertUtil.assertNotNull(symbolArr[row][colum], ErrorCode.ERR_PARAM, "all symbol can not be null");
//                // 校验：第一列不能包含wild
//                if (Objects.equals(colum, ONE_COLUM_INDEX)) {
//                    AssertUtil.assertTrue(!WILD_SYMBOL_MAP.containsKey(symbolArr[row][colum].getId()), ErrorCode.ERR_PARAM, "this first colum can not has wild");
//                }
//                // 校验：第二列 & 第4列不能包含scatter
//                if (Objects.equals(colum, TWO_COLUM_INDEX) || Objects.equals(colum, FOUR_COLUM_INDEX)) {
//                    AssertUtil.assertTrue(!StringUtils.equals(symbolArr[row][colum].getId(), SymbolEnum.Scatter.getId()), ErrorCode.ERR_PARAM, "this 2,3 colum can not has scatter");
//                }
//            }
//        }
//    }
//
//    public boolean hasRewardFreeSpin(GameInfoDo gameInfo, WheelDo wheel) {
//        AssertUtil.assertNotNull(gameInfo, ErrorCode.ERR_PARAM, "gameInfo is null");
//        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_PARAM, "wheel is null");
//        SymbolDo[][] symbolArr = wheel.getSymbolArr();
//        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM, "wheel is null");
//        // 检查1，3，5列是否有Scatter，如果不存在，则1，3，5列各自随机一行的的元素设置为scatter
//        for (Integer columIndex : SCATTER_COLUM_INDEX_LIST) {
//            boolean columHasScatter = false;
//            for (int i = 0; i < gameInfo.getRow(); i++) {
//                SymbolDo symbol = symbolArr[i][columIndex];
//                if (Objects.equals(SymbolTypeEnum.SCATTER, symbol.getSymbolType())) {
//                    columHasScatter = true;
//                    break;
//                }
//            }
//            if (!columHasScatter) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /////////////// todo delete //////////////
//
//    public List<RewardLineDo> calcRewardLines(GameInfoDo gameInfo, WheelDo wheel) {
//        SymbolDo[][] symbolArr = wheel.getSymbolArr();
//        // Map<第一列元素, TreeMap<列序号，满足规则的元素列表>
//        Map<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> map = new HashMap<>();
//        //// 目标元素集合targetSymbolSet
//        Set<SymbolDo> targetSymbolSet = new HashSet<>();
//        // 1、处理第一列
//        for (int row = 0; row < gameInfo.getRow(); row++) {
//            SymbolDo targetSymbol = symbolArr[row][ONE_COLUM_INDEX];
//            targetSymbolSet.add(targetSymbol);
//            map.put(targetSymbol, new TreeMap<>());
//        }
//        // 2、循环处理(从第1列开始处理)
//        for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
//            Integer currentColum = colum;
//            for (int row = 0; row < gameInfo.getRow(); row++) {
//                Integer currentRow = row;
//                for (SymbolDo targetSymbol : CollectionUtils.emptyIfNull(targetSymbolSet)) {
//                    boolean isMatch = isMatchTargetSymbol(symbolArr[row][colum], targetSymbol);
//                    if (!isMatch) {
//                        continue;
//                    }
//                    TreeMap<Integer, List<RewardRuleSymbolDo>> treeMap = map.get(targetSymbol);
//                    if (Objects.equals(colum, ONE_COLUM_INDEX) || CollectionUtils.isNotEmpty(treeMap.get(currentColum - 1))) {
//                        RewardRuleSymbolDo rewardRuleSymbol = RewardRuleSymbolDo.builder()
//                                .symbol(symbolArr[row][colum])
//                                .symbolIndex(SymbolIndexFactory.build(currentRow, currentColum))
//                                .build();
//                        if (Objects.isNull(treeMap.get(currentColum))) {
//                            List<RewardRuleSymbolDo> list = new ArrayList<>();
//                            list.add(rewardRuleSymbol);
//                            treeMap.put(currentColum, list);
//                        } else {
//                            treeMap.get(currentColum).add(rewardRuleSymbol);
//                        }
//                    }
//                }
//            }
//        }
//        // 3、统计中奖线
//        List<RewardLineDo> list = new ArrayList<>();
//        // 4、返回中奖线列表
//        return list;
//    }
//
//    private List<RewardLineDo> statisticsRewardLineList(Map<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> map) {
//        List<RewardLineDo> list = Lists.newArrayList();
//        for (Map.Entry<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
//            SymbolDo symbolDo = entry.getKey();
//            if (MapUtils.isEmpty(entry.getValue())) {
//                continue;
//            }
//            TreeMap<Integer, List<RewardRuleSymbolDo>> columMap = entry.getValue();
//            if (columMap.size() < 3) {
//                continue;
//            }
//            buildRewardLine(list, 0, columMap, null, null);
//        }
//        // 结果组装
//        return list;
//    }
//
//    private void buildRewardLine(List<RewardLineDo> list, Integer colum, TreeMap<Integer, List<RewardRuleSymbolDo>> columMap, List<SymbolDo> symbolList, List<SymbolIndexDo> symbolIndexList) {
//        if (Objects.equals(colum, ONE_COLUM_INDEX)) {
//            symbolList = new ArrayList<>();
//            symbolIndexList = new ArrayList<>();
//            List<RewardRuleSymbolDo> zeroList = columMap.get(colum);
////            symbolList.add()
//        } else {
//
//        }
//    }
//
//    private boolean isMatchTargetSymbol(SymbolDo symbol, SymbolDo targetSymbol) {
//        return Objects.equals(symbol, targetSymbol)
//                || Objects.equals(symbol.getSymbolType(), SymbolEnum.Wild)
//                || Objects.equals(targetSymbol.getSymbolType(), SymbolEnum.Wild);
//    }
//
//    public RewardSubTypeEnum rewardSubType(RewardHandleDo rewardHandleDo, RewardResultDo rewardResultDo) {
//        Long betAmount = rewardHandleDo.getBetAmount();
//        Long rewardTotalAmount = rewardResultDo.getTotalRewardAmount();
//        if (Objects.isNull(betAmount) || Objects.isNull(rewardTotalAmount) || betAmount <= 0 || rewardTotalAmount <= 0) {
//            return RewardSubTypeEnum.NONE;
//        }
//        double rewardTimes = NumberUtil.div(rewardTotalAmount.longValue(), betAmount.longValue());
//        if (rewardTimes >= 5 && rewardTimes < 10) {
//            return RewardSubTypeEnum.NICE;
//        }
//        if (rewardTimes >= 10 && rewardTimes < 15) {
//            return RewardSubTypeEnum.MEGA;
//        }
//        if (rewardTimes >= 15 && rewardTimes < 20) {
//            return RewardSubTypeEnum.SUPER;
//        }
//        if (rewardTimes > 20) {
//            return RewardSubTypeEnum.SENSATIONAL;
//        }
//        return RewardSubTypeEnum.NONE;
//    }
//
//
//    private RewardLineDo generatorScatterRewardLines(SymbolDo symbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap) {
//        List<SymbolDo> rewardSymbolIdList = Lists.newArrayList();
//        List<SymbolIndexDo> rewardSymbolIndexList = Lists.newArrayList();
//
//        columTreeMap.values().stream().flatMap(Collection::stream).forEach(rewardRuleSymbolDo -> {
//            rewardSymbolIdList.add(rewardRuleSymbolDo.getSymbol());
//            rewardSymbolIndexList.add(rewardRuleSymbolDo.getSymbolIndex());
//        });
//        return RewardLineDo.builder()
//                .symbol(symbolDo)
//                .rewardLineType(this.getRewardLineType(rewardSymbolIdList.size()))
//                .rewardSymbolList(rewardSymbolIdList)
//                .rewardSymbolIndexList(rewardSymbolIndexList)
//                .build();
//    }
//
//    private List<RewardLineDo> generatorComonRewardLines(SymbolDo symbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap) {
//        List<RewardLineDo> complexRewardLineList = Lists.newArrayList();
//        List<RewardRuleSymbolDo> firstColumSymbolDoList = columTreeMap.get(0);
//        for (RewardRuleSymbolDo firstColumSymbolDo : firstColumSymbolDoList) {
//            loopFindRewardLine(symbolDo, 1, columTreeMap.keySet().size() - 1, columTreeMap, Lists.newArrayList(firstColumSymbolDo.getSymbolIndex()), Lists.newArrayList(firstColumSymbolDo.getSymbol()), complexRewardLineList);
//        }
//        return complexRewardLineList;
//    }
//
//    private void loopFindRewardLine(SymbolDo symbolDo, Integer currentColum, Integer maxColum, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap, List<SymbolIndexDo> rewardSymbolIndexList, List<SymbolDo> symbolIdList, List<RewardLineDo> complexRewardLineList) {
//        if (Objects.equals(currentColum, maxColum)) {
//            for (RewardRuleSymbolDo rewardRuleSymbolDo : columTreeMap.get(currentColum)) {
//                // 计算线
//                List<SymbolDo> currentLineSymbolIdList = Lists.newArrayList(symbolIdList);
//                List<SymbolIndexDo> currentRewardSymbolIndexList = Lists.newArrayList(rewardSymbolIndexList);
//                currentLineSymbolIdList.add(rewardRuleSymbolDo.getSymbol());
//                currentRewardSymbolIndexList.add(rewardRuleSymbolDo.getSymbolIndex());
//                RewardLineDo lineDo = RewardLineDo.builder()
//                        .symbol(symbolDo)
//                        .rewardLineType(this.getRewardLineType(currentLineSymbolIdList.size()))
//                        .rewardSymbolList(currentLineSymbolIdList)
//                        .rewardSymbolIndexList(currentRewardSymbolIndexList)
//                        .build();
//                complexRewardLineList.add(lineDo);
//            }
//        } else {
//            // 记录并继续循环
//            List<RewardRuleSymbolDo> rewardRuleSymbolDos = columTreeMap.get(currentColum);
//            currentColum++;
//            for (RewardRuleSymbolDo rewardRuleSymbolDo : rewardRuleSymbolDos) {
//                List<SymbolIndexDo> tempRewardSymbolIndexList = Lists.newArrayList(rewardSymbolIndexList);
//                List<SymbolDo> tempSymbolIdList = Lists.newArrayList(symbolIdList);
//                tempRewardSymbolIndexList.add(rewardRuleSymbolDo.getSymbolIndex());
//                tempSymbolIdList.add(rewardRuleSymbolDo.getSymbol());
//                loopFindRewardLine(symbolDo, currentColum, maxColum, columTreeMap, tempRewardSymbolIndexList, tempSymbolIdList, complexRewardLineList);
//            }
//        }
//    }
//
//    private RewardLineTypeEnum getRewardLineType(int linkCount) {
//        if (linkCount == 3) {
//            return RewardLineTypeEnum.THREE;
//        }
//        if (linkCount == 4) {
//            return RewardLineTypeEnum.FOUR;
//        }
//        if (linkCount == 5) {
//            return RewardLineTypeEnum.FIVE;
//        }
//        throw new BusinessException(ErrorCode.ERR_INTER, "invalid link count");
//    }
//
//    private void addSymbolMapping(Map<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> mappingMap, SymbolDo mappingMapKey, RewardRuleSymbolDo rewardRuleSymbolDo) {
//        Integer columNum = rewardRuleSymbolDo.getSymbolIndex().getColumIndex();
//        TreeMap<Integer, List<RewardRuleSymbolDo>> columListMap = mappingMap.get(mappingMapKey);
//        if (Objects.isNull(columListMap)) {
//            columListMap = new TreeMap<>();
//            mappingMap.put(mappingMapKey, columListMap);
//        }
//        // 是否有必要再添加。如果列不连续，就没有必要再添加(scatter除外)
//        if (!isScatter(mappingMapKey) && columNum > 0 && CollectionUtil.isEmpty(columListMap.get(columNum - 1))) {
//            return;
//        }
//
//        List<RewardRuleSymbolDo> symbolDoList = columListMap.get(columNum);
//        if (CollectionUtil.isEmpty(symbolDoList)) {
//            symbolDoList = Lists.newArrayList(rewardRuleSymbolDo);
//            columListMap.put(columNum, symbolDoList);
//        } else {
//            columListMap.get(columNum).add(rewardRuleSymbolDo);
//        }
//    }
//
//    private boolean isWild(SymbolDo symbolDo) {
//        SymbolTypeEnum symbolTypeEnum = Optional.ofNullable(symbolDo).map(SymbolDo::getSymbolType).orElse(null);
//        return Objects.equals(SymbolTypeEnum.WILD, symbolTypeEnum);
//    }
//
//    private boolean isScatter(SymbolDo symbolDo) {
//        SymbolTypeEnum symbolTypeEnum = Optional.ofNullable(symbolDo).map(SymbolDo::getSymbolType).orElse(null);
//        return Objects.equals(SymbolTypeEnum.SCATTER, symbolTypeEnum);
//    }
//
//    private boolean isFirstColumSymbolId(Map<SymbolDo, List<RewardRuleSymbolDo>> firstColumSymbolMapping, SymbolDo symbolDo) {
//        return Objects.nonNull(firstColumSymbolMapping.get(symbolDo));
//    }
//
//
//    public static void main(String[] args) {
//        List<List<Integer>> list = new ArrayList<>();
//        list.add(Arrays.asList(1,2));
//        list.add(Arrays.asList(3,4,5));
//        list.add(Arrays.asList(6,7,8,9));
//        // 确定列数
//        int cols = list.isEmpty() ? 0 : list.get(0).size();
//
//        // 遍历列
//        for (int j = 0; j < cols; j++) {
//            // 遍历行
//            for (int i = 0; i < list.size(); i++) {
//                List<Integer> row = list.get(i);
//                if (j < row.size()) { // 确保当前行有足够多的元素
//                    Integer element = row.get(j); // 获取当前列中的元素
//                    // 对 element 进行操作
//                    System.out.println("Element at column " + j + ", row " + i + ": " + element);
//                }
//            }
//        }
//    }
//}

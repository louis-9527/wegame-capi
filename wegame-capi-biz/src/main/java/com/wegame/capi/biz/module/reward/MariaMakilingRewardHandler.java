package com.wegame.capi.biz.module.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.game.*;
import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
import com.wegame.capi.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class MariaMakilingRewardHandler extends AbstractRewardHandler {

    @Autowired
    OddsCalculateStrategy mariaMakilingOddsCalculateStrategy;

    @Autowired
    MultipleCalculateStrategy mariaMakilingMultipleCalculateStrategy;

    @Override
    public OddsCalculateStrategy oddsCalculateStrategy() {
        return mariaMakilingOddsCalculateStrategy;
    }

    @Override
    public MultipleCalculateStrategy multipleCalculateStrategy() {
        return mariaMakilingMultipleCalculateStrategy;
    }

    public RewardRuleResultDo calcRewardLines(RewardRuleDo rewardRuleDo) {
        // 第一列中奖元素映射<元素,中奖规则元素列表>  // 初始化第一列数据
        Map<SymbolDo, List<RewardRuleSymbolDo>> firstColumSymbolMapping = this.initFirstColumSymbol(rewardRuleDo);
        // 本轮必要元素每一列出现的统计.<元素,<列号，本列中奖规则元素列表>>
        Map<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> secondColumSymbolMapping = new HashMap<>();
        // symbolId是否有wild参与<元素,boolean>
        Map<SymbolDo, Boolean> symbolIdHasWildMap = new HashMap<>();

        SymbolDo[][] symbolArr = rewardRuleDo.getSymbolArr();
        // 第一列初始化到统计的数据中去
        firstColumSymbolMapping.values().stream().flatMap(Collection::stream)
                .forEach(firstSymbol -> this.addSymbolMapping(secondColumSymbolMapping, firstSymbol.getSymbol(), firstSymbol));

        // 从第二列开始循环
        for (int row = 0; row < symbolArr.length; row++) {
            for (int colum = 1; colum < symbolArr[row].length; colum++) {
                SymbolDo symbolDo = symbolArr[row][colum];
                if (this.isWild(symbolDo)) {
                    // 所有第一列元素的都添加(scatter除外)
                    for (SymbolDo firstSymbol : firstColumSymbolMapping.keySet()) {
                        if (!isScatter(firstSymbol)) {
                            this.addSymbolMapping(secondColumSymbolMapping, firstSymbol, RewardRuleSymbolDo.builder().gameType(rewardRuleDo.getGameType()).symbol(symbolDo).coordinate(SymbolIndexDo.build(row, colum)).build());
                            symbolIdHasWildMap.put(firstSymbol, true);
                        }
                    }
                } else if (isFirstColumSymbolId(firstColumSymbolMapping, symbolDo)) {
                    this.addSymbolMapping(secondColumSymbolMapping, symbolDo, RewardRuleSymbolDo.builder().gameType(rewardRuleDo.getGameType()).symbol(symbolDo).coordinate(SymbolIndexDo.build(row, colum)).build());
                }
            }
        }
        // 中奖线统计
        return generatorRewardLines(symbolIdHasWildMap, secondColumSymbolMapping);
    }

    @Override
    public boolean hasFreeSpin(GameTypeEnum gameType, SymbolDo[][] symbolArr) {
        int scatterCount = 0;
        for (SymbolDo[] row : symbolArr) {
            for (SymbolDo element : row) {
                AssertUtil.assertNotNull(element, ErrorCode.ERR_INTER);
                if (scatterCount >= 3) {
                    return true;
                }
                if (Objects.equals(element.getSymbolType(), SymbolTypeEnum.SCATTER)) {
                    scatterCount++;
                }
            }
        }
        return scatterCount >= 3;
    }

    @Override
    public boolean bizValidate(RewardHandleDo rewardHandle) {
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.MARIA_MAKILING), ErrorCode.ERR_PARAM, "just support game type of maria makiling");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        // 滚轮校验:必须3*5
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        AssertUtil.assertTrue(symbolArr.length == 3, ErrorCode.ERR_PARAM, "symbol arr must be 3 row,5 colum");
        for (SymbolDo[] row : symbolArr) {
            AssertUtil.assertTrue(row.length == 5, ErrorCode.ERR_PARAM, "symbol arr must be 3 row,5 colum");
            // 所有元素不能为空
            Arrays.stream(row).forEach(rowSymbol -> AssertUtil.assertNotNull(rowSymbol, ErrorCode.ERR_PARAM, "all symbol can not be null"));
            // 第二列 & 第4列不能包含scatter
            AssertUtil.assertTrue(!Objects.equals(row[1].getId(), SymbolEnum.Scatter.getId()), ErrorCode.ERR_PARAM, "this index can not has scatter:the 2 colum");
            AssertUtil.assertTrue(!Objects.equals(row[3].getId(), SymbolEnum.Scatter.getId()), ErrorCode.ERR_PARAM, "this index can not has scatter:the 4 colum");
            // 第一列不能包含wild
            AssertUtil.assertTrue(!Objects.equals(row[0].getId(), SymbolEnum.Wild.getId()), ErrorCode.ERR_PARAM, "this index can not has wild:the 1 colum");
            AssertUtil.assertTrue(!Objects.equals(row[0].getId(), SymbolEnum.WildX2.getId()), ErrorCode.ERR_PARAM, "this index can not has wild:the 1 colum");
            AssertUtil.assertTrue(!Objects.equals(row[0].getId(), SymbolEnum.WildX3.getId()), ErrorCode.ERR_PARAM, "this index can not has wild:the 1 colum");
            AssertUtil.assertTrue(!Objects.equals(row[0].getId(), SymbolEnum.WildX5.getId()), ErrorCode.ERR_PARAM, "this index can not has wild:the 1 colum");
            AssertUtil.assertTrue(!Objects.equals(row[0].getId(), SymbolEnum.WildX10.getId()), ErrorCode.ERR_PARAM, "this index can not has wild:the 1 colum");
        }
        return true;
    }

    private Map<SymbolDo, List<RewardRuleSymbolDo>> initFirstColumSymbol(RewardRuleDo rewardRuleDo) {
        Map<SymbolDo, List<RewardRuleSymbolDo>> firstColumSymbolMapping = new HashMap<>();
        SymbolDo[][] symbolArr = rewardRuleDo.getSymbolArr();
        firstColumSymbolMapping.put(symbolArr[0][0], Lists.newArrayList());
        firstColumSymbolMapping.put(symbolArr[1][0], Lists.newArrayList());
        firstColumSymbolMapping.put(symbolArr[2][0], Lists.newArrayList());

        firstColumSymbolMapping.get(symbolArr[0][0]).add(RewardRuleSymbolDo.builder().gameType(rewardRuleDo.getGameType()).symbol(symbolArr[0][0]).coordinate(SymbolIndexDo.build(0, 0)).build());
        firstColumSymbolMapping.get(symbolArr[1][0]).add(RewardRuleSymbolDo.builder().gameType(rewardRuleDo.getGameType()).symbol(symbolArr[1][0]).coordinate(SymbolIndexDo.build(1, 0)).build());
        firstColumSymbolMapping.get(symbolArr[2][0]).add(RewardRuleSymbolDo.builder().gameType(rewardRuleDo.getGameType()).symbol(symbolArr[2][0]).coordinate(SymbolIndexDo.build(2, 0)).build());
        return firstColumSymbolMapping;
    }

    private RewardRuleResultDo generatorRewardLines(Map<SymbolDo, Boolean> symbolIdHasWildMap, Map<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> secondColumSymbolMapping) {
        // 中奖线统计普通元素，scatter
        // 中奖线列表
        List<RewardRuleLineDo> rewardRuleLineList = Lists.newArrayList();
        for (Map.Entry<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> columSymbolEntry : secondColumSymbolMapping.entrySet()) {
            SymbolDo symbolDo = columSymbolEntry.getKey();
            TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap = columSymbolEntry.getValue();
            if (columTreeMap.keySet().size() >= 3) {

                if (!isScatter(symbolDo)) {
                    List<RewardRuleLineDo> complexRewardLineList = this.generatorComonRewardLines(symbolDo, columTreeMap);
                    if (CollectionUtil.isNotEmpty(complexRewardLineList)) {
                        rewardRuleLineList.addAll(complexRewardLineList);
                    }
                } else {
                    // scatter
                    RewardRuleLineDo ruleLineDo = this.generatorScatterRewardLines(symbolDo, columTreeMap);
                    if (Objects.nonNull(ruleLineDo)) {
                        rewardRuleLineList.add(ruleLineDo);
                    }
                }
            }
        }
        // 结果组装
        return RewardRuleResultDo.builder()
                .rewardRuleLineList(rewardRuleLineList)
                .build();
    }

    private RewardRuleLineDo generatorScatterRewardLines(SymbolDo symbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap) {
        List<SymbolDo> rewardSymbolIdList = Lists.newArrayList();
        List<SymbolIndexDo> rewardSymbolIndexList = Lists.newArrayList();

        columTreeMap.values().stream().flatMap(Collection::stream).forEach(rewardRuleSymbolDo -> {
            rewardSymbolIdList.add(rewardRuleSymbolDo.getSymbol());
            rewardSymbolIndexList.add(rewardRuleSymbolDo.getCoordinate());
        });
        return RewardRuleLineDo.builder()
                .symbol(symbolDo)
                .rewardLineType(this.getRewardLineType(rewardSymbolIdList.size()))
                .rewardSymbolIdList(rewardSymbolIdList)
                .rewardSymbolIndexList(rewardSymbolIndexList)
                .build();
    }

    private List<RewardRuleLineDo> generatorComonRewardLines(SymbolDo symbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap) {
        List<RewardRuleLineDo> complexRewardLineList = Lists.newArrayList();
        List<RewardRuleSymbolDo> firstColumSymbolDoList = columTreeMap.get(0);
        for (RewardRuleSymbolDo firstColumSymbolDo : firstColumSymbolDoList) {
            loopFindRewardLine(symbolDo, 1, columTreeMap.keySet().size() - 1, columTreeMap, Lists.newArrayList(firstColumSymbolDo.getCoordinate()), Lists.newArrayList(firstColumSymbolDo.getSymbol()), complexRewardLineList);
        }
        return complexRewardLineList;
    }

    private void loopFindRewardLine(SymbolDo symbolDo, Integer currentColum, Integer maxColum, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap, List<SymbolIndexDo> rewardSymbolIndexList, List<SymbolDo> symbolIdList, List<RewardRuleLineDo> complexRewardLineList) {
        if (Objects.equals(currentColum, maxColum)) {
            for (RewardRuleSymbolDo rewardRuleSymbolDo : columTreeMap.get(currentColum)) {
                // 计算线
                List<SymbolDo> currentLineSymbolIdList = Lists.newArrayList(symbolIdList);
                List<SymbolIndexDo> currentRewardSymbolIndexList = Lists.newArrayList(rewardSymbolIndexList);
                currentLineSymbolIdList.add(rewardRuleSymbolDo.getSymbol());
                currentRewardSymbolIndexList.add(rewardRuleSymbolDo.getCoordinate());
                RewardRuleLineDo lineDo = RewardRuleLineDo.builder()
                        .symbol(symbolDo)
                        .rewardLineType(this.getRewardLineType(currentLineSymbolIdList.size()))
                        .rewardSymbolIdList(currentLineSymbolIdList)
                        .rewardSymbolIndexList(currentRewardSymbolIndexList)
                        .build();
                complexRewardLineList.add(lineDo);
            }
        } else {
            // 记录并继续循环
            List<RewardRuleSymbolDo> rewardRuleSymbolDos = columTreeMap.get(currentColum);
            currentColum++;
            for (RewardRuleSymbolDo rewardRuleSymbolDo : rewardRuleSymbolDos) {
                List<SymbolIndexDo> tempRewardSymbolIndexList = Lists.newArrayList(rewardSymbolIndexList);
                List<SymbolDo> tempSymbolIdList = Lists.newArrayList(symbolIdList);
                tempRewardSymbolIndexList.add(rewardRuleSymbolDo.getCoordinate());
                tempSymbolIdList.add(rewardRuleSymbolDo.getSymbol());
                loopFindRewardLine(symbolDo, currentColum, maxColum, columTreeMap, tempRewardSymbolIndexList, tempSymbolIdList, complexRewardLineList);
            }
        }
    }

    private List<RewardRuleLineDo> generatorRewardLinesForSimple(SymbolDo symbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap) {
        // 获取每一列的个数并计算出总的个数
        int lineCount = 1;
        for (Integer colum : columTreeMap.keySet()) {
            lineCount = lineCount * columTreeMap.get(colum).size();
        }

        if (lineCount <= 0) {
            return null;
        }
        List<RewardRuleLineDo> simpleRewardLineList = Lists.newArrayList();
        List<SymbolDo> rewardSymbolList = Lists.newArrayList();

        for (Integer colum : columTreeMap.keySet()) {
            rewardSymbolList.add(symbolDo);
        }

        for (int i = 0; i < lineCount; i++) {
            RewardRuleLineDo lineDo = RewardRuleLineDo.builder()
                    .symbol(symbolDo)
                    .rewardLineType(this.getRewardLineType(columTreeMap.keySet().size()))
                    .rewardSymbolIdList(rewardSymbolList)
                    .build();
            simpleRewardLineList.add(lineDo);
        }

        return simpleRewardLineList;
    }

    private RewardLineTypeEnum getRewardLineType(int linkCount) {
        if (linkCount == 3) {
            return RewardLineTypeEnum.THREE;
        }
        if (linkCount == 4) {
            return RewardLineTypeEnum.FOUR;
        }
        if (linkCount == 5) {
            return RewardLineTypeEnum.FIVE;
        }
        throw new BusinessException(ErrorCode.ERR_INTER, "invalid link count");
    }

    private void addSymbolMapping(Map<SymbolDo, TreeMap<Integer, List<RewardRuleSymbolDo>>> mappingMap, SymbolDo mappingMapKey, RewardRuleSymbolDo rewardRuleSymbolDo) {
        Integer columNum = rewardRuleSymbolDo.getCoordinate().getColumIndex();
        TreeMap<Integer, List<RewardRuleSymbolDo>> columListMap = mappingMap.get(mappingMapKey);
        if (Objects.isNull(columListMap)) {
            columListMap = new TreeMap<>();
            mappingMap.put(mappingMapKey, columListMap);
        }
        // 是否有必要再添加。如果列不连续，就没有必要再添加(scatter除外)
        if (!isScatter(mappingMapKey) && columNum > 0 && CollectionUtil.isEmpty(columListMap.get(columNum - 1))) {
            return;
        }

        List<RewardRuleSymbolDo> symbolDoList = columListMap.get(columNum);
        if (CollectionUtil.isEmpty(symbolDoList)) {
            symbolDoList = Lists.newArrayList(rewardRuleSymbolDo);
            columListMap.put(columNum, symbolDoList);
        } else {
            columListMap.get(columNum).add(rewardRuleSymbolDo);
        }
    }

    private boolean isWild(SymbolDo symbolDo) {
        SymbolTypeEnum symbolTypeEnum = Optional.ofNullable(symbolDo).map(SymbolDo::getSymbolType).orElse(null);
        return Objects.equals(SymbolTypeEnum.WILD, symbolTypeEnum);
    }

    private boolean isScatter(SymbolDo symbolDo) {
        SymbolTypeEnum symbolTypeEnum = Optional.ofNullable(symbolDo).map(SymbolDo::getSymbolType).orElse(null);
        return Objects.equals(SymbolTypeEnum.SCATTER, symbolTypeEnum);
    }

    private boolean isFirstColumSymbolId(Map<SymbolDo, List<RewardRuleSymbolDo>> firstColumSymbolMapping, SymbolDo symbolDo) {
        return Objects.nonNull(firstColumSymbolMapping.get(symbolDo));
    }


}

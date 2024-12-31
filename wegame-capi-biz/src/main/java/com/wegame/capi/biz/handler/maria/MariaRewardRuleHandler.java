package com.wegame.capi.biz.handler.maria;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.game.*;
import com.wegame.capi.biz.handler.RewardRuleHandler;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Component("mariaMakilingRewardRuleHandler")
public class MariaRewardRuleHandler implements RewardRuleHandler {

    private static final List<SymbolTypeEnum> WILD_LIST = Lists.newArrayList(SymbolTypeEnum.Wild, SymbolTypeEnum.WildX2, SymbolTypeEnum.WildX3, SymbolTypeEnum.WildX5, SymbolTypeEnum.WildX10);


    @Override
    public RewardRuleResultDo handle(RewardRuleDo rewardRuleDo) {
        // 第一列中奖元素映射<元素id,元素>
        Map<Integer, RewardRuleSymbolDo> firstColumSymbolMapping = new HashMap<>();
        // 本轮必要元素每一列出现的统计.<元素id,<列号，本列元素列表>>
        Map<Integer, TreeMap<Integer, List<RewardRuleSymbolDo>>> secondColumSymbolMapping = new HashMap<>();
        // symbolId是否有wild参与<symbolId,boolean>
        Map<Integer, Boolean> symbolIdHasWildMap = new HashMap<>();

        // 初始化第一列数据
        Integer[][] rollerGrid = rewardRuleDo.getRollerGrid();
        firstColumSymbolMapping.put(rollerGrid[0][0], RewardRuleSymbolDo.builder().gameId(rewardRuleDo.getGameId()).id(rollerGrid[0][0]).coordinate(new int[]{0, 0}).build());
        firstColumSymbolMapping.put(rollerGrid[1][0], RewardRuleSymbolDo.builder().gameId(rewardRuleDo.getGameId()).id(rollerGrid[1][0]).coordinate(new int[]{1, 0}).build());
        firstColumSymbolMapping.put(rollerGrid[2][0], RewardRuleSymbolDo.builder().gameId(rewardRuleDo.getGameId()).id(rollerGrid[2][0]).coordinate(new int[]{2, 0}).build());
        // 第一列初始化到统计的数据中去
        firstColumSymbolMapping.values().forEach(firstSymbol -> this.addSymbolMapping(secondColumSymbolMapping, firstSymbol.getId(), firstSymbol));

        // 从第二列开始循环
        for (int row = 0; row < rollerGrid.length; row++) {
            for (int colum = 1; colum < rollerGrid[row].length; colum++) {
                Integer symbolId = rollerGrid[row][colum];
                if (this.isWild(symbolId)) {
                    // 所有第一列的都添加
                    for (Integer firstSymbolId : firstColumSymbolMapping.keySet()) {
                        this.addSymbolMapping(secondColumSymbolMapping, firstSymbolId, RewardRuleSymbolDo.builder().gameId(rewardRuleDo.getGameId()).id(symbolId).coordinate(new int[]{row, colum}).build());
                        symbolIdHasWildMap.put(firstSymbolId, true);
                    }
                    // wild 元素添加
                    this.addSymbolMapping(secondColumSymbolMapping, symbolId, RewardRuleSymbolDo.builder().gameId(rewardRuleDo.getGameId()).id(symbolId).coordinate(new int[]{row, colum}).build());
                } else if (isFirstColumSymbolId(firstColumSymbolMapping, symbolId)) {
                    this.addSymbolMapping(secondColumSymbolMapping, symbolId, RewardRuleSymbolDo.builder().gameId(rewardRuleDo.getGameId()).id(symbolId).coordinate(new int[]{row, colum}).build());
                }
            }
        }

        // 中奖线统计
        return generatorRewardLines(rewardRuleDo.getGameId(), firstColumSymbolMapping, symbolIdHasWildMap, secondColumSymbolMapping);
    }

    private RewardRuleResultDo generatorRewardLines(String gameId, Map<Integer, RewardRuleSymbolDo> firstColumSymbolMapping, Map<Integer, Boolean> symbolIdHasWildMap, Map<Integer, TreeMap<Integer, List<RewardRuleSymbolDo>>> secondColumSymbolMapping) {
        // 中奖线统计普通元素，scatter
        // wild id列表
        List<Integer> wildSymbolIdList = Lists.newArrayList();
        // 中奖线列表
        List<RewardRuleLineDo> rewardRuleLineList = Lists.newArrayList();
        for (Map.Entry<Integer, TreeMap<Integer, List<RewardRuleSymbolDo>>> columSymbolEntry : secondColumSymbolMapping.entrySet()) {
            Integer symbolId = columSymbolEntry.getKey();
            TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap = columSymbolEntry.getValue();
            if (isWild(symbolId)) {
                wildSymbolIdList.add(symbolId);
            } else if (columTreeMap.keySet().size() >= 3) {
                // 区分是否有wild的参与，没有就走简单算法，有走复杂算法
                if (Objects.isNull(symbolIdHasWildMap.get(symbolId))) {
                    // 简单算法
                    List<RewardRuleLineDo> simpleRewardLineList = this.generatorRewardLinesForSimple(symbolId, columTreeMap);
                    if (Objects.nonNull(simpleRewardLineList)) {
                        rewardRuleLineList.addAll(simpleRewardLineList);
                    }
                } else {
                    // 复杂算法
                    List<RewardRuleLineDo> complexRewardLineList = this.generatorRewardLinesForComplex(symbolId, columTreeMap);
                    if (Objects.nonNull(complexRewardLineList)) {
                        rewardRuleLineList.addAll(complexRewardLineList);
                    }
                }
            }
        }

        // 结果组装
        return RewardRuleResultDo.builder()
                .gameId(gameId)
                .rewardRuleLineList(rewardRuleLineList)
                .wildSymbolIdList(wildSymbolIdList).build();
    }

    private List<RewardRuleLineDo> generatorRewardLinesForComplex(Integer symbolId, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap) {
        // 排除非wild列数小于3的数据

        // 第一列所有元素和其他列所有元素做笛卡尔积
        List<RewardRuleLineDo> complexRewardLineList = Lists.newArrayList();
        RewardRuleLineDo currentLineDo = RewardRuleLineDo.builder()
                .symbolId(symbolId)
                .linkCount(columTreeMap.keySet().size())
                .rewardSymbolIdList(Lists.newArrayList())
                .build();
        complexRewardLineList.add(currentLineDo);
//        currentLineDo.getRewardSymbolIdList().add(firstColumSymbolDo.getId());
        List<RewardRuleSymbolDo> firstColumSymbolDoList = columTreeMap.get(0);
        for (RewardRuleSymbolDo firstColumSymbolDo : firstColumSymbolDoList) {


            for (int colum = 1; colum < columTreeMap.keySet().size() - 1; colum++) {
                List<RewardRuleSymbolDo> otherColumSymbolDoList = columTreeMap.get(colum);
                for (RewardRuleSymbolDo otherColumSymbolDo : otherColumSymbolDoList) {
                    //


                }


            }


        }

        return null;
    }

    private List<RewardRuleLineDo> generatorRewardLinesForSimple(Integer symbolId, TreeMap<Integer, List<RewardRuleSymbolDo>> columTreeMap) {
        // 获取每一列的个数并计算出总的个数
        Integer lineCount = columTreeMap.keySet().stream().reduce((colum1, colum2) -> columTreeMap.get(colum1).size() * columTreeMap.get(colum2).size()).orElse(0);
        if (lineCount <= 0) {
            return null;
        }
        Integer linkCount = columTreeMap.keySet().size();
        List<RewardRuleLineDo> simpleRewardLineList = Lists.newArrayList();

        IntStream.of(lineCount).forEach(i -> {
            RewardRuleLineDo lineDo = RewardRuleLineDo.builder()
                    .symbolId(symbolId)
                    .linkCount(linkCount)
                    // todo 暂时不要
//                    .rewardCoordinate()
                    .rewardSymbolIdList(Lists.newArrayList(symbolId))
                    .build();
            simpleRewardLineList.add(lineDo);

        });
        return simpleRewardLineList;
    }

    private void addSymbolMapping(Map<Integer, TreeMap<Integer, List<RewardRuleSymbolDo>>> mappingMap, Integer mappingMapKey, RewardRuleSymbolDo rewardRuleSymbolDo) {
        Integer columNum = rewardRuleSymbolDo.getCoordinate()[1];
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

    private boolean isWild(Integer symbolId) {
        return WILD_LIST.contains(SymbolTypeEnum.valueOfId(symbolId));
    }

    private boolean isScatter(Integer symbolId) {
        SymbolTypeEnum symbolType = SymbolTypeEnum.valueOfId(symbolId);
        return Objects.equals(symbolType, SymbolTypeEnum.Scatter);
    }

    private boolean isFirstColumSymbolId(Map<Integer, RewardRuleSymbolDo> firstColumSymbolMapping, Integer symbolId) {
        return Objects.nonNull(firstColumSymbolMapping.get(symbolId));
    }
}

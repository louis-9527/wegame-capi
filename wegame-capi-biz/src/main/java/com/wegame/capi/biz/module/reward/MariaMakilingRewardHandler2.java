package com.wegame.capi.biz.module.reward;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexFactory;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.reward.algorithm.SymbolMultiTreeNode;
import com.wegame.capi.biz.module.reward.algorithm.SymbolMultiTreeSolution;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("mariaMakilingRewardHandler2")
public class MariaMakilingRewardHandler2 extends AbstractRewardHandler2 {

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

    @Override
    public OddsCalculateStrategy oddsCalculateStrategy() {
        return mariaMakilingOddsCalculateStrategy;
    }

    @Override
    public MultipleCalculateStrategy multipleCalculateStrategy() {
        return mariaMakilingMultipleCalculateStrategy;
    }

    @Override
    public void validate(RewardHandleDo rewardHandle) {
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

    @Override
    public List<RewardLineDo> calcRewardLineList(GameInfoDo gameInfo, WheelDo wheel) {
        // 构建-多叉树
        SymbolMultiTreeNode root = buildTree(gameInfo, wheel);
        // 查找-多叉树路径列表
        List<List<SymbolMultiTreeNode>> pathList = SymbolMultiTreeSolution.findTreePathList(root);
        // 构建-中奖线列表
        List<RewardLineDo> rewardLineList = new ArrayList<>();
        for (List<SymbolMultiTreeNode> path : CollectionUtils.emptyIfNull(pathList)) {
            if (path.size() < 4) {
                continue;
            }
            // 移除-根节点
            path.remove(0);
            // 中奖元素：第一个元素为中奖元素
            SymbolDo rewardSymbol = path.get(0).getSymbol();
            RewardLineDo rewardLine = RewardLineDo.builder()
                    .rewardSymbol(rewardSymbol)
                    .rewardLineType(RewardLineTypeEnum.valueOfNo(path.size()))
                    .rewardLineSubType(rewardSymbol.isScatterSymbol() ? RewardLineSubTypeEnum.SCATTER : RewardLineSubTypeEnum.SCATTER)
                    .rewardSymbolList(path.stream().map(SymbolMultiTreeNode::getSymbol).collect(Collectors.toList()))
                    .rewardSymbolIndexList(path.stream().map(SymbolMultiTreeNode::getSymbolIndex).collect(Collectors.toList()))
                    .build();
            rewardLineList.add(rewardLine);
        }
        // 返回
        return rewardLineList;
    }

    public SymbolMultiTreeNode buildTree(GameInfoDo gameInfo, WheelDo wheel) {
        // 1、数组转换
        SymbolDo[][] symbolArr = wheel.getSymbolArr();
        SymbolMultiTreeNode[][] nodeArr = new SymbolMultiTreeNode[gameInfo.getRow()][gameInfo.getColumn()];
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            for (int colum = ONE_COLUM_INDEX; colum < gameInfo.getColumn(); colum++) {
                SymbolMultiTreeNode node = SymbolMultiTreeNode.builder()
                        .symbol(symbolArr[row][colum])
                        .symbolIndex(SymbolIndexFactory.build(row, colum))
                        .build();
                nodeArr[row][colum] = node;
            }
        }
        // 2、构建TreeNode
        List<SymbolMultiTreeNode> targetNodes = new ArrayList<>();
        List<SymbolMultiTreeNode> tempTargetNodes = new ArrayList<>();
        SymbolMultiTreeNode oneColumScatter = null;
        SymbolMultiTreeNode threeColumScatter = null;
        SymbolMultiTreeNode fiveColumScatter = null;
        //// 2.1构建根节点
        SymbolMultiTreeNode root = SymbolMultiTreeNode.builder()
                .symbolIndex(SymbolIndexFactory.build(-1, -1))
                .symbol(null)
                .children(targetNodes)
                .build();
        //// 2.2构建子节点：按列循环处理 nodeArr
        for (int colum = ONE_COLUM_INDEX; colum < gameInfo.getColumn(); colum++) {
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.equals(colum, ONE_COLUM_INDEX)) {
                    targetNodes.add(nodeArr[row][colum]);
                    if (nodeArr[row][colum].getSymbol().isScatterSymbol()) {
                        oneColumScatter = nodeArr[row][colum];
                    }
                } else {
                    SymbolMultiTreeNode currentNode = nodeArr[row][colum];
                    tempTargetNodes.add(currentNode);
                    if (Objects.equals(colum, THREE_COLUM_INDEX) && nodeArr[row][colum].getSymbol().isScatterSymbol()) {
                        threeColumScatter = nodeArr[row][colum];
                        if (Objects.nonNull(oneColumScatter)) {
                            oneColumScatter.addChildren(threeColumScatter);
                        }
                    } else if (Objects.equals(colum, FIVE_COLUM_INDEX) && nodeArr[row][colum].getSymbol().isScatterSymbol()) {
                        fiveColumScatter = nodeArr[row][colum];
                        if (Objects.nonNull(threeColumScatter)) {
                            threeColumScatter.addChildren(fiveColumScatter);
                        }
                    }
                    for (SymbolMultiTreeNode targetNode : CollectionUtils.emptyIfNull(targetNodes)) {
                        boolean isMatch = isMatchTargetSymbol(currentNode.getSymbol(), targetNode.getSymbol());
                        if (isMatch) {
                            targetNode.addChildren(currentNode);
                        }
                    }
                }
            }
            if (!Objects.equals(colum, ONE_COLUM_INDEX)) {
                targetNodes = tempTargetNodes;
            }
        }
        // 3、返回
        return root;
    }

    private boolean isMatchTargetSymbol(SymbolDo symbol, SymbolDo targetSymbol) {
        return Objects.equals(symbol, targetSymbol)
                || Objects.equals(symbol.getSymbolType(), SymbolEnum.Wild)
                || Objects.equals(targetSymbol.getSymbolType(), SymbolEnum.Wild);
    }
}

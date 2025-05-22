package com.wegame.capi.biz.module.lamang;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.*;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class LamangSymbolUtil {


    public static boolean hasSuperWild(LamangSymbolDo[][] symbolArr) {
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && StringUtils.equals(symbolArr[row][col].getId(), LAMANG_SUPER_WILD.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasBigWildInRow(LamangSymbolDo[][] symbolArr, int row) {
        for (int col = 0; col < symbolArr[row].length; col++) {
            if (Objects.nonNull(symbolArr[row][col]) && StringUtils.equals(symbolArr[row][col].getId(), LAMANG_BIG_WILD.getId())) {
                return true;
            }
        }
        return false;
    }

    public static int countIndexSymbol(LamangSymbolDo[][] symbolArr, SymbolEnum symbol) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && StringUtils.equals(symbolArr[row][col].getId(), symbol.getId())) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public static int countIndexSymbol(LamangSymbolDo[][] symbolArr, SymbolEnum symbol, int column) {
        int count = 0;
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && StringUtils.equals(symbolArr[row][column].getId(), symbol.getId())) {
                count = count + 1;
            }
        }
        return count;
    }

    public static LamangSymbolDo[][] copySymbolArr(LamangSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        LamangSymbolDo[][] newSymbolArr = new LamangSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = null;
                } else if (symbolArr[i][j].isPlaceholderSymbol()) {
                    newSymbolArr[i][j] = LAMANG_SYMBOL_PLACEHOLDER;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }


    /**
     * 消除中奖元素 <br/>
     * **** 中奖元素参与多条中奖线，则只做一次消除<br/>
     * 1、scatter元素不做消除<br/>
     * 2、普通元素：<br/>
     * 2.1 带金边，元素变成wild元素<br/>
     * 2.2 不带金边，直接消除<br/>
     * 3、wild元素：消除次数减1，若减一后消除次数为0，则消除。 否则不消除<br/>
     *
     * @param symbolArr
     * @param rewardResult
     */
    public static void clearRewardSymbol(LamangSymbolDo[][] symbolArr, RewardResultDo<LamangSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        List<RewardSymbolDo<LamangSymbolDo>> rewardSymbolList = rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<LamangSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
        rewardSymbolList.parallelStream().forEach(rewardSymbol -> clearIndexRewardSymbol(symbolArr, rewardSymbol));
    }

    private static void clearIndexRewardSymbol(LamangSymbolDo[][] symbolArr, RewardSymbolDo<LamangSymbolDo> rewardSymbol) {
        if (rewardSymbol.getSymbol().isScatterSymbol()) {
            // scatter 元素， 不做消除
            // do nothing;
        } else if (rewardSymbol.getSymbol().isWildSymbol()) {
            // wild 元素
            removeRewardSymbol(symbolArr, rewardSymbol);

        } else {
            // 普通元素消除
            IndexDo indexDo = rewardSymbol.getIndex();
            LamangSymbolDo commonSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
            if (commonSymbol.isSymbolGoldBorder()) {
                // 带金边普通元素，转换为Wild元素（元素大小转换为消除次数）
                LamangSymbolDo wildSymbol = LamangSymbolDo.builder()
                        .id(SymbolEnum.Wild.getId())
                        .name(SymbolEnum.Wild.getName())
                        .symbolType(SymbolTypeEnum.WILD)
                        .symbolGoldBorder(false)
                        .build();
                symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = wildSymbol;
            } else {
                // 不带金边普通元素，直接移除
                removeRewardSymbol(symbolArr, rewardSymbol);
            }
        }
    }

    /**
     * 移除元素
     *
     * @param symbolArr
     * @param rewardSymbol
     */
    public static void removeRewardSymbol(LamangSymbolDo[][] symbolArr, RewardSymbolDo<LamangSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        // 直接移除
        symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null;
    }

    /**
     * 重建元素数组：（按列下移处理）
     *
     * @param symbolArr
     */
    public static void rebuildSymbolArr(LamangSymbolDo[][] symbolArr, GameInfoDo gameInfoDo) {

        // 非空元素下落
        for (int column = ONE_COLUM_INDEX; column < gameInfoDo.getColumn(); column++) {
            int currentRow = gameInfoDo.getRow() - 1;
            for (int row = gameInfoDo.getRow() - 1; row >= ONE_ROW_INDEX; row--) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    if (Objects.equals(currentRow, row)) {
                        // 不做移动
                        // do nothing
                    } else {
                        // 移动
                        LamangSymbolDo temp = symbolArr[row][column];
                        symbolArr[currentRow][column] = temp;
                        symbolArr[row][column] = null;
                    }
                    currentRow--;
                }
            }
        }
    }
}

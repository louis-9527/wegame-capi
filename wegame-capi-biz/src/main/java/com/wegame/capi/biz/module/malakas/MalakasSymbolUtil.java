package com.wegame.capi.biz.module.malakas;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

public class MalakasSymbolUtil {


    public static MalakasSymbolDo[][] copySymbolArr(MalakasSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        MalakasSymbolDo[][] newSymbolArr = new MalakasSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newSymbolArr[i][j] = symbolArr[i][j].copy();
            }
        }
        return newSymbolArr;
    }

    /**
     * 重建元素数组：（非空元素按行（第一行）左移，按列下移处理）
     *
     * @param symbolArr
     */
    public static void rebuildSymbolArr(MalakasSymbolDo[][] symbolArr, GameInfoDo gameInfoDo) {
        // 处理第一行：按行处理(非空元素左移，第一列元素&最后一列元素为空)
        int currentColum = 1;
        for (int colum = 1; colum < gameInfoDo.getColumn() - 1; colum++) {
            if (Objects.nonNull(symbolArr[ONE_ROW_INDEX][colum])) {
                MalakasSymbolDo temp = symbolArr[ONE_ROW_INDEX][colum];
                symbolArr[ONE_ROW_INDEX][currentColum] = temp;
                if (!Objects.equals(currentColum, colum)) {
                    symbolArr[ONE_ROW_INDEX][colum] = null;
                }
            }
        }
        // 处理非第一行：按列处理（非空元素下移）
        for (int colum = ONE_COLUM_INDEX; colum < gameInfoDo.getColumn(); colum++) {
            int currentRow = gameInfoDo.getRow() - 1;
            for (int row = gameInfoDo.getRow() - 1; row > 0; row--) {
                if (Objects.nonNull(symbolArr[row][colum])) {
                    MalakasSymbolDo temp = symbolArr[row][colum];
                    symbolArr[currentRow][colum] = temp;
                    if (!Objects.equals(currentRow, row)) {
                        symbolArr[currentRow][colum] = null;
                    }
                }
            }
        }
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
    public static void clearRewardSymbol(MalakasSymbolDo[][] symbolArr, RewardResultDo<MalakasSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        List<RewardSymbolDo<MalakasSymbolDo>> rewardSymbolList = rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<MalakasSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
        for (RewardSymbolDo<MalakasSymbolDo> rewardSymbol : rewardSymbolList) {
            if (rewardSymbol.getSymbol().isScatterSymbol()) {
                // scatter 元素， 不做消除
                // do nothing;
            } else if (rewardSymbol.getSymbol().isWildSymbol()) {
                // wild 元素
                if (rewardSymbol.getSymbol().getSymbolClearTimes() > 1) {
                    // 消除次数大于1，则不做消除
                    // do nothing;
                } else {
                    // 消除次数小于1，则移除
                    removeRewardSymbol(symbolArr, rewardSymbol);
                }
            } else {
                // 普通元素消除
                IndexDo indexDo = rewardSymbol.getIndex();
                MalakasSymbolDo commonSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumIndex()];
                if (commonSymbol.isSymbolGoldBorder()) {
                    // 带金边普通元素，转换为Wild元素（元素大小转换为消除次数）
                    MalakasSymbolDo wildSymbol = MalakasSymbolDo.builder()
                            .id(SymbolEnum.Wild.getId())
                            .name(SymbolEnum.Wild.getName())
                            .symbolType(SymbolTypeEnum.WILD)
                            .symbolRule(null)
                            .symbolSize(commonSymbol.getSymbolSize())
                            .symbolGoldBorder(false)
                            .symbolClearTimes(SymbolSizeEnum.getClearTimes(commonSymbol.getSymbolSize()))
                            .build();
                    symbolArr[indexDo.getRowIndex()][indexDo.getColumIndex()] = wildSymbol;
                } else {
                    // 不带金边普通元素，直接移除
                    removeRewardSymbol(symbolArr, rewardSymbol);
                }
            }
        }
    }

    /**
     * 移除元素
     *
     * @param symbolArr
     * @param rewardSymbol
     */
    public static void removeRewardSymbol(MalakasSymbolDo[][] symbolArr, RewardSymbolDo<MalakasSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        if (Objects.equals(indexDo.getRowIndex(), ONE_ROW_INDEX)) {
            // 第一行元素：直接移除
            symbolArr[indexDo.getRowIndex()][indexDo.getColumIndex()] = null;
        } else {
            // 非第一行元素：按照元素大小进行移除
            int columSpanSiz = rewardSymbol.getSymbol().getSymbolSize().getColumSpan();
            for (int columSpan = 0; columSpan < columSpanSiz; columSpan++) {
                symbolArr[indexDo.getRowIndex() + columSpan][indexDo.getColumIndex()] = null;
            }
        }
    }

    public static void handlePlaceholderSymbol(MalakasSymbolDo[][] symbolArr, int row, int colum, SymbolSizeEnum symbolSize) {
        switch (symbolSize) {
            case S:
                break;
            case M:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
            case L:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 2][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
            case XL:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 2][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 3][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
        }
    }

    public static int getMaxRow(MalakasSymbolDo[][] symbolArr, int currentRow, int currentColum) {
        int maxRow = currentRow;
        while (maxRow < symbolArr.length - 1 && Objects.isNull(symbolArr[maxRow][currentColum])) {
            maxRow++;
        }
        return maxRow;
    }

    public static void postPlaceholderSymbol(List<MalakasGameRecordDo> ret) {
        for (MalakasGameRecordDo record : CollectionUtils.emptyIfNull(ret)) {
            MalakasSymbolDo[][] symbolArr = record.getWheel().getSymbolArr();
            for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
                for (int colum = ONE_COLUM_INDEX; colum < symbolArr[row].length; colum++) {
                    if (Objects.equals(symbolArr[row][colum], MALAKAS_SYMBOL_PLACEHOLDER)) {
                        symbolArr[row][colum] = null;
                    }
                }
            }
        }
    }
}

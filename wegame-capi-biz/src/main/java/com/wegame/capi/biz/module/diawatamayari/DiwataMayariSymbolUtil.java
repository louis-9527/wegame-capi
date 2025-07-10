package com.wegame.capi.biz.module.diawatamayari;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class DiwataMayariSymbolUtil {

    public static DiwataMayariSymbolDo[][] copySymbolArr(DiwataMayariSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        DiwataMayariSymbolDo[][] newSymbolArr = new DiwataMayariSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = null;
                } else if (symbolArr[i][j].isPlaceholderSymbol()) {
                    newSymbolArr[i][j] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }
    public static void clearRewardSymbol(DiwataMayariSymbolDo[][] symbolArr, List<RewardSymbolDo<DiwataMayariSymbolDo>> rewardSymbolList ) {
        rewardSymbolList.parallelStream().forEach(rewardSymbol -> clearIndexRewardSymbol(symbolArr, rewardSymbol));
    }

    public static DiwataMayariSymbolDo[][] handleSymbolNullArr(DiwataMayariSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        DiwataMayariSymbolDo[][] newSymbolArr = new DiwataMayariSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
                }else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }



    /**
     * 重建元素数组：（非空元素按列下移处理）
     * @param symbolArr
     */
    public static void rebuildSymbolArr(DiwataMayariSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        // 逐列处理
        for (int col = 0; col < cols; col++) {
            // 从底部开始向上遍历，找到第一个非null元素的位置
            int writePos = rows - 1;
            // 从底部向上移动非null元素
            for (int row = rows - 1; row >= 0; row--) {
                if (symbolArr[row][col] != null) {
                    if (writePos != row) {
                        // 移动元素到新位置
                        symbolArr[writePos][col] = symbolArr[row][col];
                        symbolArr[row][col] = null;
                    }
                    writePos--;
                }
            }
        }
    }

    public static int countIndexSymbol(DiwataMayariSymbolDo[][] symbolArr, SymbolEnum symbol) {
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
    public static void postProcessPlaceholderSymbol(List<DiwataMayariGameRecordDo> gameRecordList) {
        if (CollectionUtils.isEmpty(gameRecordList)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        gameRecordList.parallelStream().forEach(gameRecord -> {
            DiwataMayariSymbolDo[][] symbolArr = gameRecord.getWheel().getSymbolArr();
            for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
                for (int column = ONE_COLUM_INDEX; column < symbolArr[row].length; column++) {
                    if (Objects.nonNull(symbolArr[row][column])) {
                        if (symbolArr[row][column].isPlaceholderSymbol()) {
                            symbolArr[row][column] = null;
                        }
                    }
                }
            }
        });
        long endTime = System.currentTimeMillis();
        log.info("postPlaceholderSymbol cost time: {} ms", (endTime - startTime));
    }

    private static void clearIndexRewardSymbol(DiwataMayariSymbolDo[][] symbolArr, RewardSymbolDo<DiwataMayariSymbolDo> rewardSymbol) {
        if (rewardSymbol.getSymbol().isScatterSymbol()) {
            // scatter 元素， 不做消除
            return;
        }
        // 普通元素+wild 消除
        IndexDo indexDo = rewardSymbol.getIndex();
        symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null;
    }

    public static void print(DiwataMayariSymbolDo[][] symbolArr, String scene) {
        System.out.println("symbolArr: " + scene);
        for (int row = 0; row < symbolArr.length; row++) {
            for (int column = 0; column < symbolArr[row].length; column++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    System.out.print(symbolArr[row][column].getId() + " ");
                } else {
                    System.out.print("null   ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}

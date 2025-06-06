package com.wegame.capi.biz.module.lamang;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.*;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class LamangSymbolUtil {


    /**
     * 是否有超级wild
     */
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

    /**
     * 处理大wild
     */
    public static void handleBigWild(LamangSymbolDo[][] symbolArr) {
        for (int row = 0; row < symbolArr.length; row++) {
            boolean hasWild = false;
            for (int col = 0; col < symbolArr[row].length; col++) {
                LamangSymbolDo current = symbolArr[row][col];
                if (Objects.nonNull(current) && current.getId().equals(LAMANG_BIG_WILD.getId())) {
                    hasWild = true;
                }
            }
            if (hasWild) {
                for (int col = 1; col < 4; col++) { // 修改第2、3、4列（索引为1、2、3）
                    LamangSymbolDo current = symbolArr[row][col];
                    //如果是bigwild，不处理，不是bigwild则替换成占位符
                    if (Objects.nonNull(current) && current.getId().equals(LAMANG_BIG_WILD.getId())) {
                        //不处理
                    } else {
                        symbolArr[row][col] = LamangSymbolDo.buildFromSymbol(LAMANG_SYMBOL_PLACEHOLDER, false);
                    }
                }
            }
        }
    }

    /**
     * 计算中奖次数 (新增scatter & scatter+1数量) 并更新元素状态
     */
    public static int countNewScatterAndScatter1InWheel(LamangSymbolDo[][] symbolArr) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                LamangSymbolDo current = symbolArr[row][col];
                if (Objects.nonNull(current) && current.isScatterSymbol() && current.isNewSymbol()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /**
     * 统计新的scatter+1
     *
     * @param symbolArr
     * @return
     */
    public static int countNewScatter1InWheel(LamangSymbolDo[][] symbolArr) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                LamangSymbolDo current = symbolArr[row][col];
                if (Objects.nonNull(current) && current.getId().equals(LAMANG_SCATTER1.getId()) && current.isNewSymbol()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public static int countScatterInWheel(LamangSymbolDo[][] symbolArr) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && symbolArr[row][col].isScatterSymbol()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public static int countScatterInColumn(LamangSymbolDo[][] symbolArr, int column) {
        int count = 0;
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isScatterSymbol()) {
                count = count + 1;
            }
        }
        return count;
    }

    public static int countGoldenBorderInRow(LamangSymbolDo[][] symbolArr, int row) {
        int count = 0;
        for (int column = ONE_COLUM_INDEX; column < symbolArr[0].length; column++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isSymbolGoldBorder()) {
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
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }

    public static void postProcessPlaceholderSymbol(List<LamangGameRecordDo> gameRecordList) {
        if (CollectionUtils.isEmpty(gameRecordList)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        gameRecordList.parallelStream().forEach(gameRecord -> {
            LamangSymbolDo[][] symbolArr = gameRecord.getWheel().getSymbolArr();
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


    public static void print(LamangSymbolDo[][] symbolArr, String scene) {
        System.out.println("symbolArr: " + scene);
        for (int row = 0; row < symbolArr.length; row++) {
            for (int column = 0; column < symbolArr[row].length; column++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    System.out.print(symbolArr[row][column].getId() + "\t");
                } else {
                    System.out.print("null\t");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static List<RewardSymbolDo<LamangSymbolDo>> getRewardSymbolDoList(RewardResultDo<LamangSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        return rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<LamangSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
    }
}

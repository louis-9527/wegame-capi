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

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
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

    public static void holdBigWild(LamangSymbolDo[][] symbolArr) {
        //todo 用占位符覆盖bigWild所在1-3列
    }

    /**
     * 计算中奖次数 (新增scatter & scatter+1数量)
     * @param symbolArr
     * @return
     */
    //todo 优化
    public static int countAndReleaseDrawTimes(LamangSymbolDo[][] symbolArr) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && symbolArr[row][col].isScatterSymbol()) {
                    count = count + 1;
                    symbolArr[row][col].setJackpotDrawTimesAdded(true);
                }
            }
        }
        return count;
    }

    /**
     * 计算
     * @param symbolArr
     * @return
     */
    //todo 优化
    public static int countAndReleaseFreeWheel(LamangSymbolDo[][] symbolArr) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && symbolArr[row][col].getId().equals(LAMANG_SCATTER1.getId())) {
                    count = count + 1;
                    symbolArr[row][col].setFreeWheelAdded(true);
                }
            }
        }
        return count;
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


}

package com.wegame.capi.biz.module.mangkukulam;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class MangkukulamSymbolUtil {


    public static MangkukulamSymbolDo[][] copySymbolArrWithPlaceHolder(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        MangkukulamSymbolDo[][] newSymbolArr = new MangkukulamSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }

    public static int countScatterInWheel(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        int scatterCount = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.nonNull(symbolArr[i][j]) && symbolArr[i][j].isScatterSymbol()) {
                    scatterCount++;
                }
            }
        }
        return scatterCount;
    }

    public static int countFireBorderInWheel(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        int scatterCount = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.nonNull(symbolArr[i][j]) && symbolArr[i][j].isSymbolFireBorder()) {
                    scatterCount++;
                }
            }
        }
        return scatterCount;
    }

    public static void postProcessPlaceholderSymbol(List<MangkukulamGameRecordDo> gameRecordList) {
        if (CollectionUtils.isEmpty(gameRecordList)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        gameRecordList.parallelStream().forEach(gameRecord -> {
            MangkukulamSymbolDo[][] symbolArr = gameRecord.getWheel().getSymbolArr();
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
}

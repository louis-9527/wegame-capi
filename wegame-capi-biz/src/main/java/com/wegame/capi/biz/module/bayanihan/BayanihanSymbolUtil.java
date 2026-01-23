package com.wegame.capi.biz.module.bayanihan;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class BayanihanSymbolUtil {

    public static SymbolDo[][] copySymbolArr(BayanihanSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        BayanihanSymbolDo[][] newSymbolArr = new BayanihanSymbolDo[row][col];
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

    public static int countScatterInWheel(BayanihanSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
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

    public static int countScatterInColumn(BayanihanSymbolDo[][] symbolArr, int column) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isScatterSymbol()) {
                count = count + 1;
            }
        }
        return count;
    }


}

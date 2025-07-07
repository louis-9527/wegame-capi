package com.wegame.capi.biz.module.mangkukulam;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;

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
}

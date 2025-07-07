package com.wegame.capi.biz.module.mangkukulam;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;

@Slf4j
public class MangkukulamSymbolUtil {
    /**
     * 计算中奖次数 (新增scatter & scatter+1数量) 并更新元素状态
     */
    public static int countNewScatterAndScatter1InWheel(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                MangkukulamSymbolDo current = symbolArr[row][col];
                if (Objects.nonNull(current) && current.isScatterSymbol() && current.isNewSymbol()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }


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
                    newSymbolArr[i][j] = symbolArr[i][j].copyWithOriginalNewSymbol();
                }
            }
        }
        return newSymbolArr;
    }
}

package com.wegame.capi.biz.module.sirena;

import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.SirenaGameRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.module.sirena.game.SirenaGameContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;


@Slf4j
public class SirenaSymbolUtil {
    public static void print(SirenaSymbolDo[][] symbolArr) {
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

    public static void postProcessPlaceholderSymbol(List<SirenaGameRecordDo> gameRecordList) {
        if (CollectionUtils.isEmpty(gameRecordList)) {
            return;
        }
        gameRecordList.parallelStream().forEach(gameRecord -> {
            SirenaSymbolDo[][] symbolArr = gameRecord.getWheel().getSymbolArr();
            for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
                for (int column = ONE_COLUM_INDEX; column < symbolArr[row].length; column++) {
                    if (Objects.nonNull(symbolArr[row][column])) {
                        if (symbolArr[row][column].isPlaceholderSymbol()) {
                            symbolArr[row][column] = symbolArr[row][column].getNestSymbol();
                        }
                        if(SirenaGameHelper.SIRENA_Big_Pearl_Wild.getId().equals(symbolArr[row][column].getId())){
                            SirenaSymbolDo temp = symbolArr[row][column];
                            SirenaSymbolDo nestSymbolTemp = symbolArr[row][column].getNestSymbol();
                            nestSymbolTemp.setNestSymbol(temp);
                            symbolArr[row][column] = nestSymbolTemp;

                        }
                    }
                }
            }
        });
    }
}

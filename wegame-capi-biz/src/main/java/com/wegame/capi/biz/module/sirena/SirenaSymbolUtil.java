package com.wegame.capi.biz.module.sirena;

import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.domain.biz.game.SirenaGameRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
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
                            log.info("SirenaSymbolUtil#postProcessPlaceholderSymbol,temp:{}", JSON.toJSONString(temp));
                            SirenaSymbolDo nestSymbolTemp = temp.getNestSymbol().copy();
                            nestSymbolTemp.setNestSymbol(temp);
                            temp.setNestSymbol(null);
                            symbolArr[row][column] = nestSymbolTemp;
                        }
                    }
                }
            }
        });
    }

    public static void handleWild(SirenaSymbolDo[][] symbolArr) {
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            for (int column = ONE_COLUM_INDEX; column < symbolArr[row].length; column++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    SirenaSymbolDo current = symbolArr[row][column];
                    if (Objects.nonNull(current.getNestSymbol()) && SirenaGameHelper.SIRENA_Big_Pearl_Wild.getId().equals(current.getNestSymbol().getId())) {
                        SirenaSymbolDo outer = current.getNestSymbol().copy();
                        SirenaSymbolDo inner = current.copy();
                        inner.setNestSymbol(null);
                        outer.setNestSymbol(inner);
                        symbolArr[row][column] = outer;
                        setSymbolPlaceholder(row, column + 1, symbolArr);
                        setSymbolPlaceholder(row+1, column, symbolArr);
                        setSymbolPlaceholder(row+1, column+1, symbolArr);
                    }
                }
            }
        }
    }

    private static void setSymbolPlaceholder(int row,int col,SirenaSymbolDo[][] symbolArr){
        SirenaSymbolDo temp = symbolArr[row][col];
        SirenaSymbolDo symbolPlaceholder = SirenaGameHelper.SIRENA_SYMBOL_PLACEHOLDER;
        symbolPlaceholder.setNestSymbol(temp);
        symbolArr[row][col] = symbolPlaceholder;
    }
}

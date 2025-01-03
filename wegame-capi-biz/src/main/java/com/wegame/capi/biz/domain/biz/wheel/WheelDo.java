package com.wegame.capi.biz.domain.biz.wheel;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


/**
 * 滚轮
 */
@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WheelDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 滚轮序号
     */
    private Integer index;
    /**
     * 滚轮元素二维数组
     */
    private SymbolDo[][] symbolArr;

    public WheelDo copy() {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        SymbolDo[][] newSymbolArr = new SymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newSymbolArr[i][j] = symbolArr[i][j];
            }
        }
        return new WheelDo(index, newSymbolArr);
    }

    public void print() {
        log.info("index:{}", index, symbolArr);
        log.info("symbolArr: ");
        for (int i = 0; i < symbolArr.length; i++) {
            for (int j = 0; j < symbolArr[i].length; j++) {
                System.out.print(symbolArr[i][j].getId() + " ");
            }
            System.out.println();
        }
    }

}


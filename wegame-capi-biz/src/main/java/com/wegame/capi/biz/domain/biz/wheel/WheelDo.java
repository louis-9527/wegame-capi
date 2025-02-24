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
public class WheelDo<T extends SymbolDo> implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 序号
     */
    private Integer index;
    /**
     * 免费轮标识
     */
    private boolean freeFlag;
    /**
     * 滚轮-元素二维数组
     */
    private T[][] symbolArr;

    public void valid() {

    //    public void print() {
    //        System.out.println("index: " + index);
    //        System.out.println("parentIndex: " + parentIndex);
    //        System.out.println("symbolArr: ");
    //        for (int row = 0; row < symbolArr.length; row++) {
    //            for (int column = 0; column < symbolArr[row].length; column++) {
    //                if (Objects.nonNull(symbolArr[row][column])) {
    //                    System.out.print(symbolArr[row][column].getId() + " ");
    //                } else {
    //                    System.out.print("null ");
    //                }
    //            }
    //            System.out.println();
    //        }
    //        System.out.println();
    //    }
}


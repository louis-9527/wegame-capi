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
     * id
     */
    private Long id;
    /**
     * parentId
     */
    private Long parentId;
    /**
     * 滚轮-元素二维数组
     */
    private T[][] symbolArr;

    //    public void print() {
    //        System.out.println("index: " + index);
    //        System.out.println("parentIndex: " + parentIndex);
    //        System.out.println("symbolArr: ");
    //        for (int row = 0; row < symbolArr.length; row++) {
    //            for (int colum = 0; colum < symbolArr[row].length; colum++) {
    //                if (Objects.nonNull(symbolArr[row][colum])) {
    //                    System.out.print(symbolArr[row][colum].getId() + " ");
    //                } else {
    //                    System.out.print("null ");
    //                }
    //            }
    //            System.out.println();
    //        }
    //        System.out.println();
    //    }
}


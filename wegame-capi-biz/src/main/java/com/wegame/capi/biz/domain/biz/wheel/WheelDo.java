package com.wegame.capi.biz.domain.biz.wheel;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


/**
 * 滚轮
 */
@Slf4j
@Data
@ToString
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

    public void print() {
        log.info("index:{}", index);
        log.info("symbolArr: ");
        for (int i = 0; i < symbolArr.length; i++) {
            for (int j = 0; j < symbolArr[i].length; j++) {
                System.out.print(symbolArr[i][j].getId() + " ");
            }
            System.out.println();
        }
    }

}


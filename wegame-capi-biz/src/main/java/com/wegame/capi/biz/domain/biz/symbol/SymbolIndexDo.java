package com.wegame.capi.biz.domain.biz.symbol;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.checkerframework.checker.units.qual.N;

import java.io.Serializable;

/**
 * 元素坐标
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class SymbolIndexDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 行号
     */
    private int rowIndex;
    /**
     * 列号
     */
    private int columIndex;
}


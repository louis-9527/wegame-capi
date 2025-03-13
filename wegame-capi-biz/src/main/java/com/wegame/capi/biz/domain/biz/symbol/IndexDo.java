package com.wegame.capi.biz.domain.biz.symbol;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 元素坐标
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IndexDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 行号
     */
    private int rowIndex;
    /**
     * 列号
     */
    private int columnIndex;
}


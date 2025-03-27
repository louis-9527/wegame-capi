package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 中奖元素
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardSymbolDo<T extends SymbolDo> implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 元素
     */
    private T symbol;
    /**
     * 坐标
     */
    private IndexDo index;
}


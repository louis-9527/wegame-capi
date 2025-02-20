package com.wegame.capi.biz.domain.biz.symbol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ibong游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class IbongSymbolDo extends SymbolDo{
    private static final long serialVersionUID = -226678093710848L;

    /**
     * 分裂次数
     */
    private Integer breakUpCount;
}

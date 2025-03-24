package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.common.enums.SymbolBreakUpEnum;
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
//@JsonFormat(shape = JsonFormat.Shape.STRING)
public class IbongAdarnaSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -226678093710848L;

    /**
     * 分裂
     */
    private SymbolBreakUpEnum breakUp;

    public static IbongAdarnaSymbolDo buildIbongSymbolDo(SymbolDo symbolDo, SymbolBreakUpEnum breakUp) {
        return IbongAdarnaSymbolDo.builder()
                .id(symbolDo.getId())
                .name(symbolDo.getName())
                .symbolType(symbolDo.getSymbolType())
                .breakUp(breakUp)
                .build();
    }
}

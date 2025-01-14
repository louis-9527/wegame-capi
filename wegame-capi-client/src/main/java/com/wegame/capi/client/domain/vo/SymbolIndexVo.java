package com.wegame.capi.client.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SymbolIndexVo {
    /**
     * 行号
     */
    private int rowIndex;
    /**
     * 列号
     */
    private int columIndex;
}

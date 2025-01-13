package com.wegame.capi.biz.module.reward.algorithm;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SymbolMultiTreeNode {

    private String val;
    /**
     * 元素
     */
    private SymbolDo symbol;
    /**
     * 元素坐标
     */
    private SymbolIndexDo symbolIndex;
    /**
     * 子节点列表
     */
    private List<SymbolMultiTreeNode> children;

    @Override
    public String toString() {
        return symbolIndex.getRowIndex() + "" + symbolIndex.getColumIndex();
    }
}

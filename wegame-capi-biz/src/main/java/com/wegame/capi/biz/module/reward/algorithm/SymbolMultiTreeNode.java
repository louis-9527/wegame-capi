package com.wegame.capi.biz.module.reward.algorithm;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


    public void addChildren(SymbolMultiTreeNode node) {
        if (Objects.isNull(node)) {
            children = new ArrayList<>();
        }
        children.add(node);
    }

    @Override
    public String toString() {
        return symbolIndex.getRowIndex() + "" + symbolIndex.getColumIndex();
    }
}

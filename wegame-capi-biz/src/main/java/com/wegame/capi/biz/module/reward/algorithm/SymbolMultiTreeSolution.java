package com.wegame.capi.biz.module.reward.algorithm;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SymbolMultiTreeSolution {

    public static List<List<SymbolMultiTreeNode>> findTreePathList(SymbolMultiTreeNode root) {
        List<List<SymbolMultiTreeNode>> pathList = new ArrayList<>();
        if (Objects.isNull(root)) {
            return pathList;
        }
        // 深度搜索算法
        dfs(root, new ArrayList<>(), pathList);
        // 返回
        return pathList;
    }

    private static void dfs(SymbolMultiTreeNode node, List<SymbolMultiTreeNode> currentPath, List<List<SymbolMultiTreeNode>> pathList) {
        if (node == null) {
            return;
        }
        currentPath.add(node);
        if (CollectionUtils.isEmpty(node.getChildren())) {
            // 叶子节点，将当前路径添加到结果列表中
            pathList.add(new ArrayList<>(currentPath));
        } else {
            // 非叶子节点，递归深度处理
            for (SymbolMultiTreeNode child : node.getChildren()) {
                // 递归遍历所有子节点
                if (Objects.nonNull(child)) {
                    dfs(child, currentPath, pathList);
                }
            }
        }
        // Backtrack: remove the current node's value from the current path
        if (Objects.nonNull(currentPath) && currentPath.size() > 1) {
            currentPath.remove(currentPath.size() - 1);
        }
    }
}

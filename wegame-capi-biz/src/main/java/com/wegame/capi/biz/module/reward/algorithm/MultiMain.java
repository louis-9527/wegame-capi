//package com.wegame.capi.biz.module.reward.algorithm;
//
//import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
//import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexFactory;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class MultiMain {
//
//    public static void main(String[] args) {
//        SymbolDo symbolDo = new SymbolDo();
//
//        SymbolMultiTreeNode root10 = new SymbolMultiTreeNode();
//        root10.setSymbol(symbolDo);
//        root10.setSymbolIndex(SymbolIndexFactory.build(1, 0));
//        root10.setVal("10");
//
//        SymbolMultiTreeNode root01 = new SymbolMultiTreeNode();
//        root01.setSymbol(symbolDo);
//        root01.setSymbolIndex(SymbolIndexFactory.build(0, 1));
//        root01.setVal("01");
//        SymbolMultiTreeNode root21 = new SymbolMultiTreeNode();
//        root21.setSymbol(symbolDo);
//        root21.setSymbolIndex(SymbolIndexFactory.build(2, 1));
//        root21.setVal("21");
//        SymbolMultiTreeNode root12 = new SymbolMultiTreeNode();
//        root12.setSymbol(symbolDo);
//        root12.setSymbolIndex(SymbolIndexFactory.build(1, 2));
//        root12.setVal("12");
//        SymbolMultiTreeNode root03 = new SymbolMultiTreeNode();
//        root03.setSymbol(symbolDo);
//        root03.setSymbolIndex(SymbolIndexFactory.build(0, 3));
//        root03.setVal("03");
//        SymbolMultiTreeNode root13 = new SymbolMultiTreeNode();
//        root13.setSymbol(symbolDo);
//        root13.setSymbolIndex(SymbolIndexFactory.build(1, 3));
//        root13.setVal("13");
//        SymbolMultiTreeNode root24 = new SymbolMultiTreeNode();
//        root24.setSymbol(symbolDo);
//        root24.setSymbolIndex(SymbolIndexFactory.build(2, 4));
//        root24.setVal("24");
//
//
//        root10.setChildren(Arrays.asList(root01, root21));
//        root01.setChildren(Arrays.asList(root12));
//        root21.setChildren(Arrays.asList(root12));
//        root12.setChildren(Arrays.asList(root03, root13));
//        root13.setChildren(Arrays.asList(root24));
//
//        List<List<SymbolMultiTreeNode>> paths = SymbolMultiTreeSolution.treePaths(root10);
//        for (List<SymbolMultiTreeNode> path : paths) {
//            for (SymbolMultiTreeNode node : path) {
//                System.out.print(node.getVal() + " -> ");
//            }
//            System.out.println();
//        }
//    }
//}

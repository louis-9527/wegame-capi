//package com.wegame.capi.biz.service;
//
//import cn.hutool.core.util.RandomUtil;
//import com.alibaba.fastjson.JSON;
//import com.wegame.capi.biz.domain.SymbolDo;
//import com.wegame.capi.biz.module.probabilistic.ProbabilisticContext;
//import com.wegame.capi.common.enums.SymbolTypeEnum;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
///**
// * 游戏玩法服务
// */
//@Deprecated
//@Service
//public class GamePlayService {
//    @Autowired
//    private ProbabilisticContext probabilisticContext;
//
//    /**
//     * 获取游戏结果轮盘
//     *
//     * @return
//     */
//    public Integer[][] getSysBol(String gameId) {
//        Integer[][] array = new Integer[3][5];
//        Random random = new Random();
//
//        for (int i = 0; i < array.length; i++) {
//            for (int j = 0; j < array[i].length; j++) {
//                array[i][j] = random.nextInt(100); // 生成0到99之间的随机数
//            }
//        }
//
////        // 使用for循环向数组中添加元素
////        for (int i = 0; i < array.length; i++) {
////            for (int j = 0; j < array[i].length; j++) {
////                SymbolDo symbolDo = probabilisticContext.randomOneSymbol(gameId, i);
////                array[i][j] = symbolDo; // 这里填充数组元素的值
////            }
////        }
//        return array;
//    }
//
//    public List<Integer[][]> getSysBolExcludeScatter(String gameId, Integer size) {
//        List<Integer[][]> resultList = new ArrayList<>(size);
//        for (int m = 0; m < size; m++) {
//            Random random = new Random();
//            Integer[][] array = new Integer[3][5];
//            for (int i = 0; i < array.length; i++) {
//                for (int j = 0; j < array[i].length; j++) {
//                    array[i][j] = random.nextInt(100); // 生成0到99之间的随机数
//                }
//            }
//            resultList.add(array);
//        }
//
//
////        //记录生成的wild的坐标集合
////        Map<String, Integer> wildMap = new HashMap<>();
////        for (int i = 0; i < size; i++) {
////            Integer[][] array = new Integer[3][5];
////            // 使用for循环向数组中添加元素
////            for (int i = 0; i < array.length; i++) {
////                for (int j = 0; j < array[i].length; j++) {
////                    //如果当前坐标是wild,则不计算直接使用wild
////                    Integer sysBolId = wildMap.get(i + "_" + j);
////                    if (sysBolId != null) {
////                        array[i][j] = sysBolId;
////                        continue;
////                    }
////                    SymbolDo symbolDo = null;
////                    do {
////                        symbolDo = probabilisticService.randomOneSymbol(gameId, i);
////                    } while (!symbolDo.getId().equals(SymbolTypeEnum.Scatter.getId()));
////                    array[i][j] = symbolDo.getId(); // 这里填充数组元素的值
////                    if (SymbolTypeEnum.wild(symbolDo.getId())) {
////                        wildMap.put(i + "_" + j, symbolDo.getId());
////                    }
////                }
////            }
////        }
//
//
//        return resultList;
//    }
//
//    /**
//     * 获取游戏结果轮盘
//     * 包含 Scatter
//     *
//     * @return
//     */
//    public Integer[][] getSysBolScatter(String gameId) {
//
//        Integer[][] array = new Integer[3][5];
//        Random random = new Random();
//
//        for (int i = 0; i < array.length; i++) {
//            for (int j = 0; j < array[i].length; j++) {
//                array[i][j] = random.nextInt(100); // 生成0到99之间的随机数
//            }
//        }
//        return array;
////        SymbolDo[][] array = new SymbolDo[3][5];
////        // 初始化检查列是否包含特殊元素的标志
////        boolean[] containsSpecial = new boolean[3];
////
////        // 使用一次循环向数组中添加元素并检查1, 3, 5列是否包含特殊元素
////        for (int i = 0; i < array.length; i++) {
////            for (int j = 0; j < array[i].length; j++) {
////                SymbolDo symbolDo = probabilisticContext.randomOneSymbol(gameId, i);
////                array[i][j] = symbolDo;
////
////                // 检查1, 3, 5列是否包含特殊元素
////                if ((j == 0 || j == 2 || j == 4) && symbolDo.isSpecial()) {
////                    containsSpecial[j / 2] = true;
////                }
////            }
////        }
////
////        // 替换不包含特殊元素的列中的一个元素为特殊元素
////        SymbolDo specialSymbol = new SymbolDo("Special");
////        for (int col = 0; col < containsSpecial.length; col++) {
////            if (!containsSpecial[col]) {
////                int randomRow = (int) (Math.random() * array.length);
////                array[randomRow][col * 2] = specialSymbol;
////            }
////        }
////        // 使用for循环向数组中添加元素
////        for (int i = 0; i < array.length; i++) {
////            for (int j = 0; j < array[i].length; j++) {
////                SymbolDo symbolDo = probabilisticContext.randomOneSymbol(gameId, i);
////                array[i][j] = symbolDo; // 这里填充数组元素的值
////            }
////        }
////        // 检查并替换1, 3, 5列
////        int[] columnsToCheck = {0, 2, 4};
////        SymbolDo specialSymbol = new SymbolDo("Special"); //
////        for (int col : columnsToCheck) {
////            boolean containsSpecial = false;
////
////            // 检查列中是否包含特殊元素
////            for (int row = 0; row < array.length; row++) {
////                if (array[row][col].isSpecial()) {
////                    containsSpecial = true;
////                    break;
////                }
////            }
////
////            // 如果不包含特殊元素，则随机替换一行的元素为特殊元素
////            if (!containsSpecial) {
////                int randomRow = (int) (Math.random() * array.length);
////                array[randomRow][col] = specialSymbol;
////            }
////        }
////
////
////        return array;
//    }
//
//
//}

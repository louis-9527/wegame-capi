package com.wegame.capi.test.biz.module.sirena.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WheelChartConvertorTest {

    private String SOURCE;
    /**
     * key:产品元素id:L
     * value:程序id:1001
     */
    private final Map<String, String> SOURCEID_APPLICATIONID_MAP = sourceIdAppIdMap();

    @Before
    public void before() {
        SOURCE = "L2\tL2\tL2\tL3\tL1\n" +
                "H4\tL5\tL3\tH4\tL2\n" +
                "H2\tL2\tL1\tL4\tL2\n" +
                "L5\tL3\tL5\tL2\tL2\n" +
                "L1\tL1\tL3\tL5\tL2\n" +
                "L5\tH4\tW\tH1\tL3\n" +
                "H3\tH4\tL5\tL3\tH4\n" +
                "L1\tH3\tH4\tL5\tL2\n" +
                "L3\tL2\tH1\tH4\tL1\n" +
                "L2\tH2\tH3\tH4\tL1\n" +
                "L1\tH3\tL2\tL5\tL3\n" +
                "L3\tH4\tH4\tL2\tL3\n" +
                "H2\tH4\tL1\tL5\tH2\n" +
                "H3\tH4\tH1\tL2\tL5\n" +
                "L2\tH2\tL1\tH4\tL5\n" +
                "H1\tL4\tH2\tH1\tH2\n" +
                "L3\tH1\tH2\tS\tL5\n" +
                "L2\tH4\tH3\tL5\tL2\n" +
                "L2\tH4\tH1\tL5\tH2\n" +
                "L2\tL3\tL2\tL3\tL1\n" +
                "H2\tH4\tL3\tH4\tH3\n" +
                "H4\tS\tH3\tL4\tL1\n" +
                "H3\tH3\tL1\tL4\tH1\n" +
                "H4\tL2\tS\tL3\tL3";
    }

    @Test
    public void convert() {
        String[][] sourceToArray = this.convertTo2DArray(SOURCE);
        this.print(sourceToArray);

        // 转成轮带模式List<List<String>>
        List<List<String>> wheelChartList = this.arrayConverToWheelChartList(sourceToArray);

        this.print(wheelChartList);
    }

    private List<List<String>> arrayConverToWheelChartList(String[][] sourceToArray) {
        List<List<String>> wheelChartList = Lists.newArrayList();

        for (int colum = 0; colum < sourceToArray[0].length; colum++) {
            List<String> reelChartList = Lists.newArrayList();
            for (int row = 0; row < sourceToArray.length; row++) {
                try {
                    String appSymbolId = SOURCEID_APPLICATIONID_MAP.get(sourceToArray[row][colum].toUpperCase());
                    if (StrUtil.isEmpty(appSymbolId)) {
                        throw new IllegalArgumentException("not support symbol of " + sourceToArray[row][colum]);
                    }
                    if (StrUtil.isNotEmpty(appSymbolId)) {
                        reelChartList.add(appSymbolId);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // 防止数组越界
                }

            }
            wheelChartList.add(reelChartList);
        }

        return wheelChartList;
    }

    private void print(List<List<String>> wheelChartList) {
        System.out.println("//////////////// 轮带转换结果 ////////////////");
        System.out.println("[");
        for (int i = 0; i < wheelChartList.size(); i++) {
            if (i < wheelChartList.size() - 1) {
                System.out.println(JSON.toJSONString(wheelChartList.get(i), SerializerFeature.WriteNullStringAsEmpty) + ",");
            } else {
                System.out.println(JSON.toJSONString(wheelChartList.get(i)));
            }
        }
        System.out.println("]");
    }

    private void print(String[][] array) {
        System.out.println("转成而为数组：");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private String[][] convertTo2DArray(String source) {
        // 按行分割
        String[] rows = source.split("\n");

        // 创建二维数组
        String[][] result = new String[rows.length][];

        // 遍历每一行，按制表符分割
        for (int i = 0; i < rows.length; i++) {
            result[i] = rows[i].split("\t");
        }
        return result;
    }

    private static Map<String, String> sourceIdAppIdMap() {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("L1", "1001");
        resultMap.put("L2", "1002");
        resultMap.put("L3", "1003");
        resultMap.put("L4", "1004");
        resultMap.put("L5", "1005");
        resultMap.put("H1", "1006");
        resultMap.put("H2", "1007");
        resultMap.put("H3", "1008");
        resultMap.put("H4", "1009");
        // wild
        resultMap.put("W", "4001");
        // scatter
        resultMap.put("S", "3001");
        // scatter1
//        resultMap.put("S1", "3002");
        return resultMap;
    }

}

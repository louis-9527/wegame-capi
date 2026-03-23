package com.wegame.capi.api.domain.req.redirect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean展平工具
 * 核心：仅展平指定的一层Map字段，内层嵌套保留，步骤清晰易读
 */
public class BeanFlatReadableUtil {

    /**
     * 仅展平Bean中指定的单个Map字段（仅展平一层，内层嵌套保留）
     *
     * @param bean           待处理的Bean对象（如PromotionForwardReq）
     * @param nestedMapField 要展平的Map字段名（如"data"）
     * @param <T>            Bean的泛型类型
     * @return 展平后的扁平Map（仅展平指定层，内层嵌套保留）
     */
    public static <T> Map<String, Object> flatBeanOneLayerReadable(T bean, String nestedMapField) {
        // 1. 入参空值防护：如果Bean为null，直接返回空Map，避免后续NPE
        if (bean == null) {
            return new HashMap<>();
        }

        // 2. 第一步：将Bean转换为原始Map（保留所有字段，包含嵌套的Map字段）
        // 参数说明：
        // - 第2个参数：指定存储结果的Map类型（HashMap）
        // - 第3个参数：false=保留null值（如需忽略可改为true）
        // - 第4个参数：true=使用字段名作为key（而非getter方法名）
        Map<String, Object> originalBeanMap = BeanUtil.beanToMap(bean, new HashMap<>(), false, true);

        // 3. 第二步：提取并移除要展平的嵌套Map字段（仅操作指定层）
        // 从原始Map中取出目标嵌套Map，并移除该key（避免后续重复）
        Object nestedMapObj = originalBeanMap.remove(nestedMapField);
        // 类型校验：确保取出的字段是Map类型（避免类型转换异常）
        Map<String, Object> targetNestedMap = null;
        if (nestedMapObj instanceof Map) {
            targetNestedMap = (Map<String, Object>) nestedMapObj;
        }

        // 4. 第三步：将目标嵌套Map的键值对合并到外层（仅展平这一层，内层嵌套保留）
        // 空值防护：仅当嵌套Map非空时才合并，避免空指针
        if (MapUtil.isNotEmpty(targetNestedMap)) {
            originalBeanMap.putAll(targetNestedMap);
        }

        // 5. 可选：移除Map中的null值（让结果更干净，如需保留null可注释此行）
        MapUtil.removeNullValue(originalBeanMap);

        // 6. 返回最终的扁平Map（仅展平指定层，内层嵌套完整保留）
        return originalBeanMap;
    }

    // 测试方法（高可读性，步骤清晰）
    public static void main(String[] args) {
        // ========== 1. 构造多层嵌套的测试对象 ==========
        // 内层嵌套Map（要保留，不展平）
        Map<String, Object> innerNestedMap = new HashMap<>();
        innerNestedMap.put("subKey1", "subValue1");
        innerNestedMap.put("subKey2", 12345);

        // 要展平的data层Map（仅展平这一层）
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("gameId", "G7890");
        dataMap.put("innerNestedMap", innerNestedMap); // 内层嵌套保留
        dataMap.put("timestamp", 1711888888888L);

        // 构造核心Bean对象
        PromotionForwardReq req = new PromotionForwardReq();
        req.setPId("P123456");
        req.setPlayerName("test_player_001");
        req.setData(dataMap);

        // ========== 2. 调用高可读性的展平方法 ==========
        Map<String, Object> flatMap = flatBeanOneLayerReadable(req, "data");

        // ========== 3. 格式化打印结果（验证仅展平一层） ==========
        System.out.println("===== 仅展平一层的结果（高可读性版） =====");
        // Hutool格式化输出，缩进+换行，更易读
    }
}
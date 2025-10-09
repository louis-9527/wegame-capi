package com.wegame.capi.biz.domain.metrics;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.client.enums.innerdos.MetricsPropertyTypeEnum;
import com.wegame.capi.client.enums.innerdos.UserPlayMetricsPropertyNameEnum;
import com.wegame.capi.common.Constants;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户玩法指标
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserPlayMetricsDo implements Serializable {
    private static final long serialVersionUID = 323434657670673L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 平台id
     */
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 游戏id
     */
    private String gameId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 累计游戏局数
     */
    private Long totalGameCount;
    /**
     * 累计投注金额.单位：1*1000
     */
    private Long totalBetAmount;
    /**
     * 累计获奖金额.单位：1*1000
     */
    private Long totalPayoutAmount;
    /**
     * 连续空盘次数.中奖后清零
     */
    private Integer continuousLostCount;
    /**
     * 最后投注时间.按频率刷新
     */
    private Long lastBetTime;
    /**
     * 连续不中免费轮次数.中后清零
     */
    private Long continuousNoFreeSpinCount;
    /**
     * 机制列表
     */
    private List<SpecialRuleDo> specialRuleList;
    /**
     * 扩展信息,用于扩展配置
     */
    private Map<String, Object> extend = new HashMap<>();
    /**
     * 版本号
     */
    private Long version;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 上次刷盘时间:暂时不写值
     */
    private Long lastFlushTime;

    /**
     * 特殊规则
     */
    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"ruleId", "ruleName"}, callSuper = false)
    public static class SpecialRuleDo implements Serializable {
        private static final long serialVersionUID = 350098734224370673L;
        /**
         * 规则id:冗余一个id
         */
        private String ruleId;
        /**
         * 规则名称
         */
        private String ruleName;
        /**
         * 连续不中特殊规则次数
         */
        private Integer continuousNoCount;
        /**
         * 扩展参数
         */
        private Map<String, Object> extendInfo;


    }

    /**
     * 查找一个指定特殊规则
     *
     * @param ruleId   ruleId
     * @param ruleName ruleName
     * @return do
     */
    public SpecialRuleDo querySpecialRule(String ruleId, String ruleName) {
        if (CollectionUtil.isEmpty(this.specialRuleList)) {
            return null;
        }
        return this.specialRuleList.stream().filter(specialRuleDo -> Objects.equals(specialRuleDo.getRuleId(), ruleId) && Objects.equals(specialRuleDo.getRuleName(), ruleName))
                .findFirst().orElse(null);
    }

    /**
     * 添加增量
     *
     * @param metricsDataMap 单次采集数据
     */
    public void injectNewData(Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap) {
        // 添加固定数据
        this.injectNewFixedData(metricsDataMap);
        // 添加扩展数据
        this.injectNewExtendData(metricsDataMap);
        // 跟新基础数据
        this.updateBaseData();
    }

    private void updateBaseData() {
        // 版本号
        this.version = Optional.ofNullable(this.version).map(v -> v + 1).orElse(1L);
        // 更新人
        this.updater = Constants.SYSTEM;
        // 更新时间
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 注入固定属性增量数据
     *
     * @param metricsDataMap 采集map
     */
    private void injectNewFixedData(Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap) {
        // 累计游戏局数
        Object totalGameCountObj = metricsDataMap.get(UserPlayMetricsPropertyNameEnum.totalGameCount);
        this.totalGameCount = UserPlayMetricsPropertyNameEnum.totalGameCount.calc(this.totalGameCount, totalGameCountObj, Long.class);

        // 累计投注金额
        Object totalBetAmountObj = metricsDataMap.get(UserPlayMetricsPropertyNameEnum.totalBetAmount);
        this.totalBetAmount = UserPlayMetricsPropertyNameEnum.totalBetAmount.calc(this.totalBetAmount, totalBetAmountObj, Long.class);

        // 累计获奖金额
        Object totalPayoutAmountObj = metricsDataMap.get(UserPlayMetricsPropertyNameEnum.totalPayoutAmount);
        this.totalPayoutAmount = UserPlayMetricsPropertyNameEnum.totalPayoutAmount.calc(this.totalPayoutAmount, totalPayoutAmountObj, Long.class);

        // 连续空盘次数
        Object continuousLostCountObj = metricsDataMap.get(UserPlayMetricsPropertyNameEnum.continuousLostCount);
        this.continuousLostCount = UserPlayMetricsPropertyNameEnum.continuousLostCount.calc(this.continuousLostCount, continuousLostCountObj, Integer.class);

        // 最后投注时间.获取最新时间作为最后投注时间
        Object lastBetTimeObj = metricsDataMap.get(UserPlayMetricsPropertyNameEnum.lastBetTime);
        this.lastBetTime = UserPlayMetricsPropertyNameEnum.lastBetTime.calc(this.lastBetTime, lastBetTimeObj, Long.class);

        // 连续不中免费轮次数
        Object continuousNoFreeSpinCountObj = metricsDataMap.get(UserPlayMetricsPropertyNameEnum.continuousNoFreeSpinCount);
        this.continuousNoFreeSpinCount = UserPlayMetricsPropertyNameEnum.continuousNoFreeSpinCount.calc(this.continuousNoFreeSpinCount, continuousNoFreeSpinCountObj, Long.class);

        // 机制列表
        this.injectNewFixedSpecialRule(metricsDataMap);
    }

    /**
     * 注入新的特殊规则 数据
     *
     * @param metricsDataMap 数据
     */
    private void injectNewFixedSpecialRule(Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap) {
        Object specialRuleListObj = metricsDataMap.get(UserPlayMetricsPropertyNameEnum.specialRuleList);
        if (Objects.nonNull(specialRuleListObj)) {
            List<SpecialRuleDo> sourceSpecialRuleList = (List<SpecialRuleDo>) specialRuleListObj;
            if (CollectionUtil.isEmpty(this.specialRuleList)) {
                // 当前模型为空，直接替换
                this.specialRuleList = sourceSpecialRuleList;
            } else {
                if (CollectionUtil.isNotEmpty(sourceSpecialRuleList)) {
                    // 找出当前没有的规则
                    List<SpecialRuleDo> newRuleList = sourceSpecialRuleList.stream().filter(item -> !this.specialRuleList.contains(item)).collect(Collectors.toList());
                    // 存量规则匹配
                    Map<SpecialRuleDo, SpecialRuleDo> newSpecialRuleMap = sourceSpecialRuleList.stream().collect(Collectors.toMap(s -> s, s -> s, (s1, s2) -> s1));
                    for (SpecialRuleDo specialRuleDo : this.specialRuleList) {
                        SpecialRuleDo sourceSpecialRuleDo = newSpecialRuleMap.get(specialRuleDo);
                        // 新增里面没有原有的规则
                        if (Objects.isNull(sourceSpecialRuleDo)) {
                            continue;
                        }
                        // 特殊规则固定属性.连续不中当前规则次数添加.如果新增里面没有数据，原有数据清零
                        if (Objects.isNull(sourceSpecialRuleDo.getContinuousNoCount()) || sourceSpecialRuleDo.getContinuousNoCount() == 0) {
                            specialRuleDo.setContinuousNoCount(0);
                        } else {
                            specialRuleDo.setContinuousNoCount(specialRuleDo.getContinuousNoCount() + sourceSpecialRuleDo.getContinuousNoCount());
                        }

                        // exitInfo 当前没有数据，后续有数据需要靠约定属性进行处理
                    }
                    // 将新规则加入进来
                    this.specialRuleList.addAll(newRuleList);
                }
            }
        }
    }

    /**
     * 注入新的扩展数据
     *
     * @param metricsDataMap 数据map
     */
    private void injectNewExtendData(Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap) {
        this.extend = Optional.ofNullable(this.extend).orElse(new HashMap<>());
        for (Map.Entry<UserPlayMetricsPropertyNameEnum, Object> entry : metricsDataMap.entrySet()) {
            // 只处理扩展类型参数
            if (!Objects.equals(entry.getKey().getType(), MetricsPropertyTypeEnum.EXTEND)) {
                continue;
            }
            // 计算值
            Object newExtendValue = entry.getKey().calc(this.extend.get(entry.getKey().getPropertyName()), entry.getValue(), Object.class);
            this.extend.put(entry.getKey().getPropertyName(), newExtendValue);
        }
    }

}

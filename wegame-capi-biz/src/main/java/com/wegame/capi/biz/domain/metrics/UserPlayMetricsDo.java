package com.wegame.capi.biz.domain.metrics;

import cn.hutool.core.collection.CollectionUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private Map<String, Object> extend;
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
     * @param newDataDo 新增数据
     */
    public void injectNewData(UserPlayMetricsDo newDataDo) {
        this.totalGameCount = this.totalGameCount + Optional.ofNullable(newDataDo.getTotalGameCount()).orElse(0L);
        this.totalBetAmount = this.totalBetAmount + Optional.ofNullable(newDataDo.getTotalBetAmount()).orElse(0L);
        this.totalPayoutAmount = this.totalPayoutAmount + Optional.ofNullable(newDataDo.getTotalPayoutAmount()).orElse(0L);
        this.continuousLostCount = this.continuousLostCount + Optional.ofNullable(newDataDo.getContinuousLostCount()).orElse(0);
        this.lastBetTime = Math.max(this.lastBetTime, Optional.ofNullable(newDataDo.getLastBetTime()).orElse(System.currentTimeMillis()));
        this.continuousNoFreeSpinCount = this.continuousNoFreeSpinCount + Optional.ofNullable(newDataDo.getContinuousNoFreeSpinCount()).orElse(0L);
        if (CollectionUtil.isEmpty(this.specialRuleList)) {
            this.specialRuleList = newDataDo.getSpecialRuleList();
        } else {
            if (CollectionUtil.isNotEmpty(newDataDo.getSpecialRuleList())) {
                // 找出当前没有的规则
                List<SpecialRuleDo> newList = newDataDo.getSpecialRuleList().stream().filter(item -> !this.specialRuleList.contains(item)).collect(Collectors.toList());
                // 存量规则匹配
                Map<SpecialRuleDo, SpecialRuleDo> newDoMap = newDataDo.getSpecialRuleList().stream().collect(Collectors.toMap(s -> s, s -> s, (s1, s2) -> s1));
                for (SpecialRuleDo specialRuleDo : this.specialRuleList) {
                    SpecialRuleDo specialRuleDo1 = newDoMap.get(specialRuleDo);
                    // 新增里面没有原有的规则
                    if (Objects.isNull(specialRuleDo1)) {
                        continue;
                    }
                    // 连续不中当前规则次数添加
                    specialRuleDo.setContinuousNoCount(specialRuleDo.getContinuousNoCount() + Optional.ofNullable(specialRuleDo1.getContinuousNoCount()).orElse(0));
                    // exitInfo 当前没有数据，后续有数据需要靠约定属性进行处理
                }
                // 将新规则加入进来
                this.specialRuleList.addAll(newList);
            }
            // this.extend 属性，当前没有数据，后续有数据需要靠约定属性进行处理
        }


    }

}

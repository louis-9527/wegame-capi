package com.wegame.capi.dao.repository.domain.metrics;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用户玩法指标存储
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserPlayMetricsPo implements Serializable {
    private static final long serialVersionUID = 350098734224370673L;
    /**
     * 主键ID
     */
    @Id
    @Field("_id")
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
    private List<SpecialRulePo> specialRuleList;
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
    @EqualsAndHashCode(callSuper = false)
    public static class SpecialRulePo implements Serializable {
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

}

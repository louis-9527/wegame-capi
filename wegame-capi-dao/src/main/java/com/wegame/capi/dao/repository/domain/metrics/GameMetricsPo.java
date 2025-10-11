package com.wegame.capi.dao.repository.domain.metrics;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 游戏大盘指标存储
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameMetricsPo implements Serializable {
    private static final long serialVersionUID = 324365787654L;
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
     * 当前游戏RTP
     */
    private BigDecimal currentRtp;
    /**
     * 总的投注金额
     */
    private Long totalBetAmount;
    /**
     * 总的派彩金额
     */
    private Long totalPayoutAmount;
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
}

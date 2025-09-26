package com.wegame.capi.biz.domain.metrics;

import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏大盘指标
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameMetricsDo implements Serializable {
    private static final long serialVersionUID = 3234687564L;
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
     * 新增一个参数。当前的常态干预类型
     *
     * @see GameInnerDosTypeEnum
     */
    private String inUseNormalDosType;
    /**
     * 上次刷盘时间:暂时不写值
     */
    private Long lastFlushTime;
}


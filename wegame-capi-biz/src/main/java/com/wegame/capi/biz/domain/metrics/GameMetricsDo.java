package com.wegame.capi.biz.domain.metrics;

import cn.hutool.core.collection.CollectionUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 添加增量
     *
     * @param newDataDo 新增数据
     */
    public void injectNewData(GameMetricsDo newDataDo) {
        this.totalBetAmount = this.totalBetAmount + Optional.ofNullable(newDataDo.getTotalBetAmount()).orElse(0L);
        this.totalPayoutAmount = this.totalPayoutAmount + Optional.ofNullable(newDataDo.getTotalPayoutAmount()).orElse(0L);
        BigDecimal totalBetAmount = new BigDecimal(this.totalBetAmount);
        BigDecimal totalPayoutAmount = new BigDecimal(this.totalPayoutAmount);
        this.currentRtp = totalPayoutAmount.divide(totalBetAmount, 5, RoundingMode.FLOOR);
        // this.extend 属性，当前没有数据，后续有数据需要靠约定属性进行处理
    }
}



}

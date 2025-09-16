package com.wegame.capi.biz.domain.metrics;

import com.wegame.capi.biz.innerdos.config.GameRtpDosCondition;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameMetricsPropertyNameEnum;
import com.wegame.capi.client.enums.innerdos.MetricsPropertyTypeEnum;
import com.wegame.capi.common.Constants;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
     * 新增一个参数。当前的常态干预类型
     *
     * @see GameInnerDosTypeEnum
     */
    private String inUseNormalDosType;
    /**
     * 上次刷盘时间:暂时不写值
     */
    private Long lastFlushTime;

    /**
     * 添加增量
     */
    public void injectNewData(Map<GameMetricsPropertyNameEnum, Object> metricsDataMap, Map<GameInnerDosTypeEnum, GameRtpDosCondition> gameRtpDosConditionMap) {
        // 固定属性
        this.injectNewFixedData(metricsDataMap, gameRtpDosConditionMap);
        // 扩展 属性
        this.injectNewExtendData(metricsDataMap);
        // 基础属性更新
        this.updateBaseData();
    }

    /**
     * 注入固定属性增量数据
     *
     * @param metricsDataMap         采集map
     * @param gameRtpDosConditionMap 游戏动态干预条件配置
     */
    private void injectNewFixedData(Map<GameMetricsPropertyNameEnum, Object> metricsDataMap, Map<GameInnerDosTypeEnum, GameRtpDosCondition> gameRtpDosConditionMap) {
        // 累计投注金额
        Object totalBetAmountObj = metricsDataMap.get(GameMetricsPropertyNameEnum.totalBetAmount);
        this.totalBetAmount = GameMetricsPropertyNameEnum.totalBetAmount.calc(this.totalBetAmount, totalBetAmountObj, Long.class);

        // 累计获奖金额
        Object totalPayoutAmountObj = metricsDataMap.get(GameMetricsPropertyNameEnum.totalPayoutAmount);
        this.totalPayoutAmount = GameMetricsPropertyNameEnum.totalPayoutAmount.calc(this.totalPayoutAmount, totalPayoutAmountObj, Long.class);

        // 当前rtp 计算
        BigDecimal totalBetAmount = new BigDecimal(this.totalBetAmount);
        BigDecimal totalPayoutAmount = new BigDecimal(this.totalPayoutAmount);
        this.currentRtp = totalPayoutAmount.divide(totalBetAmount, 5, RoundingMode.FLOOR);

        // 当前的常态干预类型
        this.injectInUseNormalDosType(gameRtpDosConditionMap);
    }

    private void injectInUseNormalDosType(Map<GameInnerDosTypeEnum, GameRtpDosCondition> gameRtpDosConditionMap) {
        GameInnerDosTypeEnum gameInnerDosTypeEnum = GameInnerDosTypeEnum.valueOfCode(this.inUseNormalDosType);
        if (Objects.isNull(gameInnerDosTypeEnum)) {
            // 默认走normal
            this.inUseNormalDosType = GameInnerDosTypeEnum.NORMAL_DOS.getCode();
        } else {
            GameRtpDosCondition gameRtpDosCondition = gameRtpDosConditionMap.get(gameInnerDosTypeEnum);
            if (Objects.isNull(gameRtpDosCondition)) {
                // 如果没有匹配条件的类型，则不处理:一般来说normal 不会配置
                return;
            }

            // 先校验是否满足退出条件
            boolean outFlag = gameRtpDosCondition.getOut().assertIt(this.currentRtp);
            if (!outFlag) {
                // 没有退出当前条件，则返回
                return;
            }

            // 校验其他进入条件
            GameInnerDosTypeEnum inUseType = GameInnerDosTypeEnum.NORMAL_DOS;
            for (Map.Entry<GameInnerDosTypeEnum, GameRtpDosCondition> entry : gameRtpDosConditionMap.entrySet()) {
                if (Objects.equals(entry.getKey(), gameInnerDosTypeEnum)) {
                    // 当前条件不处理
                    continue;
                }
                boolean inFlag = entry.getValue().getIn().assertIt(this.currentRtp);
                if (inFlag) {
                    // 如果满足进入条件，则使用改条件
                    inUseType = entry.getKey();
                    break;
                }
            }
            this.inUseNormalDosType = inUseType.getCode();
        }
    }

    /**
     * 注入新的扩展数据
     *
     * @param metricsDataMap 数据map
     */
    private void injectNewExtendData(Map<GameMetricsPropertyNameEnum, Object> metricsDataMap) {
        for (Map.Entry<GameMetricsPropertyNameEnum, Object> entry : metricsDataMap.entrySet()) {
            // 只处理扩展类型参数
            if (!Objects.equals(entry.getKey().getType(), MetricsPropertyTypeEnum.EXTEND)) {
                continue;
            }
            // 计算值
            Object newExtendValue = entry.getKey().calc(this.extend.get(entry.getKey().getPropertyName()), entry.getValue(), Object.class);
            this.extend.put(entry.getKey().getPropertyName(), newExtendValue);
        }
    }

    private void updateBaseData() {
        // 版本号
        this.version = Optional.ofNullable(this.version).map(v -> v + 1).orElse(1L);
        // 更新人
        this.updater = Constants.SYSTEM;
        // 更新时间
        this.updateTime = System.currentTimeMillis();
    }
}


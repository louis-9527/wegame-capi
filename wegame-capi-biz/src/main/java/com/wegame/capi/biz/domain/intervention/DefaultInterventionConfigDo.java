package com.wegame.capi.biz.domain.intervention;

import com.wegame.capi.client.enums.BetTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 默认干预参数模型
 */
@SuperBuilder
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DefaultInterventionConfigDo implements Serializable {
    private static final long serialVersionUID = -1800496652360985600L;
    //=========触发条件================
    /**
     * 是否本局空盘
     */
    private boolean isCurrentEmpty;
    /**
     * 最大累计游戏局数
     */
    private Integer maxTotalGameCount;
    /**
     * 最小累计游戏局数
     */
    private Integer minTotalGameCount;
    /**
     * 最小今日局数
     */
    private Integer minTodayGameCount;
    /**
     * 最大今日局数
     */
    private Integer maxTodayGameCount;
    /**
     * 最大rtp
     */
    private BigDecimal maxRtp;
    /**
     * 最小rtp
     */
    private BigDecimal minRtp;
    /**
     * 最小当日不中fg次数
     */
    private Integer minTodayNoHitFgCount;
    /**
     * 最小模版倍率
     */
    private BigDecimal minTemplateMultiple;
    /**
     * 最小连续空盘次数
     */
    private Integer minContinuousEmptyCount;
    /**
     * 投注额系数
     */
    private BigDecimal betAmountCoefficient;
    /**
     * 支持的投注类型
     */
    private List<BetTypeEnum> supportBetTypeList;



    //=========触发概率================
    /**
     * 触发概率
     */
    private BigDecimal triggerProbability;
    /**
     * 触发概率系数
     */
    private BigDecimal triggerProbabilityCoefficient;


    //=========重复性================
    /**
     * 重复类型
     * @see com.wegame.capi.common.enums.RepeatTypeEnum
     */
    private String repeatType;

    //=========干预措施================
    /**
     * 干预模版name
     * eg:98rtp
     */
    private String rtpName;
    /**
     * 兜底干预模版name
     * eg:96rtp
     */
    private String backstopRtpName;
    /**
     * 干预模版倍率范围
     * eg:0-1
     */
    private String interventionTemplateRange;
    /**
     * 干预重试次数
     */
    private int interventionRetryCount;
    /**
     * 干预模版倍率系数
     */
    private BigDecimal interventionMultipleCoefficient;
    /**
     * 干预模版倍率偏差
     */
    private BigDecimal multipleBias;


}

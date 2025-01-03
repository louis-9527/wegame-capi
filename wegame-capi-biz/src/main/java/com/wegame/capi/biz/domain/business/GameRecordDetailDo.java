package com.wegame.capi.biz.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * game_record_detail：游戏记录明细
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordDetailDo implements Serializable {
    private static final long serialVersionUID = -3554735410157949952L;
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 投注金额 单位：分
     */
    private Long betAmount;
    /**
     * 派彩金额 单位：分
     */
    private Long rewardAmount;
    /**
     * 游戏结果json
     */
    private String gameResult;
    /**
     * 中奖结果json
     */
    private String rewardResult;
    /**
     * 倍数
     */
    private BigDecimal multiple;

    ///////////////  above auto generated ///////////////
}


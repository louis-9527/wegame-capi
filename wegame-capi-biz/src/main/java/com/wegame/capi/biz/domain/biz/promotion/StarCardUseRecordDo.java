package com.wegame.capi.biz.domain.biz.promotion;

import com.wegame.capi.common.enums.StarCardReportStatusEnum;
import com.wegame.capi.common.enums.StarCardUseStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StarCardUseRecordDo implements Serializable {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 奖励ID
     */
    private String rewardId;

    /**
     * 奖励配置id
     */
    private String rewardConfigId;
    /**
     * 玩家ID
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playName;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 派奖金额
     */
    private Long payOutAmount;
    /**
     * 使用状态
     */
    private StarCardUseStatusEnum useStatus;

    /**
     * 核销失败原因
     */
    private String failMsg;

    /**
     * 上报状态
     */
    private StarCardReportStatusEnum reportStatus;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}

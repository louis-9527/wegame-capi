package com.wegame.capi.biz.domain.biz.promotion;

import com.wegame.capi.common.enums.StartCardReportStatusEnum;
import com.wegame.capi.common.enums.StartCardUseStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StartCardUseRecordDo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 奖励ID
     */
    private String rewardId;

    /**
     * Reward config Id
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
    private StartCardUseStatusEnum useStatus;

    /**
     * 核销失败原因
     */
    private String failMsg;

    /**
     * 上报状态
     */
    private StartCardReportStatusEnum reportStatus;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;


}

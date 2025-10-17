package com.wegame.capi.api.domain.res.contract;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetPlayerContractRes implements Serializable {
    private static final long serialVersionUID = -6323243436518L;
    /**
     * 平台id
     */
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * refId
     */
    private String refId;
    /**
     * 玩家姓名
     */
    private String playerName;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * rewardId
     */
    private String rewardId;
    /**
     * experience
     */
    private BigDecimal experience;
    /**
     * target
     */
    private BigDecimal target;
    /**
     * Contract status: 1:'Unassigned',2:'Countdown',3:'Claimable'
     */
    private String status;
    /**
     * StarCard Image UR
     */
    private String starCardImgUrl;
    /**
     * freeSpin Image URL
     */
    private String freeSpinImgUrl;
    /**
     * Reward type: 1: starCard / 2: freeSpin
     */
    private Integer rewardType;
    /**
     * isEnabled
     */
    private Boolean isEnabled;
}

package com.wegame.capi.client.domain.vo.contract;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetPlayerContractVo implements Serializable {
    private static final long serialVersionUID = -632887243L;
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
     * imgURL
     */
    private String imgURL;
    /**
     * rewardType
     */
    private Integer rewardType;
    /**
     * isEnabled
     */
    private Boolean isEnabled;
}

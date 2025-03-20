package com.wegame.capi.dao.mapper.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 星卡使用记录
 *
 * @author genereate
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StarCardUseRecordPo implements Serializable {
    private static final long serialVersionUID = -3591247100811702013L;
    /**
     * 主键ID
     */
    @Id
    @Field("_id")
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
     * 游戏ID
     */
    private String gameId;
    /**
     * 玩家ID
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playName;
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
    private Integer useStatus;
    /**
     * 核销失败原因
     */
    private String failMsg;
    /**
     * 上报状态
     */
    private Integer reportStatus;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}


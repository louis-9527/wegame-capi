package com.wegame.capi.dao.repository.domain.jackpot;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * jackpot_user_reward_record：用户jackpot中奖记录
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JackpotUserRewardRecordPo implements Serializable {
    private static final long serialVersionUID = -588285755010311168L;
    /**
     * 主键ID
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 玩家id
     */
    private String playerName;
    /**
     * 投注id
     */
    private Long betRecordId;
    /**
     * 投注id
     */
    private Long gameRecordId;
    /**
     * 投注id（单钱包）
     */
    private String betId;
    /**
     * 奖池id
     */
    private Long rewardPoolId;
    /**
     * 类型 1：grand 2:major 3:minor 4:mini 5:betBonus
     */
    private Integer rewardPoolType;
    /**
     * 中奖金额.单位：厘
     */
    private Long rewardAmount;
    /**
     * 派奖流水号（refid）
     */
    private String rewardGrantSerialNo;
    /**
     * 派奖状态.0:未派奖.1:派奖中（状态待定）.2:已派奖
     */
    private Integer rewardGrantStatus;
    /**
     * 派奖成功时间
     */
    private Date rewardGrantTime;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间/中奖时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Date updateTime;

   ///////////////  above auto generated ///////////////
}


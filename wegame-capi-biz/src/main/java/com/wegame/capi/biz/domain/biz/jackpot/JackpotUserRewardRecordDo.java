package com.wegame.capi.biz.domain.biz.jackpot;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardGrantStatusEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * jackpot_user_reward_record：用户jackpot中奖记录
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JackpotUserRewardRecordDo implements Serializable {
    private static final long serialVersionUID = -1528151784013020160L;
    /**
     * 主键ID
     */
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
     * 类型 1：grand 2:major 3:minor 4:mini
     */
    private JackpotRewardPoolTypeEnum rewardPoolType;

    /**
     * 是否使用预留池
     */
    private boolean useReversedPool;
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
    private JackpotRewardGrantStatusEnum rewardGrantStatus;
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
    private Long createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Long updateTime;

    ///////////////  above auto generated ///////////////

    public static JackpotUserRewardRecordDo build(JackpotDrawAndAwardDo drawAndAwardDo, Long betGameId, Long rewardAmount, Long rewardPoolId, JackpotRewardPoolTypeEnum poolTypeEnum, JackpotTypeEnum jackpotTypeEnum, boolean useReversedPool) {
        return JackpotUserRewardRecordDo.builder()
                .userId(drawAndAwardDo.getUserId())
                .playerName(drawAndAwardDo.getPlayerName())
                .betRecordId(drawAndAwardDo.getBetRecordId())
                .betId(drawAndAwardDo.getBetId())
                .gameRecordId(betGameId)
                .rewardAmount(rewardAmount)
                .useReversedPool(useReversedPool)
                .rewardPoolId(rewardPoolId)
                .rewardPoolType(poolTypeEnum)
                .rewardGrantSerialNo(buildGrantSerialNo(drawAndAwardDo.getBetRecordId(), jackpotTypeEnum))
                .rewardGrantStatus(JackpotRewardGrantStatusEnum.NONE)
                .createTime(DateUtil.getCurrentMilli())
                .updateTime(DateUtil.getCurrentMilli())
                .build();

    }

    private static String buildGrantSerialNo(Long betRecordId, JackpotTypeEnum jackpotTypeEnum) {
        return betRecordId+jackpotTypeEnum.getGameType().getId();
    }

}


package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventDetailDo implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 记录id
     */
    private Long eventRecordId;
    /**
     * 干预事件ID
     */
    private String eventId;
    /**
     * 游戏ID
     */
    private String  operatorGameId;
    /**
     * 配置ID,命中那条倍数
     */
    private Long configId;
    /**
     * 渠道ID
     */
    private String channelId;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 玩家ID(三方)
     */
    private String playerId;
    /**
     * 玩家名称(三方)
     */
    private String playerName;
    /**
     * 玩家ID
     */
    private String userId;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 派奖金额
     */
    private Long payOutAmount;
    /**
     * 干预金额
     */
    private Long eventAmount;
    /**
     * 下注时间
     */
    private Long betTime;
    /**
     * 派奖时间
     */
    private Long payOutTime;
    /**
     * 投注id（单钱包）
     */
    private String betId;
    /**
     * 投注记录Id
     */
    private Long betRecordId;
    /**
     * 订单状态
     */
    private BetStatusEnum betStatus;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}

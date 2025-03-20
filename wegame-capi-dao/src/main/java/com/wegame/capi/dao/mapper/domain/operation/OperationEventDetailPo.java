package com.wegame.capi.dao.mapper.domain.operation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 干预事件明细Po
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventDetailPo implements Serializable {
    private static final long serialVersionUID = -5633677937689748357L;
    /**
     * 主键ID
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * 干预事件ID(系统)
     */
    private Long operatorRecordId;
    /**
     * 干预事件ID
     */
    private String eventId;
    /**
     * 游戏ID
     */
    private String  operatorGameId;
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
     * 事件干预金额
     */
    private Long eventAmount;
    /**
     * 派奖时间
     */
    private Long payOutTime;
    /**
     * 下注时间
     */
    private Long betTime;
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
    private Integer betStatus;
    /**
     * 渠道ID
     */
    private String channelId;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}

package com.wegame.capi.dao.repository.domain.operation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 干预事件Po
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventPo implements Serializable {
    private static final long serialVersionUID = 4993596549150377985L;
    /**
     * 主键ID
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * 干预事件ID
     */
    private String eventId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 干预金额
     */
    private Long bonus;
    /**
     * 最小干预金额
     */
    private Long minBonus;
    /**
     * 最大干预金额
     */
    private Long maxBonus;
    /**
     * 已完成干预金额
     */
    private Long finishBonus;
    /**
     * 干预事件类型
     * 代码：1, 表示正常游戏紧急干预.
     * 代码:  2, 表示正常游戏普通干预，可以由游戏方根据情况调整干预手段.
     * 代码:  3,  表示免费游戏紧急干预
     * 代码:  4, 表示免费游戏普通干预
     */
    private Integer eventKind;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 渠道ID
     */
    private String channelId;
    /**
     * 生效时间
     */
    private Long effectedTime;
    /**
     * 过期时间
     */
    private Long expiredTime;
    /**
     * 干预状态
     * com.wegame.capi.common.enums.DynamicOperationEventStatusEnum
     */
    private Integer eventStatus;
    /**
     * 干预状态
     * com.wegame.capi.common.enums.DynamicOperationEventSubStatusEnum
     */
    private Integer eventSubStatus;
    /**
     * 上报状态
     */
    private Integer reportStatus;
    /**
     * 干预事件消息快照
     */
    private String eventMsgDetail;
    /**
     * reportFailMsg
     */
    private String reportFailMsg;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
}

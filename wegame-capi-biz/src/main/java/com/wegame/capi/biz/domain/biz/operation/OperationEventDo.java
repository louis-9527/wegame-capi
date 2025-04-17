package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.common.enums.OperationEventKindEnum;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.common.enums.OperationEventSubStatusEnum;
import com.wegame.capi.common.enums.OperationEventReportStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventDo implements Serializable {
    /**
     * id
     */
    private Long id;
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
     * 干预事件ID
     */
    private String eventId;
    /**
     * 干预事件类型
     * 代码：1, 表示正常游戏紧急干预.
     * 代码:  2, 表示正常游戏普通干预，可以由游戏方根据情况调整干预手段.
     * 代码:  3,  表示免费游戏紧急干预
     * 代码:  4, 表示免费游戏普通干预
     */
    private OperationEventKindEnum eventKind;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * channelId
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
     */
    private OperationEventStatusEnum eventStatus;
    /**
     * 干预子状态
     */
    private OperationEventSubStatusEnum eventSubStatus;

    /**
     * 上报状态
     */
    private OperationEventReportStatusEnum reportStatus;
    /**
     * 干预事件快照
     */
    private String eventMsgDetail;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 上报时间
     */
    private Long reportTime;
    /**
     * 上报失败原因
     */
    private String reportFailMsg;
}

package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationEventDo {

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
    private DynamicOperationEventKindEnum eventKind;
    /**
     * 游戏ID
     */
    private String gameId;

    private String channelId;
    /**
     * 生效时间
     */
    private Long effectedTime;

    /**
     * 过期时间
     */
    private Long expiredTime;

    private BetTypeEnum betType;

    /**
     * 干预状态(0:干预中(默认),1:已完成(正常),2:已完成(过期))
     */
    private DynamicOperationEventStatusEnum eventStatus;

    /**
     * 上报状态
     */
    private DynamicOperationReportStatusEnum reportStatus;

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
     * 上报失败原因
     */
    private String reportFailMsg;

    /**
     * 本次干预金额
     */
    private Long interventionAmount;

}

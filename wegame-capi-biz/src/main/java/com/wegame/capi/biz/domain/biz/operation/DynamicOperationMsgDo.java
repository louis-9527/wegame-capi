package com.wegame.capi.biz.domain.biz.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationMsgDo {

    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 干预金额
     */
    private BigDecimal bonus;
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
    private Integer eventKind;



    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 游戏ID(Indicates the game's unique ID in the FPMS system in which the intervention event works)
     */
    private String operatorGameId;


    /**
     * 渠道ID
     */
    private String channelId;

    /**
     * 策略ID
     */
    private String strategyId;

    /**
     * 订单ID
     */
    private String orderNo;

    /**
     * 结算日期
     */
    private String settlementTime;

    /**
     * 生效时间
     */
    private String effectedTime;

    /**
     * 过期时间(如果为空,则事件不会过期)
     */
    private String expiredTime;

    /**
     * strategyId = 1, 命中策略时，该投注记录是该用户第几笔投注；
     * strategyId = 2, 命中策略时，该投注记录是该用户第几笔投注
     * strategyId = 3, 命中策略时，该投注记录是该用户的session 时长
     * 主要用于调试与分析。
     */
    private BigDecimal timesOrSessionDuration;

}

package com.wegame.capi.dao.mapper.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationPo implements Serializable {
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
     * 干预剩余金额
     */
    private Long bonusBalance;


    /**
     * 干预事件类型
     * 代码：1, 表示正常游戏紧急干预.
     * 代码:  2, 表示正常游戏普通干预，可以由游戏方根据情况调整干预手段.
     * 代码:  3,  表示免费游戏紧急干预
     * 代码:  4, 表示免费游戏普通干预
     */
    private Integer eventKind;


    /**
     * 游戏ID(Indicates the game's unique ID in the FPMS system in which the intervention event works)
     */
    private String operatorGameId;


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
     * 干预状态(0:干预中,1:已完成(正常),2:已完成(过期))
     */
    private Integer eventStatus;

    /**
     * 干预事件消息快照
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
}

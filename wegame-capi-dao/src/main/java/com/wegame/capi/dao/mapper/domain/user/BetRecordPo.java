package com.wegame.capi.dao.mapper.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * game_record：投注记录
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document("bet_record")
public class BetRecordPo implements Serializable {
    private static final long serialVersionUID = -633389003068449792L;
    /**
     * 主键ID
     */
    @Id
    @Field("id")
    private Long id;
    /**
     * 平台id
     */
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 游戏id
     */
    private String gameId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 投注类型
     */
    private String betType;
    /**
     * 投注子类型
     */
    private String betSubType;
    /**
     * 投注id（单钱包）
     */
    private String betId;
    /**
     * 投注流水号（对应单钱包refId）
     */
    private String betSerialNo;
    /**
     * 投注金额 单位：分
     */
    private Long betAmount;
    /**
     * 派彩金额 单位：分
     */
    private Long payOutAmount;
    /**
     * 营销标识 0否 1是
     */
    private Integer marketFlag;
    /**
     * 状态 0进行中 1已完结 2无效
     */
    private Integer status;
    /**
     * 免费轮获得标识 0否 1是
     */
    private Integer freeSpinsFlag;
    /**
     * 免费轮次数
     */
    private Integer freeSpinsTimes;

    /**
     * 免费轮关联原投注记录Id
     */
    private Long freeSpinsRefId;
    /**
     * 游戏记录明细json
     */
    private Object detail;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
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
}


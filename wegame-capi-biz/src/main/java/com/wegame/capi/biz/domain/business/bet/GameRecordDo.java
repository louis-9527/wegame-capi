package com.wegame.capi.biz.domain.business.bet;

import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.common.enums.GameBetSubTypeEnum;
import com.wegame.capi.common.enums.GameBetTypeEnum;
import com.wegame.capi.common.enums.GameStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * game_record：投注记录
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordDo<T> implements Serializable {
    private static final long serialVersionUID = -2093670798700975104L;
    /**
     * 主键ID
     */
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
     * 投注类型 1正常投注 2免费轮投注
     */
    private GameBetTypeEnum betType;
    /**
     * 投注子类型 11正常投注 21购买免费轮投注 22非购买免费轮投注
     */
    private GameBetSubTypeEnum betSubType;
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
    private BooleanEnum marketFlag;
    /**
     * 状态 0进行中 1已完结 2无效
     */
    private GameStatusEnum status;
    /**
     * 免费轮获得标识 0否 1是
     */
    private BooleanEnum freeSpinsFlag;
    /**
     * 免费轮次数
     */
    private Integer freeSpinsTimes;
    /**
     * 游戏记录明细
     */
    private List<T> detail;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
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

    /**
     * 游戏结果json
     */
    private Integer[][] gameResult;

    ///////////////  above auto generated ///////////////
}


package com.wegame.capi.biz.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * user_present_amount_record：用户赠送金额记录
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserPresentAmountRecordDo implements Serializable {
    private static final long serialVersionUID = -8399160378821548032L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 玩家ID
     */
    private String playerId;
    /**
     * 类型：1增加,2扣减
     */
    private Integer type;
    /**
     * 子类型：11营销赠送, 12游戏赠送失败回滚, 21游戏赠送后扣减
     */
    private Integer subType;
    /**
     * 业务Id(增加：赠送流水号Id/,减少为投注记录Id)
     */
    private String bizId;
    /**
     * 变更金额
     */
    private Long amount;
    /**
     * 状态：0进行中,1已完成
     */
    private Integer status;
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

    ///////////////  above auto generated ///////////////
}


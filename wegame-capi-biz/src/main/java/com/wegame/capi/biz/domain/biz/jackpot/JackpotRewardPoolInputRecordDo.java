package com.wegame.capi.biz.domain.biz.jackpot;

import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolInputTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * jackpot_reward_pool_input_record：jackpot奖池流入记录
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JackpotRewardPoolInputRecordDo implements Serializable {
    private static final long serialVersionUID = -2289705874960169984L;
    /**
     * 主键ID/投注记录ID
     */
    private Long id;
    /**
     * 类型.10派彩后流入
     */
    private JackpotRewardPoolInputTypeEnum type;
    /**
     * grand金额
     */
    private Long grandAmount;
    /**
     * major金额
     */
    private Long majorAmount;
    /**
     * minor金额
     */
    private Long minorAmount;
    /**
     * mini金额
     */
    private Long miniAmount;

    /**
     * grand预留金额
     */
    private Long grandReservedAmount;
    /**
     * major预留金额
     */
    private Long majorReservedAmount;
    /**
     * minor预留金额
     */
    private Long minorReservedAmount;
    /**
     * mini预留金额
     */
    private Long miniReservedAmount;

    /**
     * 累计标识：0：未累计， 1：已累计
     */
    private JackpotAccFlagEnum accFlag;
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


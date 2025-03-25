package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class BetResultDo<R extends ToVoConverter> implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 投注结果
     */
    private BetStatusEnum betStatus;
    /**
     * 游戏记录id
     */
    private Long betRecordId;
    /**
     * 游戏Id
     */
    private String gameId;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注子类型
     */
    private BetSubTypeEnum betSubType;
    /**
     * 余额
     */
    private Long credit;
    /**
     * （从开始到当前轮）合计中奖总金额
     */
    private Long overallRewardAmount;
    /**
     * 总计派奖金额
     */
    private Long totalPayoutAmount;

    /**
     * 投注ID(单钱包)
     */
    private String betId;
    /**
     * 投注结果详情
     */
    private R resultDetail;
}


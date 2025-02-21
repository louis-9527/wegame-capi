package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.common.enums.BetStatusEnum;
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
public class DynamicOperationEventDetailDo {

    private Long id;

    private Long operatorRecordId;

    /**
     * 干预事件ID
     */
    private String eventId;

    /**
     * 干预金额
     */
    private String  operatorGameId;

    /**
     * 玩家ID(三方)
     */
    private String playerId;
    /**
     * 玩家名称(三方)
     */
    private String playerName;
    /**
     * 玩家ID
     */
    private String userId;

    /**
     * 派奖(消耗金额)
     */
    private Long payOutAmount;

    /**
     * 派奖时间
     */
    private Long payOutTime;

    /**
     * 投注id（单钱包）
     */
    private String betId;
    /**
     * 投注记录Id
     */
    private Long betRecordId;

    /**
     * 订单状态
     */
    private BetStatusEnum betStatus;

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

}

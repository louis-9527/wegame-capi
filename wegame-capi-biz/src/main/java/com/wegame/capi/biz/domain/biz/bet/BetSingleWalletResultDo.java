package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BetSingleWalletResultDo {
    /**
     * 投注id
     */
    private String betId;
    /**
     * refId
     */
    private String refId;
    /**
     * 投注后用户余额（分）
     */
    private Long credit;
    /**
     * 投注状态
     */
    private BetStatusEnum betStatus;

    /**
     * 投注失败原因
     */
    private ErrorCode errorCode;

}

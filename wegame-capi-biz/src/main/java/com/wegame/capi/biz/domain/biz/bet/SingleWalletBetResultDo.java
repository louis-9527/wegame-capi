package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SingleWalletBetResultDo implements Serializable {
    /**
     * 投注id
     */
    private String betId;
    /**
     * refId
     */
    private String refId;
    /**
     * 投注后用户余额（厘）
     */
    private Long credit;
    /**
     * 投注状态
     */
    private BetStatusEnum betStatus;
    /**
     * 错误码
     */
    private ErrorCode errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;
}

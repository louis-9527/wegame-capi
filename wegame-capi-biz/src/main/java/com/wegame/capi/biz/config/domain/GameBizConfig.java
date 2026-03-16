package com.wegame.capi.biz.config.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBizConfig implements Serializable {

    /**
     * 派彩金额限制配置
     */
    private PayoutAmountLimitConfig payoutAmountLimitConfig = new PayoutAmountLimitConfig();

    /**
     * 业务干预配置
     */
    private BizInterfereConfig bizInterfereConfig = new BizInterfereConfig();

    public void bizValidate() {
        throw new BusinessException(ErrorCode.ERR_INTER, "no support");
    }

    public void validate() {
        AssertUtil.assertNotNull(payoutAmountLimitConfig, ErrorCode.ERR_PARAM);
        payoutAmountLimitConfig.validate();
        bizInterfereConfig.validate();
        this.bizValidate();
    }

}

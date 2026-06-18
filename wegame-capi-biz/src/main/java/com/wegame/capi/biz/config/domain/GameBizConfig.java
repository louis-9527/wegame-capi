package com.wegame.capi.biz.config.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class GameBizConfig implements Serializable, ConfigValidate {

    /**
     * 派彩金额限制配置
     */
    private PayoutAmountLimitConfig payoutAmountLimitConfig = new PayoutAmountLimitConfig();
    /**
     * 用户干预功能开关
     */
    private boolean interventionSwitch;

    public void bizValidate() {
        throw new BusinessException(ErrorCode.ERR_INTER, "no support");
    }

    @Override
    public void validate() {
        AssertUtil.assertNotNull(payoutAmountLimitConfig, ErrorCode.ERR_PARAM);
        payoutAmountLimitConfig.validate();
        this.bizValidate();
    }

}

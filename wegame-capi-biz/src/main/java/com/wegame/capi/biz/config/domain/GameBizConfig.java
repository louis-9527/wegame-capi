package com.wegame.capi.biz.config.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@Data
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBizConfig implements Serializable, ConfigValidate {

    /**
     * 派彩金额限制配置
     */
    private PayoutAmountLimitConfig payoutAmountLimitConfig = new PayoutAmountLimitConfig();

    public void bizValidate() {
        throw new BusinessException(ErrorCode.ERR_INTER, "no support");
    }

    public GameTypeEnum gameType() {
        // to sub class
        return null;
    }

    @Override
    //@PostConstruct
    public void validate() {
        AssertUtil.assertNotNull(payoutAmountLimitConfig, ErrorCode.ERR_PARAM);
        payoutAmountLimitConfig.validate();
        this.bizValidate();
    }

}

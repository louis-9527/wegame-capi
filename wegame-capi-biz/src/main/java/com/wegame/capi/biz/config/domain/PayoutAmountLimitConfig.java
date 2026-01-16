package com.wegame.capi.biz.config.domain;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutAmountLimitConfig implements Serializable {
    private static final long serialVersionUID = 43453451L;
    /**
     * 重试次数限制
     * 默认1
     */
    private int retryCount = 1;
    /**
     * 金额限制
     */
    private long limitAmount = 1000000_000;
    /**
     * 限制的投注类型
     */
    private List<BetTypeEnum> limitBetTypeList = Lists.newArrayList(BetTypeEnum.NORMAL_BET);

    public void validate() {
        AssertUtil.assertTrue(this.retryCount >= 0, ErrorCode.ERR_INTER, "retryCount is less than 0");
        AssertUtil.assertTrue(CollectionUtil.isNotEmpty(this.limitBetTypeList), ErrorCode.ERR_INTER, "limitBetTypeList is empty");
        log.info("maxPayoutAmountLimit:{}", this);
    }

}

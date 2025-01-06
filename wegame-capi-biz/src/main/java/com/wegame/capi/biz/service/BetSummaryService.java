package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.client.domain.request.BetReconciliationRequest;
import com.wegame.capi.client.domain.vo.BetSummaryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 对账服务
 */
@Service
public class BetSummaryService {

    @Autowired
    SingleWalletApi singleWalletApi;

    public BetSummaryVo betSummary(BetReconciliationRequest request) {
        String hash = singleWalletApi.hash(request.getChannelId(), request.getHash(), request.getRefId(), DateUtil.toUTC(request.getCreateTime()));
        AssertUtil.assertTrue(StringUtils.equals(hash, request.getHash()), ErrorCode.ERR_PARAM);
        // todo 麦冬 统计结果
        return BetSummaryVo.builder().build();
    }
}

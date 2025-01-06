package com.wegame.capi.biz.service;

import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 单钱包服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletService {

    @Autowired
    SingleWalletApi singleWalletApi;

    // todo


    public BetWithPayoutRes betWithPayout(BetWithPayoutReq req) {
        return singleWalletApi.betWithPayout(req);
    }
}
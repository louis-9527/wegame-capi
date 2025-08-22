package com.wegame.capi.biz.service.contract;

import com.wegame.capi.api.contract.ContractApi;
import com.wegame.capi.biz.builder.req.GetPlayerContractReqBuilder;
import com.wegame.capi.biz.builder.req.RedeemPlayerContractReqBuilder;
import com.wegame.capi.biz.converter.promotion.GetPlayerContractConverter;
import com.wegame.capi.biz.converter.promotion.RedeemPlayerContractConverter;
import com.wegame.capi.biz.domain.biz.contract.GetPlayerContractDo;
import com.wegame.capi.biz.domain.biz.contract.RedeemPlayerContractDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 契约service
 */
@Slf4j
@Component
public class ContractService {

    @Autowired
    ContractApi contractApi;

    public GetPlayerContractDo getPlayerContract() {
        contractApi.getPlayerContract(GetPlayerContractReqBuilder.buildGetPlayerContractReq());
        return GetPlayerContractConverter.resToDo(contractApi.getPlayerContract(GetPlayerContractReqBuilder.buildGetPlayerContractReq()));
    }

    public RedeemPlayerContractDo redeemPlayerContract() {
        return RedeemPlayerContractConverter.resToDo(contractApi.redeemPlayerContract(RedeemPlayerContractReqBuilder.buildRedeemPlayerContractReq()));
    }


}

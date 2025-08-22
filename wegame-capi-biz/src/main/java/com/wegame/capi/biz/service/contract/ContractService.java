package com.wegame.capi.biz.service.contract;

import com.wegame.capi.api.contract.ContractApi;
import com.wegame.capi.api.domain.res.contract.RedeemPlayerContractRes;
import com.wegame.capi.biz.builder.req.GetPlayerContractReqBuilder;
import com.wegame.capi.biz.builder.req.RedeemPlayerContractReqBuilder;
import com.wegame.capi.biz.domain.biz.promotion.AchievementDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemAchievementDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.biz.converter.promotion.AchievementConverter.toDoList;
import static com.wegame.capi.biz.converter.promotion.RedeemAchievementConverter.toDo;

/**
 * 契约service
 */
@Slf4j
@Component
public class ContractService {

    @Autowired
    ContractApi contractApi;

    public List<AchievementDo.Achievement> getPlayerContract() {

        contractApi.getPlayerContract(GetPlayerContractReqBuilder.buildGetPlayerContractReq());
        return toDoList(getAchievementRes.getAchievementList());
    }

    public RedeemAchievementDo redeemPlayerContract() {
        RedeemPlayerContractRes redeemPlayerContractRes = contractApi.redeemPlayerContract(RedeemPlayerContractReqBuilder.buildRedeemPlayerContractReq());
        return toDo(redeemAchievementRes);
    }




}

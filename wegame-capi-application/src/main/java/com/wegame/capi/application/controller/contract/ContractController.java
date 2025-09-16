package com.wegame.capi.application.controller.contract;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.promotion.GetPlayerContractConverter;
import com.wegame.capi.biz.converter.promotion.RedeemPlayerContractConverter;
import com.wegame.capi.biz.domain.biz.contract.GetPlayerContractDo;
import com.wegame.capi.biz.domain.biz.contract.RedeemPlayerContractDo;
import com.wegame.capi.biz.service.contract.ContractService;
import com.wegame.capi.client.domain.vo.contract.GetPlayerContractVo;
import com.wegame.capi.client.domain.vo.contract.RedeemPlayerContractVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("contract")
@Api(value = "契约模块", tags = {"契约模块"})
public class ContractController {

    @Autowired
    ContractService contractService;

    @LoginRequired
    @ApiOperation(value = "获取玩家契约", httpMethod = "POST")
    @PostMapping("/getPlayerContract")
    public CommonResponse<GetPlayerContractVo> getPlayerContract() {
        GetPlayerContractDo playerContract = contractService.getPlayerContract();
        return CommonResponse.build(GetPlayerContractConverter.doToVo(playerContract));
    }

    @LoginRequired
    @ApiOperation(value = "领取玩家契约", httpMethod = "POST")
    @PostMapping("/redeemPlayerContract")
    public CommonResponse<RedeemPlayerContractVo> redeemPlayerContract() {
        RedeemPlayerContractDo redeemPlayerContractDo = contractService.redeemPlayerContract();
        return CommonResponse.build(RedeemPlayerContractConverter.doToVo(redeemPlayerContractDo));
    }
}

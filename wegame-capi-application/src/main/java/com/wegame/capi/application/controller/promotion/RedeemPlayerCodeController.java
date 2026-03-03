package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.promotion.RedeemPlayerCodeConverter;
import com.wegame.capi.biz.service.promotion.RedeemPlayerCodeService;
import com.wegame.capi.client.domain.request.promotion.RedeemPlayerCodeRequest;
import com.wegame.capi.client.domain.vo.promotion.RedeemPlayerCodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wegame.base.domain.CommonResponse.build;

@Slf4j
@RestController
@RequestMapping("redeem")
@Api(value = "兑换码兑换模块", tags = {"兑换码兑换模块"})
public class RedeemPlayerCodeController {

    @Autowired
    RedeemPlayerCodeService redeemPlayerCodeService;

    @LoginRequired
    @ApiOperation(value = "兑换码兑换", httpMethod = "POST")
    @PostMapping("/redeemPlayerCode")
    public CommonResponse<RedeemPlayerCodeVo> redeemPlayerCode(@RequestBody RedeemPlayerCodeRequest request) {
        request.validate();
        return build(RedeemPlayerCodeConverter.doToVo(redeemPlayerCodeService.redeemPlayerCode(request.getCode())));
    }
}

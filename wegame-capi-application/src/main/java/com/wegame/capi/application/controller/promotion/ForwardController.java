package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.biz.service.promotion.ForwardService;
import com.wegame.capi.client.domain.request.promotion.ForwardRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.wegame.base.domain.CommonResponse.build;

@Slf4j
@RestController
@RequestMapping("promotion")
@Api(value = "转发模块", tags = {"转发模块"})
public class ForwardController {

    @Autowired
    ForwardService forwardService;

    @LoginRequired
    @ApiOperation(value = "Promotion 通用转发（path 由 action 指定）", httpMethod = "POST")
    @PostMapping("/forward")
    public CommonResponse<CommonRes<Map<String, Object>>> forward(@RequestBody ForwardRequest request) {
        request.validate();
        return build(forwardService.forward(request.getAction(), request.getData()));
    }
}

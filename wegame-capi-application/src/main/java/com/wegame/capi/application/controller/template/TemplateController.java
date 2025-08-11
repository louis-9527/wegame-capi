package com.wegame.capi.application.controller.template;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.BetService;
import com.wegame.capi.biz.service.TemplateService;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.generate.TemplateGenerateRequest;
import com.wegame.capi.client.domain.vo.BetResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.BetConverter.requestToDo;
import static com.wegame.capi.biz.converter.BetResultConverter.doToVo;

@Slf4j
@RestController
@RequestMapping("template")
@Api(value = "template模块", tags = {"template模块"})
public class TemplateController {

    @Autowired
    TemplateService templateService;

    @LoginRequired
    @ApiOperation(value = "投注", httpMethod = "POST")
    @PostMapping("/generate/mariamakiling")
    public CommonResponse<Void> generateMariamakiling(@RequestBody TemplateGenerateRequest request) {
        request.validate();
        templateService.generate(request);
        return CommonResponse.build(null);
    }
}

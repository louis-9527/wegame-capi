package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.biz.service.freespin.FreeSpinService;
import com.wegame.capi.client.domain.request.promotion.FreeSpinHistoryOfPlayerRequest;
import com.wegame.capi.client.domain.request.promotion.FreeSpinOfPlayerRequest;
import com.wegame.capi.client.domain.vo.promotion.FreeSpinHistoryOfPlayerVo;
import com.wegame.capi.client.domain.vo.promotion.FreeSpinOfPlayerVo;
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
import static com.wegame.capi.biz.converter.promotion.FreeSpinPlayerConverter.doToVo;
import static com.wegame.capi.biz.converter.promotion.FreeSpinHistoryConverter.toVo;

@Slf4j
@RestController
@RequestMapping("freeSpin")
@Api(value = "营销FreeSpin模块", tags = {"营销FreeSpin模块"})
public class FreeSpinController {

    @Autowired
    FreeSpinService freeSpinService;

    @LoginRequired
    @ApiOperation(value = "freeSpinOfPlayer", httpMethod = "POST")
    @PostMapping("/freeSpinOfPlayer")
    public CommonResponse<FreeSpinOfPlayerVo> freeSpinOfPlayer(@RequestBody FreeSpinOfPlayerRequest request) {
        request.validate();
        return build(doToVo(freeSpinService.freeSpinOfPlayer(request.getCheckCurrent())));
    }
    @LoginRequired
    @ApiOperation(value = "freeSpinHistoryOfPlayer", httpMethod = "POST")
    @PostMapping("/freeSpinHistoryOfPlayer")
    public CommonResponse<CommonRes<Map<String, Object>>> freeSpinHistoryOfPlayer(@RequestBody FreeSpinHistoryOfPlayerRequest request) {
        request.validate();
        return build(freeSpinService.freeSpinHistoryOfPlayer(request.getPage(), request.getLimit()));
    }

}

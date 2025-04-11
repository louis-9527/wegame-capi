package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.promotion.FreeSpinService;
import com.wegame.capi.client.domain.request.promotion.FreeSpinHistoryOfPlayerRequest;
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

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.promotion.FreeSpinPlayerConverter.toVo;
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
    public CommonResponse<FreeSpinOfPlayerVo> freeSpinOfPlayer() {
        return build(toVo(freeSpinService.freeSpinOfPlayer()));
    }
    @LoginRequired
    @ApiOperation(value = "freeSpinHistoryOfPlayer", httpMethod = "POST")
    @PostMapping("/freeSpinHistoryOfPlayer")
    public CommonResponse<FreeSpinHistoryOfPlayerVo> freeSpinHistoryOfPlayer(@RequestBody FreeSpinHistoryOfPlayerRequest request) {
        request.validate();
        return build(toVo(freeSpinService.freeSpinHistoryOfPlayer(request.getPage(), request.getLimit(), request.getStartTime(), request.getEndTime())));
    }

}

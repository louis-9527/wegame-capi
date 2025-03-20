package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.biz.service.promotion.TreasureService;
import com.wegame.capi.client.domain.request.promotion.RedeemTreasureRequest;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;
import com.wegame.capi.client.domain.vo.promotion.TreasureVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.promotion.RedeemTreasureConverter.toVo;
import static com.wegame.capi.biz.converter.promotion.TreasureConverter.toVoList;

@Slf4j
@RestController
@RequestMapping("treasure")
@Api(value = "营销宝箱模块", tags = {"营销宝箱模块"})
public class TreasureController {

    @Autowired
    TreasureService treasureService;

    @LoginRequired
    @ApiOperation(value = "获取宝箱列表", httpMethod = "POST")
    @PostMapping("/getTreasure")
    public CommonResponse<List<TreasureVo>> getTreasure() {
        return build(toVoList(treasureService.getTreasure()));
    }

    @LoginRequired
    @ApiOperation(value = "领取宝箱", httpMethod = "POST")
    @PostMapping("/redeemTreasure")
    public CommonResponse<RedeemTreasureVo> redeemTreasure(@RequestBody RedeemTreasureRequest request) {
        request.validate();
        RedeemTreasureDo redeemTreasureDo = treasureService.redeemTreasure(request.getTreasureId(), request.getLevel());
        return build(toVo(redeemTreasureDo));
    }

}

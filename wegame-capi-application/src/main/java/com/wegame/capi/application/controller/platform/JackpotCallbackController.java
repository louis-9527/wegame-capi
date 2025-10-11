package com.wegame.capi.application.controller.platform;

import com.wegame.capi.application.controller.jackpot.AbstractJackpotController;
import com.wegame.capi.biz.converter.jackpot.JackpotResultConverter;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.client.domain.request.query.GetStatusOfEventEncryptRequest;
import com.wegame.capi.client.domain.request.query.JackpotSearchRequest;
import com.wegame.capi.client.domain.response.JackpotResponse;
import com.wegame.capi.client.domain.vo.JackpotResultVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用于cp方查询jackpot金额
 */
@Slf4j
@RestController
@RequestMapping("jackpot")
@Api(value = "jackpot回调查询金额", tags = {"jackpot回调查询金额"})
public class JackpotCallbackController extends AbstractJackpotController {

    @ApiOperation(value = "获取jackpot详情", httpMethod = "POST")
    @PostMapping("/jackpotDetails")
    public JackpotResponse<JackpotResultVo> jackpotDetails(@RequestBody JackpotSearchRequest searchRequest) {
        GameTypeEnum game = GameTypeEnum.valueOfId(searchRequest.getGameId());

        return JackpotResponse.build(JackpotResultConverter.from(game, jackpotManager(game).listJackpotRewardPool()));
    }


}

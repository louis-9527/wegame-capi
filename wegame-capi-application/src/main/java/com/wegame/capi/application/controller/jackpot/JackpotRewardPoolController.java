package com.wegame.capi.application.controller.jackpot;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * jackpot奖池模块
 *
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("jackpotRewardPool")
@Api(value = "jackpot奖池模块", tags = {"jackpot奖池模块"})
public class JackpotRewardPoolController {

    // todo 走 manager @lyric 参考 BetRecordController
    @Autowired
    JackpotRewardPoolService jackpotRewardPoolService;

    @ApiOperation(value = "查询列表", httpMethod = "GET")
    @GetMapping("/list")
    public CommonResponse<List<JackpotRewardPoolVo>> list() {
        return CommonResponse.build(JackpotRewardPoolConverter.doToVoList(jackpotRewardPoolService.list(JackpotRewardPoolQuery.builder().build())));
    }

    ///////////////  above auto generated ///////////////
}
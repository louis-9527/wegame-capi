package com.wegame.capi.application.controller.jackpot;

import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotUserRewardRecordRequest;
import com.wegame.capi.client.domain.vo.jackpot.JackpotUserRewardRecordVo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户jackpot中奖记录模块
 *
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("jackpotUserRewardRecord")
@Api(value = "用户jackpot中奖记录模块", tags = {"用户jackpot中奖记录模块"})
public class JackpotUserRewardRecordController {

    // todo 走 manager @lyric 参考 BetRecordController
    @Autowired
    JackpotUserRewardRecordService jackpotUserRewardRecordService;

    @ApiOperation(value = "查询历史最高中奖", httpMethod = "GET")
    @GetMapping("/get/history/highest/reward")
    public CommonResponse<JackpotUserRewardRecordVo> getHistoryHighestReward() {
        // todo @lyric
        return CommonResponse.build(JackpotUserRewardRecordConverter.doToVo(jackpotUserRewardRecordService.get(Long.valueOf(0))));
    }

    @ApiOperation(value = "查询：根据投注记录ID & 游戏记录ID", httpMethod = "GET")
    @GetMapping("/get/{betRecordId}/{gameRecordId}")
    public CommonResponse<JackpotUserRewardRecordVo> getByGameRecordId(@PathVariable String betRecordId, @PathVariable String gameRecordId) {
        // todo @lyric
        return CommonResponse.build(JackpotUserRewardRecordConverter.doToVo(jackpotUserRewardRecordService.get(Long.valueOf(0))));
    }

    @ApiOperation(value = "分页查询", httpMethod = "POST")
    @PostMapping("/pageQuery")
    public CommonResponse<PageInfoVo<JackpotUserRewardRecordVo>> pageQuery(@RequestBody PageQueryJackpotUserRewardRecordRequest request) {
        request.validate();
        JackpotUserRewardRecordQuery query = JackpotUserRewardRecordConverter.requestToQuery(request, "order by `create_time` desc");
        long cnt = jackpotUserRewardRecordService.count(query);
        List<JackpotUserRewardRecordDo> list = jackpotUserRewardRecordService.list(query);
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                JackpotUserRewardRecordConverter.doToVoList(list),
                cnt));
    }

    ///////////////  above auto generated ///////////////
}
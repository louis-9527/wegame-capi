package com.wegame.capi.application.controller.jackpot;

import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;

import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotUserRewardRecordRequest;
import com.wegame.capi.client.domain.vo.jackpot.JackpotUserRewardRecordVo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 用户jackpot中奖记录模块
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("jackpotUserRewardRecord")
@Api(value = "用户jackpot中奖记录模块", tags = {"用户jackpot中奖记录模块"})
public class JackpotUserRewardRecordController {

    @Autowired
    JackpotUserRewardRecordService jackpotUserRewardRecordService;


    @ApiOperation(value = "分页查询（批量：根据查询条件）", httpMethod = "POST")
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

    @ApiOperation(value = "查询最大派奖金额记录", httpMethod = "GET")
    @PostMapping("/maxReward")
    public CommonResponse<JackpotUserRewardRecordVo> getMaxUserRewardRecord() {
        // todo  查询mongo
        JackpotUserRewardRecordDo result = jackpotUserRewardRecordService.get(1L);
        return CommonResponse.build(JackpotUserRewardRecordConverter.doToVo(result));
    }

    @ApiOperation(value = "根据中奖gameRecordId查询中奖记录", httpMethod = "GET")
    @PostMapping("/gameRecordId/{gameRecordId}")
    public CommonResponse<JackpotUserRewardRecordVo> getMaxUserRewardRecord(@PathVariable String gameRecordId) {
        // todo  查询mongo
        JackpotUserRewardRecordDo result = jackpotUserRewardRecordService.get(1L);
        return CommonResponse.build(JackpotUserRewardRecordConverter.doToVo(result));
    }
}
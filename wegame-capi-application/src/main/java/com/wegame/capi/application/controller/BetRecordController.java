package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 游戏记录模块
 *
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("betRecord")
@Api(value = "游戏记录模块", tags = {"游戏记录模块"})
public class BetRecordController {

    @Autowired
    BetRecordService betRecordService;

    @LoginRequired
    @ApiOperation(value = "分页查询（批量：根据查询条件）", httpMethod = "POST")
    @PostMapping("/pageQuery")
    public CommonResponse<PageInfoVo<BetRecordVo>> pageQuery(@RequestBody PageQueryBetRecordRequest request) {
        request.validate();
        BetRecordQuery query = BetRecordConverter.requestToQuery(request,"createTime");
        long cnt = betRecordService.count(query);
        if (Objects.equals(cnt, 0L)) {
            return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                    request.getPageSize(),
                    new ArrayList<>(),
                    cnt));
        }
        query.setLimit(request.getPageSize());
        query.setOffset(request.getOffset());
        List<BetRecordDo> list = betRecordService.list(query);
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                BetRecordConverter.doToVoList(list),
                cnt));

    }
}
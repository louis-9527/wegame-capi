package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.converter.BetRecordPageConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordPageVo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public CommonResponse<PageInfoVo<BetRecordPageVo>> pageQuery(@RequestBody PageQueryBetRecordRequest request) {
        request.validate();
        BetRecordQuery query = BetRecordConverter.requestToQuery(request, "createTime");
        long cnt = betRecordService.count(query);
        if (Objects.equals(cnt, 0L)) {
            return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                    request.getPageSize(),
                    new ArrayList<>(),
                    cnt));
        }
        query.setLimit(request.getPageSize());
        query.setOffset(request.getOffset());
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                BetRecordPageConverter.doToPageVoList(betRecordService.list(query)),
                cnt));
    }

    @LoginRequired
    @ApiOperation(value = "根据投注记录id查详情", httpMethod = "GET")
    @GetMapping("/{id}")
    public CommonResponse<BetRecordPageVo> detail(@PathVariable("id") String id) {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM, "id is empty");
        BetRecordDo betRecordDo = betRecordService.get(Long.valueOf(id));
        return CommonResponse.build(BetRecordPageConverter.doToPageDetailVo(betRecordDo));
    }
}
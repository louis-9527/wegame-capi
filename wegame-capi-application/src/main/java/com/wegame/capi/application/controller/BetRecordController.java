package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 投注记录模块
 *
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("betRecord")
@Api(value = "投注记录模块", tags = {"投注记录模块"})
public class BetRecordController {

    @Autowired
    BetRecordService betRecordService;

    @LoginRequired
    @ApiOperation(value = "分页查询", httpMethod = "POST")
    @PostMapping("/pageQuery")
    public CommonResponse<PageInfoVo<BetRecordVo>> pageQuery(@RequestBody PageQueryBetRecordRequest request) {
        request.validate();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        request.setUserId(userInfo.getId());
        BetRecordQuery query = BetRecordConverter.requestToQuery(request, "createTime");
        query.setLimit(request.getPageSize());
        query.setLastCreateTime(request.getLastCreateTime());
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                BetRecordConverter.doToVoList(betRecordService.listForPage(query, GameTypeEnum.valueOfId(userInfo.getGameId()))),
                0L));
    }

    @LoginRequired
    @ApiOperation(value = "查询详情", httpMethod = "GET")
    @GetMapping("/get/{id}")
    public CommonResponse<BetRecordVo> get(@PathVariable("id") String id) {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        BetRecordDo betRecordDo = betRecordService.get(Long.valueOf(id), GameTypeEnum.valueOfId(userInfo.getGameId()));
        return CommonResponse.build(BetRecordConverter.doToVo(betRecordDo, false));
    }
}
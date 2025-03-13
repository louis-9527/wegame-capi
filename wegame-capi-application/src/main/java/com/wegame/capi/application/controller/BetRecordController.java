package com.wegame.capi.application.controller;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    public static final Map<GameTypeEnum, String> RECORD_MANAGER_MAP = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingRecordManager")
            .build();

    @Autowired
    BetRecordService betRecordService;
    @Autowired
    Map<String, AbstractRecordManager> recordManagerMap;

    @LoginRequired
    @ApiOperation(value = "分页查询", httpMethod = "POST")
    @PostMapping("/pageQuery")
    public CommonResponse<PageInfoVo<BetRecordVo>> pageQuery(@RequestBody PageQueryBetRecordRequest request) {
        request.validate();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        BetRecordQuery query = BetRecordConverter.requestToQuery(request, "createTime");
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                BetRecordConverter.doToVoList(betRecordService.list(query, gameType)),
                0L));
    }

    @LoginRequired
    @ApiOperation(value = "查询详情", httpMethod = "GET")
    @GetMapping("/get/{id}")
    public CommonResponse<BetRecordVo> get(@PathVariable("id") String id) {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(NumberUtil.isLong(id), ErrorCode.ERR_PARAM);
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        BetRecordDo betRecordDo = betRecordService.get(Long.valueOf(id), userInfo.getPlayerId(), gameType);
        BetRecordVo betRecordVo = BetRecordConverter.doToVo(betRecordDo);
        AssertUtil.assertNotNull(betRecordVo, ErrorCode.ERR_PARAM);
        betRecordVo.setGameRecordList(getRecordManager(gameType).listGameRecordVo(Long.parseLong(id), GameTypeEnum.valueOfId(userInfo.getGameId())));
        return CommonResponse.build(betRecordVo);
    }

    private AbstractRecordManager getRecordManager(GameTypeEnum gameType) {
        AbstractRecordManager recordManager = recordManagerMap.get(RECORD_MANAGER_MAP.get(gameType));
        AssertUtil.assertNotNull(recordManager, ErrorCode.ERR_INTER);
        return recordManager;
    }
}
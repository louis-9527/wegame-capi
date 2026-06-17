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
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.ReplayBetResultVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("betRecord")
@Api(value = "投注记录模块", tags = {"投注记录模块"})
public class BetRecordController {

    public static final Map<GameTypeEnum, String> BET_RECORD_MANAGER_NAME_MAP = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingBetRecordManager")
            .put(GameTypeEnum.MALAKAS, "malakasBetRecordManager")
            .put(GameTypeEnum.IBONG_ADARNA, "ibongAdarnaBetRecordManager")
            .put(GameTypeEnum.BAKUNAWA, "bakunawaBetRecordManager")
            .put(GameTypeEnum.LAM_ANG, "lamangBetRecordManager")
            .put(GameTypeEnum.DIWATA_MAYARI, "diwataMayariBetRecordManager")
            .put(GameTypeEnum.KAPRE, "kapreBetRecordManager")
            .put(GameTypeEnum.MANGKUKULAM, "mangkukulamBetRecordManager")
            .put(GameTypeEnum.SIRENA, "sirenaBetRecordManager")
            .put(GameTypeEnum.ATLANTIS, "atlantisBetRecordManager")
            .put(GameTypeEnum.BAYANIHAN, "bayanihanBetRecordManager")
            .build();

    @Autowired
    BetRecordService betRecordService;
    @Autowired
    Map<String, AbstractBetRecordManager> betRecordManagerMap;

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
        betRecordVo.setGameRecordList(betRecordManager(gameType).listGameRecordVo(Long.parseLong(id), gameType));
        return CommonResponse.build(betRecordVo);
    }

    @LoginRequired
    @ApiOperation(value = "查询最后一条详情", httpMethod = "GET")
    @GetMapping("/getLastGameRecord")
    public CommonResponse<BetRecordVo> lastGameRecord() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        long oneMonthAgoMillis = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli();
        BetRecordQuery betRecordQuery = BetRecordQuery.builder()
                .playerId(userInfo.getPlayerId())
                .startCreateTime(oneMonthAgoMillis)
                .orderByClause("createTime")
                .limit(1).build();
        BetRecordDo lastOneGameRecord = betRecordManager(gameType).findLastOneGameRecord(betRecordQuery, gameType);
        if (lastOneGameRecord == null) {
            return CommonResponse.build(null);
        }
        BetRecordVo betRecordVo = BetRecordConverter.doToVo(lastOneGameRecord);
        AssertUtil.assertNotNull(betRecordVo, ErrorCode.ERR_PARAM);
        betRecordVo.setGameRecordList(lastOneGameRecord.getGameRecordList());
        return CommonResponse.build(betRecordVo);
    }

    @LoginRequired
    @ApiOperation(value = "获取回放结果", httpMethod = "GET")
    @GetMapping("/get/replay/result/{id}")
    public CommonResponse<ReplayBetResultVo> getReplayResult(@PathVariable("id") String id) {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(NumberUtil.isLong(id), ErrorCode.ERR_PARAM);
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        return CommonResponse.build(betRecordManager(gameType).getReplayResult(userInfo, Long.valueOf(id), gameType));
    }

    private AbstractBetRecordManager betRecordManager(GameTypeEnum gameType) {
        AbstractBetRecordManager recordManager = betRecordManagerMap.get(BET_RECORD_MANAGER_NAME_MAP.get(gameType));
        AssertUtil.assertNotNull(recordManager, ErrorCode.ERR_INTER);
        return recordManager;
    }
}
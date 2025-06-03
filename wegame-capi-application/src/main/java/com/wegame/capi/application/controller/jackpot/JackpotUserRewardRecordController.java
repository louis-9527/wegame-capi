package com.wegame.capi.application.controller.jackpot;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotUserRewardRecordRequest;
import com.wegame.capi.client.domain.vo.jackpot.JackpotUserRewardRecordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordTypesQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.shade.org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter.*;

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

    public static final Map<GameTypeEnum, String> JACKPOT_MANAGER_NAME_MAP = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.LAM_ANG, "lamangJackpotManager")
            .build();

    @Autowired
    Map<String, AbstractJackpotManager> jackpotManagerMap;

    @LoginRequired
    @ApiOperation(value = "查询最高中奖记录", httpMethod = "GET")
    @GetMapping("/get/highest/user/reward/record")
    public CommonResponse<JackpotUserRewardRecordVo> getHighestUserRewardRecord() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        return build(doToVo(jackpotManager(gameType).getHighestUserRewardRecord()));
    }

    @LoginRequired
    @ApiOperation(value = "查询：根据投注记录ID & 游戏记录ID", httpMethod = "GET")
    @GetMapping("/get/{betRecordId}/{gameRecordId}")
    public CommonResponse<JackpotUserRewardRecordVo> get(@PathVariable String betRecordId, @PathVariable String gameRecordId) {
        AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(gameRecordId, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(NumberUtils.isNumber(betRecordId), ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(NumberUtils.isNumber(gameRecordId), ErrorCode.ERR_PARAM);
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        return build(doToVo(jackpotManager(gameType).getUserRewardRecord(JackpotUserRewardRecordQuery.builder()
                .betRecordId(Long.valueOf(betRecordId))
                .gameRecordId(Long.valueOf(gameRecordId))
                .build())));
    }

    @LoginRequired
    @ApiOperation(value = "根据jackpot奖池类型列表分页查询", httpMethod = "POST")
    @PostMapping("/pageQuery")
    public CommonResponse<PageInfoVo<JackpotUserRewardRecordVo>> pageQuery(@RequestBody PageQueryJackpotUserRewardRecordRequest request) {
        request.validate();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordQuery query = requestToQuery(request, "createTime");
        return build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                doToVoList(jackpotManager(gameType).listUserRewardRecord(query)),
                0L));
    }

    private AbstractJackpotManager jackpotManager(GameTypeEnum gameType) {
        AbstractJackpotManager jackpotManager = jackpotManagerMap.get(JACKPOT_MANAGER_NAME_MAP.get(gameType));
        AssertUtil.assertNotNull(jackpotManager, ErrorCode.ERR_INTER);
        return jackpotManager;
    }
}
package com.wegame.capi.application.controller.jackpot;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.Validate;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotRewardPoolRequest;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolSummarizeVo;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * jackpot奖池模块
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("jackpotRewardPool")
@Api(value = "jackpot奖池模块", tags = {"jackpot奖池模块"})
public class JackpotRewardPoolController {

    public static final Map<GameTypeEnum, String> JACKPOT_MANAGER_NAME_MAP = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.LAM_ANG, "lamangJackpotManager")
            .build();

    @Autowired
    Map<String, AbstractJackpotManager> jackpotManagerMap;


    @ApiOperation(value = "获取奖池信息", httpMethod = "GET")
    @GetMapping("/poolSummarize")
    public CommonResponse<JackpotRewardPoolSummarizeVo> getPoolSummarize() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        return CommonResponse.build(JackpotRewardPoolConverter.doToVo(jackpotManager(gameType).getPoolSummarize(gameType)));
    }

    ///////////////  above auto generated ///////////////

    private AbstractJackpotManager jackpotManager(GameTypeEnum gameType) {
        AbstractJackpotManager jackpotManager = jackpotManagerMap.get(JACKPOT_MANAGER_NAME_MAP.get(gameType));
        AssertUtil.assertNotNull(jackpotManager, ErrorCode.ERR_INTER);
        return jackpotManager;
    }
}
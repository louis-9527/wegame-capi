package com.wegame.capi.application.controller.jackpot;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter.doToVoList;

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

    public static final Map<GameTypeEnum, String> JACKPOT_MANAGER_NAME_MAP = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.LAM_ANG, "lamangJackpotManager")
            .build();

    @Autowired
    Map<String, AbstractJackpotManager> jackpotManagerMap;

    @LoginRequired
    @ApiOperation(value = "查询列表", httpMethod = "GET")
    @GetMapping("/list")
    public CommonResponse<List<JackpotRewardPoolVo>> list() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        return build(doToVoList(jackpotManager(gameType).listJackpotRewardPool()));
    }

    private AbstractJackpotManager jackpotManager(GameTypeEnum gameType) {
        AbstractJackpotManager jackpotManager = jackpotManagerMap.get(JACKPOT_MANAGER_NAME_MAP.get(gameType));
        AssertUtil.assertNotNull(jackpotManager, ErrorCode.ERR_INTER);
        return jackpotManager;
    }
}
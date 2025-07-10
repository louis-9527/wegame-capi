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
 * 抽象jackpot奖池
 *
 * @author generate
 */
public abstract class AbstractJackpotController {

    public static final Map<GameTypeEnum, String> JACKPOT_MANAGER_NAME_MAP = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.LAM_ANG, "lamangJackpotManager")
            .build();

    @Autowired
    Map<String, AbstractJackpotManager> jackpotManagerMap;

    public AbstractJackpotManager jackpotManager(GameTypeEnum gameType) {
        AbstractJackpotManager jackpotManager = jackpotManagerMap.get(JACKPOT_MANAGER_NAME_MAP.get(gameType));
        AssertUtil.assertNotNull(jackpotManager, ErrorCode.ERR_INTER);
        return jackpotManager;
    }
}
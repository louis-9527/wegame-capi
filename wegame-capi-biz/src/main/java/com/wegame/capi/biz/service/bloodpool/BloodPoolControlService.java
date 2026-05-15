package com.wegame.capi.biz.service.bloodpool;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.service.SingleWalletConfigService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.infrastructure.helper.LarkHelper;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.wegame.capi.api.SingleWalletConfigApi.*;

/**
 * 游戏模版Play(血池控制)
 */
@Component
@Slf4j
public class BloodPoolControlService {

    @Autowired
    GameApiProperty gameApiProperty;
    @Autowired
    LarkHelper larkHelper;
    @Autowired
    SingleWalletConfigService singleWalletConfigService;

    public GetPoolConfigRes.BloodPoolConfig matchBloodPoolConfig(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 1、获取血池控制配置
        boolean supportDos = gameApiProperty.supportDosChannelIdSet().contains(userInfo.getChannelId());
        if (!supportDos) {
            // 不支持血池控制
            log.warn("not support blood pool control, pid: {}, channelId: {}", userInfo.getPId(), userInfo.getChannelId());
            return null;
        }
        List<GetPoolConfigRes.BloodPoolConfig> list = singleWalletConfigService.getBloodPoolConfigWithCache(userInfo.getPId(), userInfo.getChannelId(), gamePlayDo.getGameType());
        return matchConfig(userInfo, list, betDo, gamePlayDo);
    }

    private GetPoolConfigRes.BloodPoolConfig matchConfig(UserInfo userInfo, List<GetPoolConfigRes.BloodPoolConfig> configList, BetDo betDo, GamePlayDo gamePlayDo) {
        if (CollectionUtils.isEmpty(configList)) {
            larkHelper.sendWarningMsg(String.format(EMPTY_LIST_WARNING_MSG_FORMAT, betDo.getGameId()));
            return null;
        }
        List<GetPoolConfigRes.BloodPoolConfig> matchConfigList = configList.stream().filter(item -> item.match(userInfo.getPId(), userInfo.getChannelId(), betDo.getBetAmount()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchConfigList)) {
            larkHelper.sendWarningMsg(String.format(NO_MATCH_WARNING_MSG_FORMAT, userInfo.getPId(), userInfo.getChannelId(), betDo.getBetType(), betDo.getGameId()));
            return null;
        } else if (matchConfigList.size() > 1) {
            larkHelper.sendWarningMsg(String.format(MULTI_MATCH_WARNING_MSG_FORMAT, userInfo.getPId(), userInfo.getChannelId(), betDo.getBetType(), betDo.getGameId()));
            return null;
        } else {
            return matchConfigList.get(0);
        }
    }
}

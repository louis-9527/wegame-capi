package com.wegame.capi.biz.service.bloodpool;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.service.SingleWalletConfigService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import com.wegame.capi.infrastructure.helper.LarkHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.api.SingleWalletConfigApi.*;

/**
 * 血池控制服务
 */
@Component
@Slf4j
public class BloodPoolControlService {
    private static final List<GameTypeEnum> SUPPORT_GAME_LIST = Arrays.asList(GameTypeEnum.DIWATA_MAYARI);
    @Autowired
    LarkHelper larkHelper;
    @Autowired
    GameApiProperty gameApiProperty;
    @Autowired
    SingleWalletConfigService singleWalletConfigService;

    public GetPoolConfigRes.BloodPoolConfig matchBloodPoolConfig(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 渠道判断是否支持
        if (!gameApiProperty.supportDosChannelIdSet().contains(userInfo.getChannelId())) {
            // 非dos不支持血池控制
            log.warn("not support blood pool control, pid: {}, channelId: {}", userInfo.getPId(), userInfo.getChannelId());
            return null;
        }
        List<GetPoolConfigRes.BloodPoolConfig> configList = singleWalletConfigService.getBloodPoolConfigWithCache(userInfo.getPId(), userInfo.getChannelId(), gamePlayDo.getGameType());
        return matchConfig(userInfo, configList, betDo, gamePlayDo);
    }

    public boolean judgeEnableBloodPoolControl(BetDo betDo) {
        if (!SUPPORT_GAME_LIST.contains(betDo.getGameType())) {
            return false;
        }
        switch (betDo.getBetPromotionType()) {
            case NONE:
            case STAR_CARD:
            case STAR_CARD_IN_FREE:
            case FREE_SPIN:
                return true;
            case OPERATION_EVENT:
                return false;
        }
        return false;
    }

    private GetPoolConfigRes.BloodPoolConfig matchConfig(UserInfo userInfo, List<GetPoolConfigRes.BloodPoolConfig> configList, BetDo betDo, GamePlayDo gamePlayDo) {
        if (CollectionUtils.isEmpty(configList)) {
            // 未获取到配置，告警
            larkHelper.sendWarningMsg(String.format(EMPTY_LIST_WARNING_MSG_FORMAT, betDo.getGameId()));
            return null;
        }
        List<GetPoolConfigRes.BloodPoolConfig> matchConfigList = configList.stream().filter(item -> item.match(betDo.getBetAmount(), betDo.getBetType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchConfigList)) {
            // 未匹配到，告警
            larkHelper.sendWarningMsg(String.format(NO_MATCH_WARNING_MSG_FORMAT, userInfo.getPId(), userInfo.getChannelId(), betDo.getBetType(), betDo.getGameId()));
            return null;
        } else if (matchConfigList.size() > 1) {
            // 匹配到多条，告警
            larkHelper.sendWarningMsg(String.format(MULTI_MATCH_WARNING_MSG_FORMAT, userInfo.getPId(), userInfo.getChannelId(), betDo.getBetType(), betDo.getGameId()));
            return null;
        } else {
            return matchConfigList.get(0);
        }
    }
}

package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.SingleWalletConfigApi;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * 单钱包服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletConfigService {

    @Autowired
    GameApiProperty gameApiProperty;
    @Autowired
    SingleWalletConfigApi singleWalletConfigApi;

    // todo 缓存控制： 刷新 等
    public List<GetPoolConfigRes.BloodPoolConfig> getConfig(String pid, String channelId, GameTypeEnum gameType) {
        AssertUtil.assertStrNotEmpty(pid, ErrorCode.ERR_INTER);
        AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        if (gameApiProperty.supportDosChannelIdSet().contains(channelId)) {
            GetPoolConfigRes res = singleWalletConfigApi.getPoolConfigList(pid, channelId, gameType.getId());
            return Objects.nonNull(res) ? res.getPools() : null;
        } else {
            return null;
        }
    }
}
package com.wegame.capi.biz.service;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.SingleWalletConfigApi;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import com.wegame.capi.infrastructure.helper.LarkHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.api.SingleWalletConfigApi.EXCEPTION_WARNING_MSG_FORMAT;
import static com.wegame.capi.common.constants.WarningConstants.EXCEPTION_WARNING_MSG_FORMAT;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.defaultThreadPoolExecutor;


/**
 * 单钱包配置服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletConfigService {

    @Value("${lark.warningRobot}")
    public String warningRobot;
    @Autowired
    LarkHelper larkHelper;
    @Autowired
    SingleWalletConfigApi singleWalletConfigApi;

    public List<GetPoolConfigRes.BloodPoolConfig> getBloodPoolConfigNoCache(String pid, String channelId, GameTypeEnum gameType) {
        AssertUtil.assertStrNotEmpty(pid, ErrorCode.ERR_INTER);
        AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        try {
            GetPoolConfigRes res = singleWalletConfigApi.getPoolConfigList(pid, channelId, gameType.getId());
            return Objects.nonNull(res) ? res.getPools() : null;
        } catch (Exception e) {
            larkHelper.sendWarningMsg(String.format(EXCEPTION_WARNING_MSG_FORMAT, pid, channelId, gameType.getId()));
            return null;
        }
    }

    @Cacheable(value = CacheConstants.BLOOD_POOL_CONFIG, key = "#pid + '_' + #channelId + '_' + #gameType.name")
    public List<GetPoolConfigRes.BloodPoolConfig> getBloodPoolConfigWithCache(String pid, String channelId, GameTypeEnum gameType) {
        return getBloodPoolConfigNoCache(pid, channelId, gameType);
    }

    @Async(defaultThreadPoolExecutor)
    @Cacheable(value = CacheConstants.BLOOD_POOL_CONFIG, key = "#pid + '_' + #channelId + '_' + #gameType.name")
    public List<GetPoolConfigRes.BloodPoolConfig> asyncPutBloodPoolConfigCache(String pid, String channelId, GameTypeEnum gameType, List<GetPoolConfigRes.BloodPoolConfig> configList) {
        return configList;
    }
}
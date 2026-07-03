package com.wegame.capi.biz.service;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.SingleWalletConfigApi;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.helper.LarkHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.common.constants.WarningConstants.EXCEPTION_WARNING_MSG_FORMAT;


/**
 * 单钱包配置服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class SingleWalletConfigService {

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
            larkHelper.sendWarningMsg(String.format(EXCEPTION_WARNING_MSG_FORMAT, pid, channelId, gameType.getId(), ThreadMdcUtil.getTraceId()));
            return null;
        }
    }

    @Cacheable(value = CacheConstants.BLOOD_POOL_CONFIG, key = "#pid + '_' + #channelId + '_' + #gameType.name")
    public List<GetPoolConfigRes.BloodPoolConfig> getBloodPoolConfigWithCache(String pid, String channelId, GameTypeEnum gameType) {
        return getBloodPoolConfigNoCache(pid, channelId, gameType);
    }

    @CachePut(value = CacheConstants.BLOOD_POOL_CONFIG, key = "#pid + '_' + #channelId + '_' + #gameType.name")
    public List<GetPoolConfigRes.BloodPoolConfig> putBloodPoolConfigCache(String pid, String channelId, GameTypeEnum gameType, List<GetPoolConfigRes.BloodPoolConfig> configList) {
        log.info("putBloodPoolConfigCache, pid: {}, channelId: {}, gameType: {}", pid, channelId, gameType.getName());
        return configList;
    }
}
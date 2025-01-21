package com.wegame.capi.infrastructure.cache;

import com.wegame.base.cache.AbstractMultiCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.CacheConstants.REWARD_INFO;

/**
 * @author apple
 * 中大奖缓存
 */
@Component(REWARD_INFO)
public class RewardCache extends AbstractMultiCache {

    public RewardCache() {
        super(CommonCacheEnvEnum.REWARD_INFO.cacheKeyPrefixName(), 180, 60, 10);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.REWARD_INFO.cacheName();
    }
}

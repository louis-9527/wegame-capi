package com.wegame.capi.infrastructure.cache;

import com.wegame.base.cache.AbstractMultiCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.CacheConstants.JACKPOT_REWARD_POOL;

@Component(JACKPOT_REWARD_POOL)
public class JackpotRewardPoolCache extends AbstractMultiCache {

    public JackpotRewardPoolCache() {
        super(CommonCacheEnvEnum.JACKPOT_REWARD_POOL.cacheKeyPrefixName(),  30, 5, 20);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.JACKPOT_REWARD_POOL.cacheName();
    }
}

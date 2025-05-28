package com.wegame.capi.infrastructure.cache;

import com.wegame.base.cache.AbstractCache;
import com.wegame.base.cache.AbstractMultiCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.CacheConstants.JACKPOT_REWARD_POOL_CONFIG;
import static com.wegame.base.CacheConstants.JACKPOT_USER_REWARD_RECORD_CONFIG;

@Component(JACKPOT_USER_REWARD_RECORD_CONFIG)
public class JackpotUserRewardRecordCache extends AbstractCache {
    public JackpotUserRewardRecordCache() {
        super(CommonCacheEnvEnum.JACKPOT_USER_REWARD_RECORD.cacheKeyPrefixName(),  60);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.JACKPOT_USER_REWARD_RECORD.cacheName();
    }
}

package com.wegame.capi.infrastructure.cache;

import com.wegame.base.cache.AbstractCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.CacheConstants.JACKPOT_USER_REWARD_RECORD_LIST;

@Component(JACKPOT_USER_REWARD_RECORD_LIST)
public class JackpotUserRewardRecordListCache extends AbstractCache {
    public JackpotUserRewardRecordListCache() {
        super(CommonCacheEnvEnum.JACKPOT_USER_REWARD_RECORD_LIST.cacheKeyPrefixName(),  60);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.JACKPOT_USER_REWARD_RECORD_LIST.cacheName();
    }
}

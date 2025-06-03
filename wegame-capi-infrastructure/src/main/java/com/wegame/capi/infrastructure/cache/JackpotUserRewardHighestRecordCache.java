package com.wegame.capi.infrastructure.cache;

import com.wegame.base.cache.AbstractCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.CacheConstants.*;

@Component(HIGHEST_JACKPOT_USER_REWARD_RECORD)
public class JackpotUserRewardHighestRecordCache extends AbstractCache {
    public JackpotUserRewardHighestRecordCache() {
        super(CommonCacheEnvEnum.HIGHEST_JACKPOT_USER_REWARD_RECORD.cacheKeyPrefixName(),  60);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.HIGHEST_JACKPOT_USER_REWARD_RECORD.cacheName();
    }
}

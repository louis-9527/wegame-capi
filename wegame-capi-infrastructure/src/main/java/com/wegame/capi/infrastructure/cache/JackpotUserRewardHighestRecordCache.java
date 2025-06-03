package com.wegame.capi.infrastructure.cache;

import com.wegame.base.cache.AbstractCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.CacheConstants.*;

@Component(JACKPOT_USER_REWARD_HIGHEST_RECORD)
public class JackpotUserRewardHighestRecordCache extends AbstractCache {
    public JackpotUserRewardHighestRecordCache() {
        super(CommonCacheEnvEnum.JACKPOT_USER_REWARD_HIGHEST_RECORD.cacheKeyPrefixName(),  60);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.JACKPOT_USER_REWARD_HIGHEST_RECORD.cacheName();
    }
}

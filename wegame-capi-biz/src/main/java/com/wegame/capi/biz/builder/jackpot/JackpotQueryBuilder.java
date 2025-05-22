package com.wegame.capi.biz.builder.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class JackpotQueryBuilder {
    public static Update buildUpdate(JackpotRewardPoolPo rewardPool) {
        if (rewardPool == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(rewardPool.getAmount())) {
            update.inc("amount", rewardPool.getAmount());
        }
        if (Objects.nonNull(rewardPool.getUpdater())) {
            update.set("updater", rewardPool.getUpdater());
        }
        if (Objects.nonNull(rewardPool.getUpdateTime())) {
            update.set("updateTime", rewardPool.getUpdateTime());
        } else {
            update.set("updateTime", DateUtil.getCurrentMilli());
        }
        return update;
    }
}

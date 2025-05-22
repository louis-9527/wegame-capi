package com.wegame.capi.biz.builder.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class JackpotQueryBuilder {
    public static Update buildUpdate(JackpotRewardPoolDo rewardPoolDo) {
        if (rewardPoolDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(rewardPoolDo.getAmount())) {
            update.inc("amount", rewardPoolDo.getAmount());
        }
        if (Objects.nonNull(rewardPoolDo.getUpdater())) {
            update.set("updater", rewardPoolDo.getUpdater());
        }
        if (Objects.nonNull(rewardPoolDo.getUpdateTime())) {
            update.set("updateTime", rewardPoolDo.getUpdateTime());
        } else {
            update.set("updateTime", DateUtil.getCurrentMilli());
        }
        return update;
    }
}

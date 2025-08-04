package com.wegame.capi.biz.builder.jackpot;

import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;

public class JackpotUserRewardPoolInputRecordQueryBuilder {

    public static Query buildDeleteQuery(long lgCreateTime, JackpotAccFlagEnum jackpotAccFlag) {
        Criteria criteria = Criteria.where("createTime").lt(lgCreateTime)
                .and("accFlag").is(jackpotAccFlag.getCode());
        return new Query(criteria);
    }

}

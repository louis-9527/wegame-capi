package com.wegame.capi.biz.service.metric;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 埋点服务
 *
 * @author genereate
 */
@Slf4j
@Component
public class UserMetricHelperService {

    @Autowired
    BetRecordService betRecordService;

    public BetRecordDo getLastBetRecord(UserInfo userInfo) {
        try {
            return betRecordService.get(BetRecordQuery.builder()
                            .pId(userInfo.getPId())
                            .channelId(userInfo.getChannelId())
                            .playerId(userInfo.getPlayerId())
                            .status(BetStatusEnum.SUCCESS.getCode())
                            .orderByClause("createTime")
                            .limit(1)
                            .direction(Sort.Direction.DESC)
                            .build(),
                    GameTypeEnum.valueOfId(userInfo.getGameId()));
        } catch (Exception e) {
            log.error("getLastBetRecord error, userInfo: {}", userInfo, e);
            return null;
        }
    }

    public Boolean yesterdayHasBet(UserInfo userInfo, long startCreateTime, long endCreateTime) {
        try {
            BetRecordDo betRecordDo = betRecordService.get(BetRecordQuery.builder()
                            .pId(userInfo.getPId())
                            .channelId(userInfo.getChannelId())
                            .playerId(userInfo.getPlayerId())
                            .status(BetStatusEnum.SUCCESS.getCode())
                            .startCreateTime(startCreateTime)
                            .endCreateTime(endCreateTime)
                            .orderByClause("createTime")
                            .limit(1)
                            .direction(Sort.Direction.DESC)
                            .build(),
                    GameTypeEnum.valueOfId(userInfo.getGameId()));
            return Objects.nonNull(betRecordDo);
        } catch (Exception e) {
            log.error("yesterdayHasBet error, userInfo: {}", userInfo, e);
            return null;
        }
    }
}
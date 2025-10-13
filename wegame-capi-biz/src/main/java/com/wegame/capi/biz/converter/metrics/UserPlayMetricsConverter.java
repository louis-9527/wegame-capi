package com.wegame.capi.biz.converter.metrics;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsPo;
import java.util.Objects;

/**
 * UserPlayMetricsConverter
 *
 * @author generate
 */
public class UserPlayMetricsConverter {

    public static UserPlayMetricsDo poToDo(UserPlayMetricsPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        UserPlayMetricsDo userPlayMetricsDo = new UserPlayMetricsDo();
        userPlayMetricsDo.setId(po.getId());
        userPlayMetricsDo.setPId(po.getPId());
        userPlayMetricsDo.setChannelId(po.getChannelId());
        userPlayMetricsDo.setGameId(po.getGameId());
        userPlayMetricsDo.setPlayerId(po.getPlayerId());
        userPlayMetricsDo.setPlayerName(po.getPlayerName());
        userPlayMetricsDo.setTotalGameCount(po.getTotalGameCount());
        userPlayMetricsDo.setTotalBetAmount(po.getTotalBetAmount());
        userPlayMetricsDo.setTotalPayoutAmount(po.getTotalPayoutAmount());
        userPlayMetricsDo.setContinuousLostCount(po.getContinuousLostCount());
        userPlayMetricsDo.setLastBetTime(po.getLastBetTime());
        userPlayMetricsDo.setContinuousNoFreeSpinCount(po.getContinuousNoFreeSpinCount());
        if (CollectionUtil.isNotEmpty(po.getSpecialRuleList())){
            userPlayMetricsDo.setSpecialRuleList(SpecialRuleConverter.poToDoList(po.getSpecialRuleList()));
        }
        userPlayMetricsDo.setExtend(po.getExtend());
        userPlayMetricsDo.setVersion(po.getVersion());
        userPlayMetricsDo.setCreator(po.getCreator());
        userPlayMetricsDo.setCreateTime(po.getCreateTime());
        userPlayMetricsDo.setUpdater(po.getUpdater());
        userPlayMetricsDo.setUpdateTime(po.getUpdateTime());
        return userPlayMetricsDo;
    }

    public static UserPlayMetricsPo doToPo(UserPlayMetricsDo aDo) {
        if (Objects.isNull(aDo)) {
            return null;
        }
        UserPlayMetricsPo po = new UserPlayMetricsPo();
        po.setId(aDo.getId());
        po.setPId(aDo.getPId());
        po.setChannelId(aDo.getChannelId());
        po.setGameId(aDo.getGameId());
        po.setPlayerId(aDo.getPlayerId());
        po.setPlayerName(aDo.getPlayerName());
        po.setTotalGameCount(aDo.getTotalGameCount());
        po.setTotalBetAmount(aDo.getTotalBetAmount());
        po.setTotalPayoutAmount(aDo.getTotalPayoutAmount());
        po.setContinuousLostCount(aDo.getContinuousLostCount());
        po.setLastBetTime(aDo.getLastBetTime());
        po.setContinuousNoFreeSpinCount(aDo.getContinuousNoFreeSpinCount());
        if (CollectionUtil.isNotEmpty(aDo.getSpecialRuleList())){
            po.setSpecialRuleList(SpecialRuleConverter.doToPoList(aDo.getSpecialRuleList()));
        }
        po.setExtend(aDo.getExtend());
        po.setVersion(aDo.getVersion());
        po.setCreator(aDo.getCreator());
        po.setCreateTime(aDo.getCreateTime());
        po.setUpdater(aDo.getUpdater());
        po.setUpdateTime(aDo.getUpdateTime());
        return po;
    }

    ///////////////  above auto generated ///////////////

}
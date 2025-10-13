package com.wegame.capi.biz.converter.metrics;

import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.dao.repository.domain.metrics.GameMetricsPo;

import java.util.Objects;

/**
 * UserPlayMetricsConverter
 *
 * @author generate
 */
public class GameMetricsConverter {

    public static GameMetricsDo poToDo(GameMetricsPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        GameMetricsDo gameMetricsDo = new GameMetricsDo();
        gameMetricsDo.setId(po.getId());
        gameMetricsDo.setPId(po.getPId());
        gameMetricsDo.setChannelId(po.getChannelId());
        gameMetricsDo.setGameId(po.getGameId());
        gameMetricsDo.setCurrentRtp(po.getCurrentRtp());
        gameMetricsDo.setTotalBetAmount(po.getTotalBetAmount());
        gameMetricsDo.setTotalPayoutAmount(po.getTotalPayoutAmount());
        gameMetricsDo.setExtend(po.getExtend());
        gameMetricsDo.setVersion(po.getVersion());
        gameMetricsDo.setCreator(po.getCreator());
        gameMetricsDo.setCreateTime(po.getCreateTime());
        gameMetricsDo.setUpdater(po.getUpdater());
        gameMetricsDo.setUpdateTime(po.getUpdateTime());
        return gameMetricsDo;
    }

    public static GameMetricsPo doToPo(GameMetricsDo aDo) {
        if (Objects.isNull(aDo)) {
            return null;
        }
        GameMetricsPo po = new GameMetricsPo();
        po.setId(aDo.getId());
        po.setPId(aDo.getPId());
        po.setChannelId(aDo.getChannelId());
        po.setGameId(aDo.getGameId());
        po.setCurrentRtp(aDo.getCurrentRtp());
        po.setTotalBetAmount(aDo.getTotalBetAmount());
        po.setTotalPayoutAmount(aDo.getTotalPayoutAmount());
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
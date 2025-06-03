package com.wegame.capi.biz.manager.jackpot;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolInputRecordService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * jackpot manager
 */
@Slf4j
@Component
public abstract class AbstractJackpotManager {

    @Autowired
    JackpotRewardPoolInputRecordService jackpotRewardPoolInputRecordService;
    @Autowired
    JackpotRewardPoolService jackpotRewardPoolService;
    @Autowired
    JackpotUserRewardRecordService jackpotUserRewardRecordService;

    public abstract JackpotTypeEnum jackpotType();

    /**
     * 获取奖池列表
     */
    public List<JackpotRewardPoolDo> listJackpotRewardPool() {
        return jackpotRewardPoolService.listAllWithCache(jackpotType());
    }

    /**
     * 获取用户最高中奖记录
     */
    public JackpotUserRewardRecordDo getHighestUserRewardRecord() {
        return jackpotUserRewardRecordService.getHighestUserRewardRecordWithCache(jackpotType());
    }

    /**
     * 获取用户中奖记录
     */
    public JackpotUserRewardRecordDo getUserRewardRecord(JackpotUserRewardRecordQuery query) {
        return jackpotUserRewardRecordService.selectOne(query, jackpotType());
    }

    /**
     * 查询用户中奖列表
     */
    public abstract List<JackpotUserRewardRecordDo> listUserRewardRecord(JackpotUserRewardRecordQuery query);

    /**
     * 新增奖池记录
     */
    public abstract void addRewardPoolInputRecord(JackpotRewardPoolInputRecordDo jackpotRewardPoolInputRecordDo);

    /**
     * 批量新增奖池记录
     */
    public abstract void batchAddRewardPoolInputRecord(List<JackpotRewardPoolInputRecordDo> rewardPoolInputRecordDos);

    /**
     * 派奖,返回派奖记录ID
     */
    public abstract Long winReward(JackpotUserRewardRecordDo jackpotUserRewardRecordDo);


}

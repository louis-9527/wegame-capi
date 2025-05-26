package com.wegame.capi.biz.manager.jackpot;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * jackpot manager
 */
@Slf4j
@Component
public abstract class AbstractJackpotManager {

    /**
     * 获取奖池列表
     */
    public abstract List<JackpotRewardPoolDo> listJackpotRewardPool();

    /**
     * 获取历史最高中奖记录
     */
    public abstract JackpotUserRewardRecordDo getHighestUserRewardRecord();

    /**
     * 获取中奖记录
     */
    public abstract JackpotUserRewardRecordDo getUserRewardRecord(JackpotUserRewardRecordQuery query);

    public abstract List<JackpotUserRewardRecordDo> listUserRewardRecord(JackpotUserRewardRecordQuery query);

    /**
     * 批量新增,返回失败的ID列表
     * @param rewardPoolInputRecordDos
     * @return
     */
    public abstract void batchAddUserRewardRecord(List<JackpotRewardPoolInputRecordDo> rewardPoolInputRecordDos);

    /**
     * 派奖,返回派奖记录ID
     * @param jackpotUserRewardRecordDo
     */
    public abstract Long winReward(JackpotUserRewardRecordDo jackpotUserRewardRecordDo);


}

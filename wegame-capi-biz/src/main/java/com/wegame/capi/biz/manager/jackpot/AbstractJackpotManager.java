package com.wegame.capi.biz.manager.jackpot;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolSummarizeDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
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
    //todo 走缓存
    public abstract JackpotRewardPoolSummarizeDo getPoolSummarize(GameTypeEnum gameType);

    public abstract List<JackpotUserRewardRecordDo> list(JackpotUserRewardRecordQuery query, GameTypeEnum gameType);

    public abstract JackpotUserRewardRecordDo getMaxReward(GameTypeEnum gameType);

    public abstract JackpotUserRewardRecordDo getByGameRecordId(Long gameRecordId, GameTypeEnum gameType);

    /**
     * 批量新增,返回失败的ID列表
     * @param rewardPoolInputRecordDos
     * @return
     */
    public abstract void batchAddJackpotRewardRecord (List<JackpotRewardPoolInputRecordDo> rewardPoolInputRecordDos);

}

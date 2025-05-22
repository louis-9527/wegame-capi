package com.wegame.capi.biz.manager.jackpot;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolSummarizeDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component("lamangJackpotManager")
public class LamangJackpotManager extends AbstractJackpotManager {

    @Override
    public JackpotRewardPoolSummarizeDo getPoolSummarize(GameTypeEnum gameType) {
        return null;
    }

    @Override
    public List<JackpotUserRewardRecordDo> list(JackpotUserRewardRecordQuery query, GameTypeEnum gameType) {
        return Collections.emptyList();
    }

    @Override
    public JackpotUserRewardRecordDo getMaxReward(GameTypeEnum gameType) {
        return null;
    }

    @Override
    public JackpotUserRewardRecordDo getByGameRecordId(Long gameRecordId, GameTypeEnum gameType) {
        return null;
    }
}

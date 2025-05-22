package com.wegame.capi.biz.manager.jackpot;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolSummarizeDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolInputRecordService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wegame.capi.infrastructure.config.mongo.LamangMongoConfig.lamangTransactionManager;
import static com.wegame.capi.infrastructure.config.mongo.MariamakilingMongoConfig.mariamakilingTransactionManager;

@Slf4j
@Component("lamangJackpotManager")
public class LamangJackpotManager extends AbstractJackpotManager {
    @Autowired
    private JackpotRewardPoolInputRecordService jackpotRewardPoolInputRecordService;
    @Autowired
    private JackpotRewardPoolService jackpotRewardPoolService;

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

    @Transactional(transactionManager = lamangTransactionManager)
    @Override
    public void batchAddJackpotRewardRecord(List<JackpotRewardPoolInputRecordDo> rewardPoolInputRecordDos) {
        if (CollectionUtils.isEmpty(rewardPoolInputRecordDos)) {
            return;
        }
        List<Long> failIds = jackpotRewardPoolInputRecordService.batchAdd(rewardPoolInputRecordDos, JackpotTypeEnum.valueOfGameType(GameTypeEnum.LAM_ANG));
        if (!failIds.isEmpty()) {
            rewardPoolInputRecordDos.removeIf(item -> failIds.contains(item.getId()));
        }
        // 按照奖池金额统计
        long grandAmount = 0;
        long majorAmount = 0;
        long minorAmount = 0;
        long miniAmount = 0;
        // 遍历列表并累加各个金额字段
        for (JackpotRewardPoolInputRecordDo record : rewardPoolInputRecordDos) {
            grandAmount += record.getGrandAmount() == null ? 0 : record.getGrandAmount();
            majorAmount += record.getMajorAmount() == null ? 0 : record.getMajorAmount();
            minorAmount += record.getMinorAmount() == null ? 0 : record.getMinorAmount();
            miniAmount += record.getMiniAmount() == null ? 0 : record.getMiniAmount();
        }

        // 组装奖池对象
        long currentMilli = DateUtil.getCurrentMilli();
        JackpotRewardPoolDo grandDo = JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.grand)
                .amount(grandAmount)
                .updateTime(currentMilli)
                .build();
        JackpotRewardPoolDo majorDo = JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.major)
                .amount(majorAmount)
                .updateTime(currentMilli)
                .build();
        JackpotRewardPoolDo minorDo = JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.minor)
                .amount(minorAmount)
                .updateTime(currentMilli)
                .build();
        JackpotRewardPoolDo miniDo = JackpotRewardPoolDo.builder()
                .type(JackpotRewardPoolTypeEnum.mini)
                .amount(miniAmount)
                .updateTime(currentMilli)
                .build();
        List<JackpotRewardPoolDo> rewardPoolDos = new ArrayList<>(4);
        rewardPoolDos.add(grandDo);
        rewardPoolDos.add(majorDo);
        rewardPoolDos.add(minorDo);
        rewardPoolDos.add(miniDo);

        jackpotRewardPoolService.batchUpdate(rewardPoolDos, JackpotTypeEnum.valueOfGameType(GameTypeEnum.LAM_ANG));
    }
}

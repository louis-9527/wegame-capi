package com.wegame.capi.biz.manager.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolInputRecordService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordTypesQuery;
import com.wegame.capi.infrastructure.config.mongo.LamangMongoConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.builder.jackpot.JackpotRewardPoolBuilder.buildList;
import static com.wegame.capi.client.enums.JackpotTypeEnum.LAM_ANG;

@Slf4j
@Component("lamangJackpotManager")
public class LamangJackpotManager extends AbstractJackpotManager {
    @Autowired
    JackpotRewardPoolInputRecordService jackpotRewardPoolInputRecordService;
    @Autowired
    JackpotRewardPoolService jackpotRewardPoolService;
    @Autowired
    JackpotUserRewardRecordService jackpotUserRewardRecordService;

    @Override
    public List<JackpotRewardPoolDo> listJackpotRewardPool() {
        return jackpotRewardPoolService.listAll(LAM_ANG);
    }

    @Override
    public JackpotUserRewardRecordDo getHighestUserRewardRecord() {
        return jackpotUserRewardRecordService.getHighestUserRewardRecord(LAM_ANG);
    }

    @Override
    public JackpotUserRewardRecordDo getUserRewardRecord(JackpotUserRewardRecordQuery query) {
        return jackpotUserRewardRecordService.selectOne(query, LAM_ANG);
    }

    @Override
    public List<JackpotUserRewardRecordDo> listUserRewardRecord(JackpotUserRewardRecordQuery query) {
        return jackpotUserRewardRecordService.list(query, LAM_ANG);
    }

    @Override
    public List<JackpotUserRewardRecordDo> listUserRewardRecordByTypes(JackpotUserRewardRecordTypesQuery query) {
        return jackpotUserRewardRecordService.listByTypes(query, LAM_ANG);
    }

    @Override
    @Transactional(transactionManager = LamangMongoConfig.lamangTransactionManager, rollbackFor = Exception.class)
    public void batchAddUserRewardRecord(List<JackpotRewardPoolInputRecordDo> recordList) {
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        jackpotRewardPoolInputRecordService.batchAdd(recordList, LAM_ANG);
        // 按照奖池金额统计
        long grandAmount = 0;
        long majorAmount = 0;
        long minorAmount = 0;
        long miniAmount = 0;
        // 遍历列表并累加各个金额字段
        for (JackpotRewardPoolInputRecordDo record : recordList) {
            grandAmount += record.getGrandAmount() == null ? 0 : record.getGrandAmount();
            majorAmount += record.getMajorAmount() == null ? 0 : record.getMajorAmount();
            minorAmount += record.getMinorAmount() == null ? 0 : record.getMinorAmount();
            miniAmount += record.getMiniAmount() == null ? 0 : record.getMiniAmount();
        }
        jackpotRewardPoolService.batchUpdate(buildList(grandAmount, majorAmount, minorAmount, miniAmount), LAM_ANG);
    }

    @Override
    @Transactional(transactionManager = LamangMongoConfig.lamangTransactionManager, rollbackFor = Exception.class)
    public void addUserRewardRecord(JackpotRewardPoolInputRecordDo rewardPoolInputRecordDo) {
        if (Objects.isNull(rewardPoolInputRecordDo)) {
            return;
        }
        jackpotRewardPoolInputRecordService.add(rewardPoolInputRecordDo, LAM_ANG);
        jackpotRewardPoolService.batchUpdate(buildList(rewardPoolInputRecordDo.getGrandAmount(), rewardPoolInputRecordDo.getMajorAmount(), rewardPoolInputRecordDo.getMinorAmount(), rewardPoolInputRecordDo.getMiniAmount()),
                LAM_ANG);
    }

    @Override
    @Transactional(transactionManager = LamangMongoConfig.lamangTransactionManager)
    public Long winReward(JackpotUserRewardRecordDo jackpotUserRewardRecordDo) {
        Long add = jackpotUserRewardRecordService.add(jackpotUserRewardRecordDo, LAM_ANG);
        JackpotRewardPoolDo buildUpdate = JackpotRewardPoolDo.builder().type(jackpotUserRewardRecordDo.getRewardPoolType())
                .amount(-jackpotUserRewardRecordDo.getRewardAmount()).build();
        boolean update = jackpotRewardPoolService.update(buildUpdate, LAM_ANG);
        AssertUtil.assertTrue(update, ErrorCode.ERR_INTER);
        return add;
    }
}

package com.wegame.capi.biz.manager.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wegame.capi.biz.builder.jackpot.JackpotRewardPoolBuilder.buildList;
import static com.wegame.capi.biz.config.mongo.LamangMongoConfig.lamangTransactionManager;

@Slf4j
@Component("lamangJackpotManager")
public class LamangJackpotManager extends AbstractJackpotManager {

    @Override
    public JackpotTypeEnum jackpotGameType() {
        return JackpotTypeEnum.LAM_ANG;
    }

    @Override
    public List<JackpotUserRewardRecordDo> listUserRewardRecord(JackpotUserRewardRecordQuery query) {
        return jackpotUserRewardRecordService.listWithCache(query, jackpotGameType());
    }

    @Override
    //    @Retryable(value = {UncategorizedMongoDbException.class, MongoCommandException.class},
    //            maxAttempts = 3, // 失败后，默认重试两次
    //            backoff = @Backoff(delay = 500, multiplier = 2))
    @Transactional(transactionManager = lamangTransactionManager, rollbackFor = Exception.class)
    public void batchAddRewardPoolInputRecord(List<JackpotRewardPoolInputRecordDo> recordList) {
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        jackpotRewardPoolInputRecordService.batchAdd(recordList, jackpotGameType());
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
        jackpotRewardPoolService.batchUpdate(buildList(grandAmount, majorAmount, minorAmount, miniAmount), jackpotGameType());
    }

    //    @Override
    //    @Transactional(transactionManager = lamangTransactionManager, rollbackFor = Exception.class)
    //    public void addRewardPoolInputRecord(JackpotRewardPoolInputRecordDo rewardPoolInputRecord) {
    //        if (Objects.isNull(rewardPoolInputRecord)) {
    //            return;
    //        }
    //        jackpotRewardPoolInputRecordService.add(rewardPoolInputRecord, jackpotGameType());
    //        jackpotRewardPoolService.batchUpdate(buildList(rewardPoolInputRecord.getGrandAmount(),
    //                        rewardPoolInputRecord.getMajorAmount(),
    //                        rewardPoolInputRecord.getMinorAmount(),
    //                        rewardPoolInputRecord.getMiniAmount()),
    //                jackpotGameType());
    //    }

    @Override
    @Transactional(transactionManager = lamangTransactionManager)
    public Long winReward(JackpotUserRewardRecordDo jackpotUserRewardRecord) {
        // 新增用户中奖记录
        Long recordId = jackpotUserRewardRecordService.add(jackpotUserRewardRecord, jackpotGameType());
        AssertUtil.assertNotNull(recordId, ErrorCode.ERR_INTER);
        // 扣减对应奖池金额
        boolean update = jackpotRewardPoolService.updateAmountByType(JackpotRewardPoolDo.builder()
                .type(jackpotUserRewardRecord.getRewardPoolType())
                .amount(-jackpotUserRewardRecord.getRewardAmount())
                .build(), jackpotGameType());
        AssertUtil.assertTrue(update, ErrorCode.ERR_INTER);
        return recordId;
    }
}

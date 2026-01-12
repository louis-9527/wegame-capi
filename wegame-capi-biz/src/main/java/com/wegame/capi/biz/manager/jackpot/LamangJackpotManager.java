package com.wegame.capi.biz.manager.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotRewardPoolBuilder;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional(transactionManager = lamangTransactionManager, rollbackFor = Exception.class)
    public void batchAddRewardPoolInputRecord(List<JackpotRewardPoolInputRecordDo> recordList) {
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        jackpotRewardPoolInputRecordService.batchAdd(recordList, jackpotGameType());
    }

    @Override
    @Transactional(transactionManager = lamangTransactionManager, rollbackFor = Exception.class)
    public void accRewardPool(List<JackpotRewardPoolInputRecordDo> recordList) {
        recordList = Optional.ofNullable(recordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> Objects.equals(item.getAccFlag().getCode(), JackpotAccFlagEnum.NO.getCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        List<Long> idList = recordList.stream()
                .map(JackpotRewardPoolInputRecordDo::getId)
                .collect(Collectors.toList());
        long modifyNum = jackpotRewardPoolInputRecordService.batchUpdateAccFlag(idList, JackpotAccFlagEnum.YES, jackpotGameType());
        log.info("modifyNum: {}, idList size: {}", modifyNum, idList.size());
        // 按照奖池金额统计
        long grandAmount = 0;
        long majorAmount = 0;
        long minorAmount = 0;
        long miniAmount = 0;
        long grandReservedAmount = 0;
        long majorReservedAmount = 0;
        long minorReservedAmount = 0;
        long miniReservedAmount = 0;
        // 遍历列表并累加各个金额字段
        for (JackpotRewardPoolInputRecordDo record : recordList) {
            grandAmount += record.getGrandAmount() == null ? 0 : record.getGrandAmount();
            majorAmount += record.getMajorAmount() == null ? 0 : record.getMajorAmount();
            minorAmount += record.getMinorAmount() == null ? 0 : record.getMinorAmount();
            miniAmount += record.getMiniAmount() == null ? 0 : record.getMiniAmount();
            grandReservedAmount += record.getGrandReservedAmount() == null ? 0 : record.getGrandReservedAmount();
            majorReservedAmount += record.getMajorReservedAmount() == null ? 0 : record.getMajorReservedAmount();
            minorReservedAmount += record.getMinorReservedAmount() == null ? 0 : record.getMinorReservedAmount();
            miniReservedAmount += record.getMiniReservedAmount() == null ? 0 : record.getMiniReservedAmount();
        }
        JackpotRewardPoolBuilder.BuildContext context = JackpotRewardPoolBuilder.BuildContext.builder()
                .grandAmount(grandAmount)
                .grandReservedAmount(grandReservedAmount)
                .majorAmount(majorAmount)
                .majorReservedAmount(majorReservedAmount)
                .minorAmount(minorAmount)
                .minorReservedAmount(minorReservedAmount)
                .miniAmount(miniAmount)
                .miniReservedAmount(miniReservedAmount)
                .build();
        jackpotRewardPoolService.batchUpdate(buildList(context), jackpotGameType());
    }

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

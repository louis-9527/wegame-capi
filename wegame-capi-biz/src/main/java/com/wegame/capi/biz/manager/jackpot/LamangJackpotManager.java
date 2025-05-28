package com.wegame.capi.biz.manager.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolInputRecordService;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolService;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;

import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.wegame.capi.infrastructure.config.mongo.LamangMongoConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component("lamangJackpotManager")
public class LamangJackpotManager extends AbstractJackpotManager {
    @Autowired
    JackpotRewardPoolInputRecordService jackpotRewardPoolInputRecordService;
    @Autowired
    JackpotRewardPoolService jackpotRewardPoolService;
    @Autowired
    JackpotUserRewardRecordService jackpotUserRewardRecordService;
    @Autowired
    LamangConfig lamangConfig;

    @Override
    public List<JackpotRewardPoolDo> listJackpotRewardPool() {
        return jackpotRewardPoolService.listAll(JackpotTypeEnum.LAM_ANG);
    }

    @Override
    public JackpotUserRewardRecordDo getHighestUserRewardRecord() {
        return jackpotUserRewardRecordService.getHighestUserRewardRecord(JackpotTypeEnum.LAM_ANG);
        // todo. 查询 & 缓存 & 缓存刷新 @lyric 参考 大奖记录
    }

    @Override
    public JackpotUserRewardRecordDo getUserRewardRecord(JackpotUserRewardRecordQuery query) {
        return jackpotUserRewardRecordService.selectOne(query, JackpotTypeEnum.LAM_ANG);
    }

    @Override
    public List<JackpotUserRewardRecordDo> listUserRewardRecord(JackpotUserRewardRecordQuery query) {
        return jackpotUserRewardRecordService.list(query, JackpotTypeEnum.LAM_ANG);
    }


    @Transactional(transactionManager = LamangMongoConfig.lamangTransactionManager, rollbackFor = Exception.class)
    @Override
    public void batchAddUserRewardRecord(List<JackpotRewardPoolInputRecordDo> rewardPoolInputRecordDos) {
        if (CollectionUtils.isEmpty(rewardPoolInputRecordDos)) {
            return;
        }
       jackpotRewardPoolInputRecordService.batchAdd(rewardPoolInputRecordDos, JackpotTypeEnum.valueOfGameType(GameTypeEnum.LAM_ANG));
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

        jackpotRewardPoolService.batchUpdate(this.buildRewardPoolDos(grandAmount, majorAmount, minorAmount, miniAmount), JackpotTypeEnum.valueOfGameType(GameTypeEnum.LAM_ANG));
    }
    @Transactional(transactionManager = LamangMongoConfig.lamangTransactionManager, rollbackFor = Exception.class)
    @Override
    public void addUserRewardRecord(JackpotRewardPoolInputRecordDo rewardPoolInputRecordDo) {
        if (Objects.isNull(rewardPoolInputRecordDo)) {
            return;
        }
        jackpotRewardPoolInputRecordService.add(rewardPoolInputRecordDo, JackpotTypeEnum.valueOfGameType(GameTypeEnum.LAM_ANG));
        jackpotRewardPoolService.batchUpdate(this.buildRewardPoolDos(rewardPoolInputRecordDo.getGrandAmount(), rewardPoolInputRecordDo.getMajorAmount(), rewardPoolInputRecordDo.getMinorAmount(), rewardPoolInputRecordDo.getMiniAmount()),
                JackpotTypeEnum.valueOfGameType(GameTypeEnum.LAM_ANG));
    }

    @Override
    @Transactional(transactionManager = LamangMongoConfig.lamangTransactionManager)
    public Long winReward(JackpotUserRewardRecordDo jackpotUserRewardRecordDo) {
        Long add = jackpotUserRewardRecordService.add(JackpotTypeEnum.LAM_ANG, jackpotUserRewardRecordDo);

        JackpotRewardPoolDo buildUpdate = JackpotRewardPoolDo.builder().type(jackpotUserRewardRecordDo.getRewardPoolType())
                .amount(-jackpotUserRewardRecordDo.getRewardAmount()).build();
        boolean update = jackpotRewardPoolService.update(buildUpdate, JackpotTypeEnum.valueOfGameType(GameTypeEnum.LAM_ANG));
        AssertUtil.assertTrue(update, ErrorCode.ERR_INTER);
        return add;
    }

    private List<JackpotRewardPoolDo> buildRewardPoolDos(Long grandAmount,Long majorAmount,Long minorAmount,Long miniAmount) {
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
        return rewardPoolDos;
    }
}

package com.wegame.capi.biz.manager;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RecordManager<T> {
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    GameRecordService<T> gameRecordService;

    @Transactional
    public boolean addRecord(BetRecordDo betRecord, List<T> gameRecordList, GameTypeEnum gameType) {
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        boolean ret = gameRecordService.batchAdd(gameRecordList, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        return true;
    }
}

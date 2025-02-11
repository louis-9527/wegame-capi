package com.wegame.capi.biz.manager;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Component
public class MariaMakilingRecordManager extends AbstractRecordManager<MariaMakilingGameRecordDo> {

    @Autowired
    GameRecordService<MariaMakilingGameRecordDo> gameRecordService;

    @Transactional(transactionManager = "mariamakilingTransactionManager")
    public Long add(BetRecordDo betRecord, List<MariaMakilingGameRecordDo> gameRecordList, GameTypeEnum gameType) {
        Long id = betRecordService.add(betRecord, gameType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        boolean ret = gameRecordService.batchAdd(gameRecordList, gameType);
        AssertUtil.assertTrue(ret, ErrorCode.ERR_INTER);
        return betRecord.getId();
    }

    @Override
    public List<MariaMakilingGameRecordDo> listGameRecord(GameRecordQuery query, GameTypeEnum gameType) {
        return gameRecordService.list(query, gameType, MariaMakilingGameRecordDo.class);
    }


}

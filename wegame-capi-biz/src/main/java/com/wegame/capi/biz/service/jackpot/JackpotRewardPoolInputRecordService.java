package com.wegame.capi.biz.service.jackpot;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotUserRewardPoolInputRecordQueryBuilder;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolInputRecordConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Jackpot奖池流入记录服务
 *
 * @author genereate
 */
@Service
public class JackpotRewardPoolInputRecordService extends AbstractService {

    public void batchAdd(List<JackpotRewardPoolInputRecordDo> doList, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "jackpotType is null");
        List<JackpotRewardPoolInputRecordPo> poList = JackpotRewardPoolInputRecordConverter.doToPoList(doList, OptEnum.ADD);
        getMongoTemplate(jackpotType.getGameType()).insert(poList, jackpotType.getJackpotRewardPoolInputRecordCollectionName());
    }

    public long batchDelete(JackpotTypeEnum jackpotType, Query query) {
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "jackpotType is null");
        DeleteResult result = getMongoTemplate(jackpotType.getGameType()).remove(query, jackpotType.getJackpotRewardPoolInputRecordCollectionName());
        return result.getDeletedCount();
    }

    public long batchUpdateAccFlag(List<Long> idList, JackpotAccFlagEnum jackpotAccFlag, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotEmpty(idList, ErrorCode.ERR_PARAM, "idList is empty");
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "gameType is null");
        Query query = Query.query(Criteria.where("_id").in(idList));
        Update update = new Update().set("accFlag", jackpotAccFlag.getCode()); // 把 status 字段改为 "DONE"
        UpdateResult updateResult = getMongoTemplate(jackpotType.getGameType()).updateMulti(query, update, jackpotType.getJackpotRewardPoolInputRecordCollectionName());
        return updateResult.getModifiedCount();
    }

    public List<JackpotRewardPoolInputRecordDo> list(JackpotRewardPoolInputRecordQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<JackpotRewardPoolInputRecordPo> poList = getMongoTemplate(jackpotType.getGameType()).find(JackpotUserRewardPoolInputRecordQueryBuilder.buildMongodbQuery(query),
                JackpotRewardPoolInputRecordPo.class,
                jackpotType.getJackpotRewardPoolInputRecordCollectionName());
        return JackpotRewardPoolInputRecordConverter.poToDoList(poList);
    }

}
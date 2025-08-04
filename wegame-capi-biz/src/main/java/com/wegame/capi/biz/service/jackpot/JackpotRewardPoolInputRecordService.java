package com.wegame.capi.biz.service.jackpot;

import com.mongodb.client.result.DeleteResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolInputRecordConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordPo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolInputRecordConverter.doToPo;

/**
 * Jackpot奖池流入记录服务
 *
 * @author genereate
 */
@Service
public class JackpotRewardPoolInputRecordService extends AbstractService {

    public void add(JackpotRewardPoolInputRecordDo recordDo, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(recordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "jackpotType is null");
        JackpotRewardPoolInputRecordPo po = doToPo(recordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        getMongoTemplate(jackpotType.getGameType()).insert(po, jackpotType.getJackpotRewardPoolInputCollectionName());
    }

    public void batchAdd(List<JackpotRewardPoolInputRecordDo> doList, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "jackpotType is null");
        List<JackpotRewardPoolInputRecordPo> poList = JackpotRewardPoolInputRecordConverter.doToPoList(doList, OptEnum.ADD);
        getMongoTemplate(jackpotType.getGameType()).insert(poList, jackpotType.getJackpotRewardPoolInputCollectionName());
    }

    public long batchDelete(JackpotTypeEnum jackpotType, Query query) {
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "jackpotType is null");
        DeleteResult result = getMongoTemplate(jackpotType.getGameType()).remove(query, jackpotType.getJackpotRewardPoolInputCollectionName());
        return result.getDeletedCount();
    }

}
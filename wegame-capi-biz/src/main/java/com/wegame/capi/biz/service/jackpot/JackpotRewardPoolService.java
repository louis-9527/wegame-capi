package com.wegame.capi.biz.service.jackpot;

import com.mongodb.bulk.BulkWriteResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotQueryBuilder;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.dao.mapper.business.JackpotRewardPoolMapper;
import com.wegame.capi.dao.repository.domain.business.BetRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * po服务
 * @author genereate
 */
@Service
public class JackpotRewardPoolService extends AbstractService {

    @Autowired
    JackpotRewardPoolMapper jackpotRewardPoolMapper;

    /**
     * 批量更新
     *
     * @param jackpotRewardPoolDos
     * @param jackpotTypeEnum
     */
    public void batchUpdate(List<JackpotRewardPoolDo> jackpotRewardPoolDos, JackpotTypeEnum jackpotTypeEnum) {
        AssertUtil.assertNotNull(jackpotRewardPoolDos, ErrorCode.ERR_PARAM, "jackpotRewardPoolDos is null");
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM, "gameType is null");
        List<JackpotRewardPoolPo> jackpotRewardPoolPos = JackpotRewardPoolConverter.doToPoList(jackpotRewardPoolDos,OptEnum.UPDATE);
        AssertUtil.assertNotNull(jackpotRewardPoolPos, ErrorCode.ERR_PARAM, "jackpotRewardPoolPo is null");

        BulkOperations bulkOps = getMongoTemplate(jackpotTypeEnum.getGameType()).bulkOps(BulkOperations.BulkMode.UNORDERED,
                BetRecordPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());
        jackpotRewardPoolDos.forEach(poolDo -> {
            Criteria criteria = new Criteria();
            criteria.and("type").is(poolDo.getType());
            Query query = new Query(criteria);
            bulkOps.updateOne(query, JackpotQueryBuilder.buildUpdate(poolDo));
        });
        BulkWriteResult bulkWriteResult = bulkOps.execute();
        bulkWriteResult.getModifiedCount();
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<JackpotRewardPoolDo> jackpot奖池列表
     */
    public List<JackpotRewardPoolDo> list(JackpotRewardPoolQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return JackpotRewardPoolConverter.poToDoList(jackpotRewardPoolMapper.list(query));
    }

    //    /**
    //     * 数量统计
    //     *
    //     * @param query 查询参数
    //     * @return 数量
    //     */
    //    public long count(JackpotRewardPoolQuery query) {
    //        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
    //        return jackpotRewardPoolMapper.count(query);
    //    }

    ///////////////  above auto generated ///////////////
}
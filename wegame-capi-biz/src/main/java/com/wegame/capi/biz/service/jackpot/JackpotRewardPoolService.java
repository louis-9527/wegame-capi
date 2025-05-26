package com.wegame.capi.biz.service.jackpot;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotQueryBuilder;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * po服务
 *
 * @author genereate
 */
@Service
public class JackpotRewardPoolService extends AbstractService {

    /**
     * 批量更新
     *
     * @param doList
     * @param jackpotTypeEnum
     */
    public void batchUpdate(List<JackpotRewardPoolDo> doList, JackpotTypeEnum jackpotTypeEnum) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM, "doList is null");
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM, "gameType is null");
        List<JackpotRewardPoolPo> poList = JackpotRewardPoolConverter.doToPoList(doList, OptEnum.UPDATE);
        AssertUtil.assertNotNull(poList, ErrorCode.ERR_PARAM, "jackpotRewardPoolPo is null");
        BulkOperations bulkOps = getMongoTemplate(jackpotTypeEnum.getGameType()).bulkOps(BulkOperations.BulkMode.ORDERED,
                JackpotRewardPoolPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());
        poList.forEach(poolPo -> {
            Criteria criteria = new Criteria();
            criteria.and("type").is(poolPo.getType());
            Query query = new Query(criteria);
            bulkOps.updateOne(query, JackpotQueryBuilder.buildUpdate(poolPo));
        });
        bulkOps.execute();
    }

    /**
     * 查询列表
     *
     * @return List<JackpotRewardPoolDo> jackpot奖池列表
     */
    @Cacheable(value = CacheConstants.JACKPOT_REWARD_POOL_CONFIG, key = "#jackpotTypeEnum.gameType.id")
    public List<JackpotRewardPoolDo> listAll(JackpotTypeEnum jackpotTypeEnum) {
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM);
        List<JackpotRewardPoolPo> pos = getMongoTemplate(jackpotTypeEnum.getGameType()).find(JackpotQueryBuilder.listAllQuery(),
                JackpotRewardPoolPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());
        return JackpotRewardPoolConverter.poToDoList(pos);
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
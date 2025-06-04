package com.wegame.capi.biz.service.jackpot;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotRewardPoolQueryBuilder;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;

import static com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter.poToDoList;

/**
 * Jackpot奖池服务
 *
 * @author genereate
 */
@Service
public class JackpotRewardPoolService extends AbstractService {

    @Autowired
    LamangConfig lamangConfig;

    public void batchUpdate(List<JackpotRewardPoolDo> doList, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM, "doList is null");
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "jackpotType is null");
        List<JackpotRewardPoolPo> poList = JackpotRewardPoolConverter.doToPoList(doList, OptEnum.UPDATE);
        AssertUtil.assertNotEmpty(poList, ErrorCode.ERR_PARAM, "poList is null");
        BulkOperations bulkOps = getMongoTemplate(jackpotType.getGameType()).bulkOps(BulkOperations.BulkMode.ORDERED,
                JackpotRewardPoolPo.class,
                jackpotType.getJackpotRewardPoolCollectionName());
        poList.forEach(poolPo -> {
            Criteria criteria = new Criteria();
            criteria.and("type").is(poolPo.getType());
            Query query = new Query(criteria);
            bulkOps.updateOne(query, JackpotRewardPoolQueryBuilder.buildAddAmountUpdate(poolPo));
        });
        bulkOps.execute();
    }

    public boolean updateAmountByType(JackpotRewardPoolDo jackpotRewardPool, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(jackpotRewardPool, ErrorCode.ERR_PARAM, "jackpotRewardPool is null");
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM, "jackpotType is null");
        JackpotRewardPoolPo po = JackpotRewardPoolConverter.doToPo(jackpotRewardPool, OptEnum.UPDATE);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM, "po is null");

        Criteria criteria = new Criteria();
        criteria.and("type").is(po.getType());
        Query query = new Query(criteria);
        // 更新金额
        UpdateResult result = getMongoTemplate(jackpotType.getGameType()).updateFirst(query,
                JackpotRewardPoolQueryBuilder.buildAddAmountUpdate(po),
                JackpotRewardPoolPo.class,
                jackpotType.getJackpotRewardPoolCollectionName());
        return result.getModifiedCount() > 0;
    }

    @Cacheable(value = CacheConstants.JACKPOT_REWARD_POOL, key = "#jackpotTypeEnum.gameType.id")
    public List<JackpotRewardPoolDo> listAllWithCache(JackpotTypeEnum jackpotTypeEnum) {
        return listAll(jackpotTypeEnum);
    }

    public List<JackpotRewardPoolDo> listAll(JackpotTypeEnum jackpotTypeEnum) {
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM);
        List<JackpotRewardPoolPo> poList = getMongoTemplate(jackpotTypeEnum.getGameType()).find(JackpotRewardPoolQueryBuilder.buildListAllQuery(),
                JackpotRewardPoolPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());
        List<JackpotRewardPoolDo> doList = poToDoList(poList);
        // 修改初始化金额
        TreeMap<String, Long> guaranteedAmount = lamangConfig.getJackPotConfig().getGuaranteedAmount();
        doList.forEach(poolDo -> {
            poolDo.setAmount(Math.max(guaranteedAmount.get(poolDo.getType().getName()), poolDo.getAmount()));
            poolDo.setInitAmount(guaranteedAmount.get(poolDo.getType().getName()));
        });
        return doList;
    }

    @CacheEvict(value = CacheConstants.JACKPOT_REWARD_POOL, key = "#jackpotTypeEnum.gameType.id")
    public void evictJackpotRewardPoolListCache(JackpotTypeEnum jackpotTypeEnum) {
    }

    public JackpotRewardPoolDo selectOneByType(JackpotRewardPoolTypeEnum rewardPoolTypeEnum, JackpotTypeEnum jackpotTypeEnum) {
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(rewardPoolTypeEnum, ErrorCode.ERR_PARAM);
        JackpotRewardPoolQuery buildQuery = JackpotRewardPoolQuery.builder()
                .type(rewardPoolTypeEnum.getCode())
                .build();
        JackpotRewardPoolPo po = getMongoTemplate(jackpotTypeEnum.getGameType()).findOne(JackpotRewardPoolQueryBuilder.buildMongodbQuery(buildQuery),
                JackpotRewardPoolPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());
        return JackpotRewardPoolConverter.poToDo(po);
    }
}
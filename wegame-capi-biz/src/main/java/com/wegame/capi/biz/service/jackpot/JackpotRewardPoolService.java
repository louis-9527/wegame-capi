package com.wegame.capi.biz.service.jackpot;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotQueryBuilder;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;

/**
 * po服务
 *
 * @author genereate
 */
@Service
public class JackpotRewardPoolService extends AbstractService {
    @Autowired
    private LamangConfig lamangConfig;

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
            bulkOps.updateOne(query, JackpotQueryBuilder.buildAddAmount(poolPo));
        });
        bulkOps.execute();
    }

    public boolean update(JackpotRewardPoolDo jackpotRewardPoolDo, JackpotTypeEnum jackpotTypeEnum) {
         AssertUtil.assertNotNull(jackpotRewardPoolDo, ErrorCode.ERR_PARAM, "jackpotRewardPoolDo is null");
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM, "gameType is null");
        JackpotRewardPoolPo jackpotRewardPoolPo = JackpotRewardPoolConverter.doToPo(jackpotRewardPoolDo,OptEnum.UPDATE);
        AssertUtil.assertNotNull(jackpotRewardPoolPo, ErrorCode.ERR_PARAM, "jackpotRewardPoolPo is null");

        Criteria criteria = new Criteria();
        criteria.and("type").is(jackpotRewardPoolPo.getType());
        Query query = new Query(criteria);
        // 扣减金额
        UpdateResult result = getMongoTemplate(jackpotTypeEnum.getGameType()).updateFirst(query,
                JackpotQueryBuilder.buildAddAmount(jackpotRewardPoolPo),
                JackpotRewardPoolPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());
        return result.getModifiedCount() > 0;
    }

    /**
     * 查询列表
     *
     * @return List<JackpotRewardPoolDo> jackpot奖池列表
     */
    @Cacheable(value = CacheConstants.JACKPOT_REWARD_POOL, key = "#jackpotTypeEnum.gameType.id")
    public List<JackpotRewardPoolDo> listAll(JackpotTypeEnum jackpotTypeEnum) {
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM);
        List<JackpotRewardPoolPo> pos = getMongoTemplate(jackpotTypeEnum.getGameType()).find(JackpotQueryBuilder.listAllQuery(),
                JackpotRewardPoolPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());

        List<JackpotRewardPoolDo> rewardPoolDos = JackpotRewardPoolConverter.poToDoList(pos);
        if(CollectionUtils.isEmpty(rewardPoolDos)){
            return rewardPoolDos;
        }
        // 修改初始化金额
        TreeMap<String, Long> guaranteedAmount = lamangConfig.getJackPotConfig().getGuaranteedAmount();
        rewardPoolDos.forEach(poolDo -> {
            poolDo.setAmount(Math.max(guaranteedAmount.get(poolDo.getType().getName()),poolDo.getAmount()));
            poolDo.setInitAmount(guaranteedAmount.get(poolDo.getType().getName()));
        });
        return rewardPoolDos;
    }
    @CacheEvict(value = CacheConstants.JACKPOT_REWARD_POOL, key = "#jackpotTypeEnum.gameType.id")
    public void evictJackpotRewardPoolListCache(JackpotTypeEnum jackpotTypeEnum) {
    }

    public JackpotRewardPoolDo getByType(JackpotTypeEnum jackpotTypeEnum, JackpotRewardPoolTypeEnum rewardPoolTypeEnum){
        AssertUtil.assertNotNull(jackpotTypeEnum, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(rewardPoolTypeEnum, ErrorCode.ERR_PARAM);
        JackpotRewardPoolQuery buildQuery = JackpotRewardPoolQuery.builder().type(rewardPoolTypeEnum.getCode()).build();
        JackpotRewardPoolPo po= getMongoTemplate(jackpotTypeEnum.getGameType()).findOne(JackpotQueryBuilder.buildMongodbQuery(buildQuery),
                JackpotRewardPoolPo.class,
                jackpotTypeEnum.getJackpotRewardPoolCollectionName());
        return JackpotRewardPoolConverter.poToDo(po);

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
package com.wegame.capi.biz.service;

import cn.hutool.core.date.StopWatch;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 投注记录服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetRecordService {

    @Autowired
    public MongoTemplate mongoTemplate;

    /**
     * 新增
     *
     * @param betRecordDo 投注记录
     * @return 主键id
     */
    public Long add(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BetRecordPo po = mongoTemplate.insert(BetRecordConverter.doToPo(betRecordDo, OptEnum.ADD), gameType.getCollectionName());
        stopWatch.stop();
        log.info("BetRecordService#add cost time:{}", stopWatch.getLastTaskTimeMillis());
        return po.getId();
    }

    /**
     * 修改
     *
     * @param betRecordDo 投注记录
     * @return true/false
     */
    @CacheEvict(value = CacheConstants.BET_RECORD, key = "#gameType.collectionName +'-'+ #betRecordDo.id")
    public boolean update(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betRecordDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is empty");
        UpdateResult result = mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(betRecordDo.getId())),
                BetRecordQueryBuilder.buildMongoUpdate(betRecordDo),
                BetRecordPo.class, gameType.getCollectionName());
        stopWatch.stop();
        log.info("BetRecordService#update cost time:{}", stopWatch.getLastTaskTimeMillis());
        return Objects.nonNull(result) && result.getModifiedCount() > 0;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return BetRecordDo 投注记录
     */
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.collectionName +'-'+ #id")
    public <T> BetRecordDo<T> get(Long id, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        BetRecordPo betRecordPo = mongoTemplate.findById(id, BetRecordPo.class, gameType.getCollectionName());
        return BetRecordConverter.poToDo(betRecordPo);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> list(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<BetRecordPo> recordPoList = mongoTemplate.find(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class, gameType.getCollectionName());
        return BetRecordConverter.poToDoList(recordPoList);
    }

    /**
     * 分页查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> listForPage(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<BetRecordPo> recordPoList = mongoTemplate.find(BetRecordQueryBuilder.buildMongodbQueryForPage(query), BetRecordPo.class, gameType.getCollectionName());
        return BetRecordConverter.poToDoList(recordPoList);
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return mongoTemplate.count(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class, gameType.getCollectionName());
    }

    ///////////////  above auto generated ///////////////
}
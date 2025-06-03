package com.wegame.capi.biz.service.jackpot;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotQueryBuilder;
import com.wegame.capi.biz.builder.jackpot.JackpotUserRewardRecordQueryBuilder;
import com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordTypesQuery;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.jackpot.JackpotUserRewardRecordQueryBuilder.buildHighestRecordQuery;
import static com.wegame.capi.biz.builder.jackpot.JackpotUserRewardRecordQueryBuilder.buildMongodbQuery;
import static com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter.poToDo;
import static com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter.poToDoList;

/**
 * po服务
 *
 * @author genereate
 */
@Service
public class JackpotUserRewardRecordService extends AbstractService {

    ///////////////  mongo done start ///////////////
    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return JackpotUserRewardRecordDo 用户jackpot中奖记录
     */
    public JackpotUserRewardRecordDo get(Long id, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        JackpotUserRewardRecordPo rewardRecordPo = getMongoTemplate(jackpotType.getGameType()).findOne(
                query,
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDo(rewardRecordPo);
    }

    public JackpotUserRewardRecordDo selectOne(JackpotUserRewardRecordQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        Query recordQuery = buildMongodbQuery(query);
        JackpotUserRewardRecordPo rewardRecordPo = getMongoTemplate(jackpotType.getGameType()).findOne(
                recordQuery,
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDo(rewardRecordPo);
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(JackpotUserRewardRecordQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        Query recordQuery = buildMongodbQuery(query);
        recordQuery.fields().include("_id");
        List<JackpotUserRewardRecordPo> betRecordPoList = getMongoTemplate(jackpotType.getGameType()).find(
                recordQuery,
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return Optional.ofNullable(betRecordPoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotUserRewardRecordPo::getId)
                .collect(Collectors.toList());
    }

    public List<JackpotUserRewardRecordDo> list(JackpotUserRewardRecordQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<JackpotUserRewardRecordPo> recordPoList = getMongoTemplate(jackpotType.getGameType()).find(buildMongodbQuery(query),
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDoList(recordPoList);
    }


    /**
     * 根据奖池类型返回固定条数的列表
     * @param query
     * @param jackpotType
     * @return
     */
    @Cacheable(value = CacheConstants.JACKPOT_USER_REWARD_RECORD_LIST,
            key = "#query.getCacheKey() + ':' + #jackpotType.gameType.id")
    public List<JackpotUserRewardRecordDo> listByTypes(JackpotUserRewardRecordTypesQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<JackpotUserRewardRecordPo> recordPoList = getMongoTemplate(jackpotType.getGameType())
                .find(buildMongodbQuery(query),
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDoList(recordPoList);
    }

    @Cacheable(value = CacheConstants.JACKPOT_USER_REWARD_HIGHEST_RECORD, key = "#jackpotType.gameType.id")
    public JackpotUserRewardRecordDo getHighestUserRewardRecord(JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo maxRecord = getMongoTemplate(jackpotType.getGameType()).findOne(buildHighestRecordQuery(),
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDo(maxRecord);
    }

    @CacheEvict(value = CacheConstants.JACKPOT_USER_REWARD_HIGHEST_RECORD, key = "#jackpotType.gameType.id")
    public void clearHighestUserRewardRecord(JackpotTypeEnum jackpotType) {
        // ...
    }



    public Long add(JackpotTypeEnum jackpotType,JackpotUserRewardRecordDo recordDo){
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo jackpotUserRewardRecordPo = JackpotUserRewardRecordConverter.doToPo(recordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(jackpotUserRewardRecordPo, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo insert = getMongoTemplate(jackpotType.getGameType()).insert(jackpotUserRewardRecordPo,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return insert.getId();
    }

    public Boolean update(JackpotTypeEnum jackpotType,JackpotUserRewardRecordDo recordDo){
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo jackpotUserRewardRecordPo = JackpotUserRewardRecordConverter.doToPo(recordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(jackpotUserRewardRecordPo, ErrorCode.ERR_PARAM);
        Query query = buildMongodbQuery(JackpotUserRewardRecordQuery.builder().id(recordDo.getId()).build());
        UpdateResult result = getMongoTemplate(jackpotType.getGameType()).updateFirst(query,
                JackpotUserRewardRecordQueryBuilder.buildUpdate(jackpotUserRewardRecordPo),
                JackpotRewardPoolPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return result.getModifiedCount() > 0;
    }

    ///////////////  mongo done end ///////////////


    //    /**
    //     * 新增
    //     *
    //     * @param jackpotUserRewardRecordDo 用户jackpot中奖记录
    //     * @return 主键id
    //     */
    //    public Long add(JackpotUserRewardRecordDo jackpotUserRewardRecordDo) {
    //        AssertUtil.assertNotNull(jackpotUserRewardRecordDo, ErrorCode.ERR_PARAM);
    //        JackpotUserRewardRecordPo po = JackpotUserRewardRecordConverter.doToPo(jackpotUserRewardRecordDo, OptEnum.ADD);
    //        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
    //        jackpotUserRewardRecordMapper.insert(po);
    //        return po.getId();
    //    }
    //
    //    /**
    //     * 批量新增
    //     *
    //     * @param doList 用户jackpot中奖记录列表
    //     * @return true/false
    //     */
    //    public boolean batchAdd(List<JackpotUserRewardRecordDo> doList) {
    //        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
    //        jackpotUserRewardRecordMapper.batchInsert(JackpotUserRewardRecordConverter.doToPoList(doList, OptEnum.ADD));
    //        return true;
    //    }
    //
    //    /**
    //     * 修改
    //     *
    //     * @param jackpotUserRewardRecordDo 用户jackpot中奖记录
    //     * @return true/false
    //     */
    //    public boolean update(JackpotUserRewardRecordDo jackpotUserRewardRecordDo) {
    //        AssertUtil.assertNotNull(jackpotUserRewardRecordDo, ErrorCode.ERR_PARAM);
    //        AssertUtil.assertNotNull(jackpotUserRewardRecordDo.getId(), ErrorCode.ERR_PARAM);
    //        jackpotUserRewardRecordMapper.updateById(JackpotUserRewardRecordConverter.doToPo(jackpotUserRewardRecordDo, OptEnum.UPDATE));
    //        return true;
    //    }
    //
    //    //    /**
    //    //     * 查询：根据条件查询
    //    //     *
    //    //     * @param query 查询条件
    //    //     * @return JackpotUserRewardRecordDo 用户jackpot中奖记录
    //    //     */
    //    //    public JackpotUserRewardRecordDo selectOne(JackpotUserRewardRecordQuery query) {
    //    //        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
    //    //        return JackpotUserRewardRecordConverter.poToDo(jackpotUserRewardRecordMapper.selectOne(query));
    //    //    }
    //
    //    /**
    //     * 查询Id：根据条件查询
    //     *
    //     * @param query 查询条件
    //     * @return id
    //     */
    //    public Long selectId(JackpotUserRewardRecordQuery query) {
    //        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
    //        return jackpotUserRewardRecordMapper.selectId(query);
    //    }
    //
    //    /**
    //     * 查询：根据id列表
    //     *
    //     * @param ids id列表
    //     * @return List<JackpotUserRewardRecordDo> 用户jackpot中奖记录列表
    //     */
    //    public List<JackpotUserRewardRecordDo> listByIds(List<Long> ids) {
    //        if (CollectionUtils.isEmpty(ids)) {
    //            return Collections.emptyList();
    //        }
    //        return poToDoList(jackpotUserRewardRecordMapper.listByIds(ids));
    //    }
    //
    //    /**
    //     * 查询全部
    //     *
    //     * @return List<JackpotUserRewardRecordDo> 用户jackpot中奖记录列表
    //     */
    //    public List<JackpotUserRewardRecordDo> listAll() {
    //        return poToDoList(jackpotUserRewardRecordMapper.listAll());
    //    }
    //
    ////    /**
    ////     * 查询列表
    ////     *
    ////     * @param query 查询参数
    ////     * @return List<JackpotUserRewardRecordDo> 用户jackpot中奖记录列表
    ////     */
    ////    public List<JackpotUserRewardRecordDo> list(JackpotUserRewardRecordQuery query) {
    ////        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
    ////        return poToDoList(jackpotUserRewardRecordMapper.list(query));
    ////    }
    //
    //    //    /**
    //    //     * 数量统计
    //    //     *
    //    //     * @param query 查询参数
    //    //     * @return 数量
    //    //     */
    //    //    public long count(JackpotUserRewardRecordQuery query) {
    //    //        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
    //    //        return jackpotUserRewardRecordMapper.count(query);
    //    //    }

    ///////////////  above auto generated ///////////////
}
package com.wegame.capi.biz.service;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Long add(BetRecordDo betRecordDo) {
        BetRecordPo po = mongoTemplate.insert(BetRecordConverter.doToPo(betRecordDo, OptEnum.ADD));
        return po.getId();
    }

    /**
     * 修改
     *
     * @param betRecordDo 投注记录
     * @return true/false
     */
    public boolean update(BetRecordDo betRecordDo) {
        log.info("BetRecordService#update betRecordDo:{}", betRecordDo);
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betRecordDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is empty");
        UpdateResult result = mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(betRecordDo.getId())),
                BetRecordQueryBuilder.buildMongoUpdate(betRecordDo),
                BetRecordPo.class);
        return Objects.nonNull(result) && result.getModifiedCount() > 0;
    }

    /**
     * 查询：根据主键id查询
     * todo 添加二级缓存 与 更新联动
     * @param id 主键id
     * @return BetRecordDo 投注记录
     */
    public <T> BetRecordDo<T> get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        BetRecordPo betRecordPo = mongoTemplate.findById(id, BetRecordPo.class);
        return BetRecordConverter.poToDo(betRecordPo);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> list(BetRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<BetRecordPo> recordPoList = mongoTemplate.find(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class);
        return BetRecordConverter.poToDoList(recordPoList);
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(BetRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return mongoTemplate.count(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class);
    }

    ///////////////  above auto generated ///////////////
}
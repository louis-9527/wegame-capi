package com.wegame.capi.biz.service.template;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.template.GameRecordRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repository.domain.template.GameRecordTemplatePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.biz.converter.operation.GameRecordTemplateConvert.poToDoList;

@Slf4j
@Service
public class GameRecordTemplateService<T> extends AbstractService {

    public boolean batchAdd(List<T> gameRecordList, TemplateTypeEnum templateTypeEnum, Class<T> tClass) {
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateTypeEnum, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(tClass, ErrorCode.ERR_INTER);
        BulkOperations bulkOps = getMongoTemplate(templateTypeEnum.getGameType()).bulkOps(BulkOperations.BulkMode.UNORDERED, tClass, templateTypeEnum.getGameRecordTemplateName());
        bulkOps.insert(gameRecordList);
        bulkOps.execute();
        return true;
    }

    public List<GameRecordTemplateDo> listByBetRecordIdList(TemplateTypeEnum templateType, List<Long> betRecordIdList) {
        log.info("listByBetRecordIdList query from mdb, size: {}; typeEnum: {}", betRecordIdList.size(), templateType.getGameType());
        Query query = Query.query(Criteria.where("betRecordId").in(betRecordIdList));
        List<GameRecordTemplatePo> poList = getMongoTemplate(templateType.getGameType()).find(query, GameRecordTemplatePo.class, templateType.getGameRecordTemplateName());
        return poToDoList(poList);
    }

    @CachePut(value = CacheConstants.GAME_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betRecordId")
    public List<GameRecordTemplateDo> putLocalCache(TemplateTypeEnum templateType, Long betRecordId, List<GameRecordTemplateDo> list) {
        log.info("putLocalCache, betRecordId: {}", betRecordId);
        return list;
    }

    @CachePut(value = CacheConstants.GAME_RECORD_TEMPLATE_REDIS, key = "#templateType.name() + '_' + #betRecordId")
    public List<GameRecordTemplateDo> putRedisCache(TemplateTypeEnum templateType, Long betRecordId, List<GameRecordTemplateDo> list) {
        log.info("putRedisCache, betRecordId: {}", betRecordId);
        return list;
    }

    @Cacheable(value = CacheConstants.GAME_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betRecordId")
    public List<GameRecordTemplateDo> listByBetRecordIdWithLocalCache(TemplateTypeEnum templateType, Long betRecordId) {
        // todo delete log
        log.info("listByBetRecordIdWithLocalCache, betRecordId: {}; templateType: {}", betRecordId, templateType.getGameType());
        return listByBetRecordId(templateType, betRecordId);
    }

    @Cacheable(value = CacheConstants.GAME_RECORD_TEMPLATE_REDIS, key = "#templateType.name() + '_' + #betRecordId")
    public List<GameRecordTemplateDo> listByBetRecordIdWithRedisCache(TemplateTypeEnum templateType, Long betRecordId) {
        // todo delete log
        log.info("listByBetRecordIdWithRedisCache, betRecordId: {}; templateType: {}", betRecordId, templateType.getGameType());
        return listByBetRecordId(templateType, betRecordId);
    }

    public List<GameRecordTemplateDo> listByBetRecordId(TemplateTypeEnum templateType, Long betRecordId) {
        log.info("listByBetRecordId  from mdb , betRecordId: {}; templateType: {}", betRecordId, templateType.getGameType());
        List<GameRecordTemplatePo> poList = getMongoTemplate(templateType.getGameType())
                .find(GameRecordRecordQueryBuilder.buildQuery(betRecordId),
                        GameRecordTemplatePo.class,
                        templateType.getGameRecordTemplateName());
        return poToDoList(poList);
    }

}

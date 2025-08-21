package com.wegame.capi.biz.service.template;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.template.BetRecordTemplateQueryBuilder;
import com.wegame.capi.biz.domain.biz.operation.BetRecordTemplateDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordPo;
import com.wegame.capi.dao.repository.domain.template.BetRecordTemplatePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.converter.BetRecordTemplateConverter.doToPo;
import static com.wegame.capi.biz.converter.BetRecordTemplateConverter.doToPoList;

@Slf4j
@Service
public class BetRecordTemplateService extends AbstractService {

    public Long add(BetRecordTemplateDo betRecordTemplateDo, TemplateTypeEnum templateTypeEnum) {
        BetRecordTemplatePo po = getMongoTemplate(templateTypeEnum.getGameType()).insert(doToPo(betRecordTemplateDo, OptEnum.ADD),
                templateTypeEnum.getBetRecordTemplateCollectionName());
        return po.getId();
    }

    public boolean batchAdd(List<BetRecordTemplateDo> betRecordList, TemplateTypeEnum templateTypeEnum) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM, "betRecordTemplateList is empty");
        AssertUtil.assertNotNull(templateTypeEnum, ErrorCode.ERR_PARAM, "gameType is null");
        List<BetRecordTemplatePo> poList = doToPoList(betRecordList, OptEnum.ADD);
        BulkOperations bulkOps = getMongoTemplate(templateTypeEnum.getGameType()).bulkOps(BulkOperations.BulkMode.UNORDERED,
                BetRecordPo.class,
                templateTypeEnum.getBetRecordTemplateCollectionName());
        poList.forEach(bulkOps::insert);
        bulkOps.execute();
        return true;
    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betType.getCode() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        log.info("listIdByMultipleRange query, betTypeEnum: {}, multipleRangeStart: {}, multipleRangeEnd: {}, typeEnum: {}", betType.getCode(), multipleRangeStart, multipleRangeEnd, templateType.getGameType());
        Query query = BetRecordTemplateQueryBuilder.buildQuery(betType, multipleRangeStart, multipleRangeEnd);
        String betRecordTemplateCollectionName = templateType.getBetRecordTemplateCollectionName();
        MongoTemplate mongoTemplate = getMongoTemplate(templateType.getGameType());
        // 1. 先查询为 ObjectId 类型的列表
        List<BetRecordTemplatePo> betRecordTemplateList = mongoTemplate.find(query, BetRecordTemplatePo.class, betRecordTemplateCollectionName);
        return Optional.of(betRecordTemplateList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordTemplatePo::getId)
                .collect(Collectors.toList());
    }
}

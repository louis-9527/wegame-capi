package com.wegame.capi.biz.service.template;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.template.GameRecordRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repository.domain.template.GameRecordTemplatePo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.biz.converter.operation.GameRecordTemplateConvert.poToDoList;

@Service
public class GameRecordTemplateService<T> extends AbstractService {

    public boolean batchAdd(List<T> gameRecordList, TemplateTypeEnum templateTypeEnum, Class<T> tClass) {
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateTypeEnum, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(tClass, ErrorCode.ERR_INTER);
        BulkOperations bulkOps = getMongoTemplate(templateTypeEnum.getGameType()).bulkOps(BulkOperations.BulkMode.UNORDERED, tClass);
        bulkOps.insert(gameRecordList);
        bulkOps.execute();
        return true;
    }
    @Cacheable(value = CacheConstants.OPERATION_EVENT_CONFIG, key = "#typeEnum.name() + '_' + #betRecordId")
    public List<GameRecordTemplateDo> listByBetRecordId(Long betRecordId, TemplateTypeEnum typeEnum) {
        List<GameRecordTemplatePo> poList = getMongoTemplate(typeEnum.getGameType())
                .find(GameRecordRecordQueryBuilder.buildQuery(betRecordId),
                        GameRecordTemplatePo.class,
                        typeEnum.getGameRecordTemplateCollectionName());
        return poToDoList(poList);
    }
}

package com.wegame.capi.biz.service.template;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.template.BetRecordTemplateQueryBuilder;
import com.wegame.capi.biz.domain.biz.operation.BetRecordTemplateDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordPo;
import com.wegame.capi.dao.repository.domain.operation.OperationEventDetailPo;
import com.wegame.capi.dao.repository.domain.template.BetRecordTemplatePo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.biz.converter.BetRecordTemplateConverter.*;

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

    @Cacheable(value = CacheConstants.OPERATION_EVENT_CONFIG, key = "#typeEnum.name() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(String multipleRangeStart, String multipleRangeEnd, TemplateTypeEnum typeEnum) {
        return getMongoTemplate(typeEnum.getGameType())
                .find(BetRecordTemplateQueryBuilder.buildQuery(multipleRangeStart, multipleRangeEnd),
                        Long.class,
                        typeEnum.getBetRecordTemplateCollectionName());
    }
}

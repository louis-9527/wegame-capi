package com.wegame.capi.biz.service.template;

import com.wegame.base.CacheConstants;
import com.wegame.capi.biz.builder.template.BetRecordTemplateQueryBuilder;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetRecordTemplateService extends AbstractService {

    @Cacheable(value = CacheConstants.OPERATION_EVENT_CONFIG, key = "#typeEnum.name() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(String multipleRangeStart, String multipleRangeEnd, TemplateTypeEnum typeEnum) {
        return getMongoTemplate(typeEnum.getGameType())
                .find(BetRecordTemplateQueryBuilder.buildQuery(multipleRangeStart, multipleRangeEnd),
                        Long.class,
                        typeEnum.getBetRecordTemplateCollectionName());
    }
}

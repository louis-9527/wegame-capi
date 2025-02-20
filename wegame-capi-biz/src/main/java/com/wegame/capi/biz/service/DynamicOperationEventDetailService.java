package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.DynamicOperationDetailConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailPo;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class DynamicOperationEventDetailService {
    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;
    private final String dynamicOperationEventDetailCollectionName = "mariamakiling_dynamicOperation_event_detail";

    public Long add(DynamicOperationEventDetailDo detailDo, GameTypeEnum gameType) {
        DynamicOperationDetailPo detailPo = DynamicOperationDetailConvert.convertPo(detailDo, OptEnum.ADD);
        if (Objects.isNull(detailPo)) {
            return null;
        }
        DynamicOperationDetailPo insert = getMongoTemplate(gameType).insert(detailPo, dynamicOperationEventDetailCollectionName);
        return insert.getId();
    }

    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }
}

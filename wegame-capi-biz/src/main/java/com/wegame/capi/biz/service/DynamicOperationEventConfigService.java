package com.wegame.capi.biz.service;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationConfigQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DynamicOperationEventConfigService {
    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    private final String dynamicOperationEventCollectionName = "mariamakiling_dynamicOperation_event_config";

    public List<List<WheelDo>> getConfigDetail(DynamicOperationConfigQuery configQuery, GameTypeEnum gameType) {
        List<List<WheelDo>> wheelDos = new ArrayList<>();
        List<DynamicOperationConfigPo> dynamicOperationConfigPos = getMongoTemplate(gameType).find(DynamicOperationConfigQueryBuilder.buildQuery(configQuery), DynamicOperationConfigPo.class);

        for (DynamicOperationConfigPo configPo : dynamicOperationConfigPos) {
            String detail = configPo.getDetail();
            wheelDos.add(JSON.parseArray(detail, WheelDo.class));
        }
        return wheelDos;
    }

    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }

}

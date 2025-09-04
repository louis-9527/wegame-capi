package com.wegame.capi.biz.service.template.es;

import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GameRecordTemplateEsService<T> {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    public List<T> listByBetRecordId(Long betRecordId, TemplateTypeEnum templateType) {
        // todo @maidong
        return null;
    }
}

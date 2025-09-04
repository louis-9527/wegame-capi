package com.wegame.capi.biz.service.template.es;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class GameRecordTemplateEsService<T> {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Deprecated  // 测试之用
    public boolean add(TemplateTypeEnum templateType, T t) {
        T saveT = elasticsearchRestTemplate.save(t);
        return Objects.nonNull(saveT);
    }

    @Deprecated  // 测试之用
    public T findById(Long id, Class<T> clazz) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(clazz, ErrorCode.ERR_PARAM);
        return elasticsearchRestTemplate.get(String.valueOf(id), clazz);
    }

    //    public T listByBetRecordId(Long betRecordId, TemplateTypeEnum templateType) {
    //        // todo @maidong
    //        return null;
    //    }
}

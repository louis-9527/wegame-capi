package com.wegame.capi.biz.service.template.es;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BetRecordTemplateEsService<T> {

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

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betType.getCode() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        // todo
        return null;
    }
}

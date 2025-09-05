package com.wegame.capi.biz.service.template.es;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.template.BetRecordTemplateQueryBuilder;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repository.domain.template.BetRecordTemplatePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BetRecordTemplateEsService<T> {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    public boolean add(TemplateTypeEnum templateType, T t) {
        T saveT = elasticsearchRestTemplate.save(t);
        return Objects.nonNull(saveT);
    }

    public T findById(Long id, Class<T> clazz) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(clazz, ErrorCode.ERR_PARAM);
        return elasticsearchRestTemplate.get(String.valueOf(id), clazz);
    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betType.getCode() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        log.info("listIdByMultipleRange query, betTypeEnum: {}, multipleRangeStart: {}, multipleRangeEnd: {}, typeEnum: {}", betType.getCode(), multipleRangeStart, multipleRangeEnd, templateType.getGameType());
        // 构建ES查询
        NativeSearchQuery query = BetRecordTemplateQueryBuilder.buildEsQuery(betType, multipleRangeStart, multipleRangeEnd);

        // 执行查询
        List<BetRecordTemplateEsDo> betRecordTemplateList = elasticsearchRestTemplate
                .search(query, BetRecordTemplateEsDo.class)
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return Optional.of(betRecordTemplateList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordTemplateEsDo::getId)
                .collect(Collectors.toList());
    }
}

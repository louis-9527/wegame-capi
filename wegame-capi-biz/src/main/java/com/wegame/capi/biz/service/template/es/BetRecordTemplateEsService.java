package com.wegame.capi.biz.service.template.es;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.common.TemplateConstants.MALAKAS_BET_RECORD;

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
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("betType", betType))
                .filter(QueryBuilders.rangeQuery("multiple").gt(multipleRangeStart).lte(multipleRangeEnd));
        // 只取id字段，不返回_source
        SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"id"}, null);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSourceFilter(sourceFilter)
                .build();
        // 查询
        SearchHits<Map> hits = elasticsearchRestTemplate.search(query, Map.class, indexCoord(templateType));
        // 提取id
        return hits.stream()
                .map(h -> Long.valueOf(h.getContent().get("id").toString()))
                .collect(Collectors.toList());
    }

    private IndexCoordinates indexCoord(TemplateTypeEnum templateType) {
        return IndexCoordinates.of(templateType.getBetRecordTemplateCollectionName());
    }
}

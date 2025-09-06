package com.wegame.capi.biz.service.template.es;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.template.BetRecordTemplateQueryBuilder;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BetRecordTemplateEsService<T extends BetRecordTemplateEsDo> {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public boolean add(TemplateTypeEnum templateType, T t) {
        T saveT = elasticsearchRestTemplate.save(t);
        return Objects.nonNull(saveT);
    }

    public T findById(TemplateTypeEnum templateType, Long id, Class<T> clazz) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(clazz, ErrorCode.ERR_PARAM);
        //        return elasticsearchRestTemplate.get(String.valueOf(id), clazz);
        //        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //        boolQuery.filter(QueryBuilders.termQuery("id", id));
        //        return elasticsearchRestTemplate.searchOne(new NativeSearchQueryBuilder()
        //                .withQuery(boolQuery)
        //                .build(), clazz)
        //                .getContent();
        //        restHighLevelClient.asyncSearch().get()
        try {
            // 1. 组装请求
            GetRequest getRequest = new GetRequest(templateType.getBetRecordTemplateName(), id.toString())
                    .fetchSourceContext(new FetchSourceContext(true,
                            new String[]{"id", "bytes"},   // 包含
                            null));
            long start = System.currentTimeMillis();
            // 2. 执行
            GetResponse resp = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            log.info("cost time: {} ms", System.currentTimeMillis() - start);
            // 3. 无结果返回 null
            if (!resp.isExists()) {
                return null;
            }
            // 4. 反序列化（JSON → 对象）
            return FastJsonUtils.fromJSONString(resp.getSourceAsString(), clazz);
        } catch (Exception e) {
            return null;
        }
    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betType.getCode() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd, Class<T> clazz) {
        log.info("listIdByMultipleRange query, betType: {}, multipleRangeStart: {}, multipleRangeEnd: {}, templateType: {}", betType.getCode(), multipleRangeStart, multipleRangeEnd, templateType.name());
        // 构建ES查询
        NativeSearchQuery query = BetRecordTemplateQueryBuilder.buildListIdEsQuery(betType, multipleRangeStart, multipleRangeEnd);

        // 执行查询
        List<T> betRecordTemplateList = elasticsearchRestTemplate
                .search(query, clazz)
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return Optional.of(betRecordTemplateList)
                .orElse(Collections.emptyList())
                .stream()
                .map(T::getId)
                .collect(Collectors.toList());
    }

    public List<T> listByIdList(TemplateTypeEnum templateType, List<Long> idList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        // 构造 terms 查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery("id", idList));
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(Pageable.unpaged())   // 一次拿完
                .build();

        SearchHits<T> hits = elasticsearchRestTemplate.search(query, clazz, IndexCoordinates.of(templateType.getBetRecordTemplateName()));

        return hits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @CachePut(value = CacheConstants.TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betRecordId")
    public T putTemplateLocalCache(TemplateTypeEnum templateType, Long betRecordId, T t) {
        // log.info("putLocalCache, betRecordId: {}", betRecordId);
        return t;
    }

    @CachePut(value = CacheConstants.TEMPLATE_REDIS, key = "#templateType.name() + '_' + #betRecordId")
    public T putTemplateRedisCache(TemplateTypeEnum templateType, Long betRecordId, T t) {
        // log.info("putRedisCache, betRecordId: {}", betRecordId);
        return t;
    }

    @Cacheable(value = CacheConstants.TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betRecordId")
    public T getTemplateWithLocalCache(TemplateTypeEnum templateType, Long betRecordId, Class<T> clazz) {
        // todo delete log
        log.info("getTemplateWithLocalCache, betRecordId: {}; templateType: {}", betRecordId, templateType.name());
        return findById(templateType, betRecordId, clazz);
    }

    @Cacheable(value = CacheConstants.TEMPLATE_REDIS, key = "#templateType.name() + '_' + #betRecordId")
    public T getTemplateWithRedisCache(TemplateTypeEnum templateType, Long betRecordId, Class<T> clazz) {
        // todo delete log
        log.info( "getTemplateWithRedisCache, betRecordId: {}; templateType: {}", betRecordId, templateType.name());
        return findById(templateType, betRecordId, clazz);
    }
}

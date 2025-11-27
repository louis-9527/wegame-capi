package com.wegame.capi.biz.service.template;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.config.es.EsRestClientConfig;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.BUILD_BEAN_FORMAT;

@Slf4j
@Service
@ConditionalOnBean(EsRestClientConfig.class)
public class BetRecordTemplateEsService<T extends BetRecordTemplateEsDo> implements InitializingBean {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(BUILD_BEAN_FORMAT, "BetRecordTemplateEsService");
    }

    public boolean add(TemplateTypeEnum templateType, T t) {
        T saveT = elasticsearchRestTemplate.save(t);
        return Objects.nonNull(saveT);
    }

    public T findById(TemplateTypeEnum templateType, Long id, Class<T> clazz) {
        long startTime = System.currentTimeMillis();
        log.info("findById query, id: {}, templateType: {}", id, templateType);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(clazz, ErrorCode.ERR_PARAM);
        //        return elasticsearchRestTemplate.get(String.valueOf(id), clazz);
        //                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //                        boolQuery.filter(QueryBuilders.termQuery("id", id));
        //                        return elasticsearchRestTemplate.searchOne(new NativeSearchQueryBuilder()
        //                                .withQuery(boolQuery)
        //                                .build(), clazz)
        //                                .getContent();
        //                        restHighLevelClient.asyncSearch().get()
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
            log.error("findById error, id: {}", id, e);
            return null;
        } finally {
            log.info("findId cost time: {} ms", System.currentTimeMillis() - startTime);
        }
    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betType.getCode() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd, Class<T> clazz) {
        log.info("listIdByMultipleRange query, betType: {}, multipleRangeStart: {}, multipleRangeEnd: {}, templateType: {}", betType.getCode(), multipleRangeStart, multipleRangeEnd, templateType);
        // 构建ES查询
        // NativeSearchQuery query = BetRecordTemplateQueryBuilder.buildListIdEsQuery(betType, multipleRangeStart, multipleRangeEnd);
        // 创建布尔查询构建器
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加betType条件
        boolQuery.filter(QueryBuilders.termQuery("betType", betType.getCode()));
        // 添加multiple范围条件
        if (multipleRangeStart.compareTo(multipleRangeEnd) == 0) {
            // 等于条件
            boolQuery.filter(QueryBuilders.termQuery("multiple", multipleRangeStart.doubleValue()));
        } else {
            // 范围条件 (大于start，小于等于end)
            boolQuery.filter(QueryBuilders.rangeQuery("multiple")
                    .gt(multipleRangeStart.doubleValue())
                    .lte(multipleRangeEnd.doubleValue()));
        }
        List<Long> result = new ArrayList<>();
        // 第一次不带 search_after
        Object[] searchAfter = null;
        do {
            List<Long> batch = fetchBatch(templateType, boolQuery, searchAfter, 5000);
            if (batch.isEmpty()) break;
            result.addAll(batch);
            searchAfter = new Object[]{batch.get(batch.size() - 1)}; // 最后一条 id
        } while (true);
        return result;
        //        // 执行查询
        //        List<T> betRecordTemplateList = elasticsearchRestTemplate
        //                .search(query, clazz)
        //                .stream()
        //                .map(SearchHit::getContent)
        //                .collect(Collectors.toList());
        //
        //        return Optional.of(betRecordTemplateList)
        //                .orElse(Collections.emptyList())
        //                .stream()
        //                .map(T::getId)
        //                .collect(Collectors.toList());

    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateType.name() + '_' + #betType.getCode() + '_'+ #betSubType.getCode()+'_'+ #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType, BetSubTypeEnum betSubType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd, Class<T> clazz) {
        log.info("listIdByMultipleRange query, betType: {},betSubType:{}, multipleRangeStart: {}, multipleRangeEnd: {}, templateType: {}",
                betType.getCode(), betSubType.getCode(), multipleRangeStart, multipleRangeEnd, templateType);
        // 构建ES查询
        // NativeSearchQuery query = BetRecordTemplateQueryBuilder.buildListIdEsQuery(betType, multipleRangeStart, multipleRangeEnd);
        // 创建布尔查询构建器
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加betType条件
        boolQuery.filter(QueryBuilders.termQuery("betType", betType.getCode()));
        boolQuery.filter(QueryBuilders.termQuery("betSubType", betSubType.getCode()));
        // 添加multiple范围条件
        if (multipleRangeStart.compareTo(multipleRangeEnd) == 0) {
            // 等于条件
            boolQuery.filter(QueryBuilders.termQuery("multiple", multipleRangeStart.doubleValue()));
        } else {
            // 范围条件 (大于start，小于等于end)
            boolQuery.filter(QueryBuilders.rangeQuery("multiple")
                    .gt(multipleRangeStart.doubleValue())
                    .lte(multipleRangeEnd.doubleValue()));
        }
        List<Long> result = new ArrayList<>();
        // 第一次不带 search_after
        Object[] searchAfter = null;
        do {
            List<Long> batch = fetchBatch(templateType, boolQuery, searchAfter, 5000);
            if (batch.isEmpty()) break;
            result.addAll(batch);
            searchAfter = new Object[]{batch.get(batch.size() - 1)}; // 最后一条 id
        } while (true);
        return result;
        //        // 执行查询
        //        List<T> betRecordTemplateList = elasticsearchRestTemplate
        //                .search(query, clazz)
        //                .stream()
        //                .map(SearchHit::getContent)
        //                .collect(Collectors.toList());
        //
        //        return Optional.of(betRecordTemplateList)
        //                .orElse(Collections.emptyList())
        //                .stream()
        //                .map(T::getId)
        //                .collect(Collectors.toList());

    }


    public List<T> listByIdList(TemplateTypeEnum templateType, List<Long> idList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }

        // 转换Long类型的ID为字符串（Elasticsearch的 _id 始终是字符串类型）
        List<String> idStrList = idList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 构造ids查询
        IdsQueryBuilder idsQuery = QueryBuilders.idsQuery().addIds(idStrList.toArray(new String[0]));
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(idsQuery)
                .withPageable(Pageable.unpaged())
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
        log.info("getTemplateWithLocalCache, betRecordId: {}; templateType: {}", betRecordId, templateType);
        return findById(templateType, betRecordId, clazz);
    }

    @Cacheable(value = CacheConstants.TEMPLATE_REDIS, key = "#templateType.name() + '_' + #betRecordId")
    public T getTemplateWithRedisCache(TemplateTypeEnum templateType, Long betRecordId, Class<T> clazz) {
        // todo delete log
        log.info("getTemplateWithRedisCache, betRecordId: {}; templateType: {}", betRecordId, templateType);
        return findById(templateType, betRecordId, clazz);
    }

    private List<Long> fetchBatch(TemplateTypeEnum templateType,
                                  BoolQueryBuilder boolQuery,
                                  Object[] searchAfter,
                                  int size) {
        try {
            // 只返回 id（doc value 字段）
            SearchSourceBuilder source = new SearchSourceBuilder()
                    .query(boolQuery)
                    .size(size)
                    // _source 不要
                    .fetchSource(false)
                    // 只取 id 列存
                    .docValueField("id")
                    .sort(SortBuilders.fieldSort("id").order(SortOrder.ASC));
            if (searchAfter != null) {
                source.searchAfter(searchAfter);
            }
            SearchRequest request = new SearchRequest(templateType.getBetRecordTemplateName())
                    .source(source);
//                    .preference("_primary");   // 可选：避免副本 reorder
            SearchResponse resp = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return Arrays.stream(resp.getHits().getHits())
                    .map(h -> (Long) h.getFields().get("id").getValue())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("fetchBatch error", e);
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }
}

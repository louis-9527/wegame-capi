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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.BUILD_BEAN_FORMAT;

/**
 * 投注记录模版服务： 从es中获取模版
 *
 * @param <T>
 */
@Slf4j
@Service
@ConditionalOnBean(EsRestClientConfig.class)
public class BetRecordTemplateService<T extends BetRecordTemplateEsDo> implements InitializingBean {

    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void afterPropertiesSet() {
        log.info(BUILD_BEAN_FORMAT, "BetRecordTemplateService");
    }

    public T findById(String templateIndexName, Long id, Class<T> clazz) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(clazz, ErrorCode.ERR_PARAM);
        try {
            // 1. 组装请求
            GetRequest getRequest = new GetRequest(templateIndexName, id.toString())
                    .fetchSourceContext(new FetchSourceContext(true,
                            new String[]{"id", "bytes", "multiple"},
                            //new String[]{"id", "bytes","multiple"},   // 包含
                            null));
            long start = System.currentTimeMillis();
            // 2. 执行
            GetResponse resp = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            // 3. 无结果返回 null
            if (!resp.isExists()) {
                return null;
            }
            // 4. 反序列化（JSON → 对象）
            return FastJsonUtils.fromJSONString(resp.getSourceAsString(), clazz);
        } catch (Exception e) {
            log.error("findById error, templateIndexName: {}, id: {}", templateIndexName, id, e);
            return null;
        } finally {
            log.info("findId templateIndexName: {}, id: {}, cost time: {} ms", templateIndexName, id, System.currentTimeMillis() - startTime);
        }
    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateIndexName + '_' + #betType.getCode() + '_' + #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(String templateIndexName, BetTypeEnum betType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        log.info("listIdByMultipleRange templateIndexName: {}, betType: {}, multipleRangeStart: {}, multipleRangeEnd: {}",
                templateIndexName,
                betType.getCode(),
                multipleRangeStart,
                multipleRangeEnd);
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
            List<Long> batch = fetchBatch(templateIndexName, boolQuery, searchAfter, 5000);
            if (batch.isEmpty()) break;
            result.addAll(batch);
            searchAfter = new Object[]{batch.get(batch.size() - 1)}; // 最后一条 id
        } while (true);
        return result;
    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateIndexName + '_' + #betType.getCode() + '_'+ #betSubType.getCode()+'_'+ #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByMultipleRange(String templateIndexName, BetTypeEnum betType, BetSubTypeEnum betSubType, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        log.info("listIdByMultipleRange templateIndexName: {}, betType: {},betSubType:{}, multipleRangeStart: {}, multipleRangeEnd: {}",
                templateIndexName,
                betType.getCode(),
                betSubType.getCode(),
                multipleRangeStart,
                multipleRangeEnd);
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
            List<Long> batch = fetchBatch(templateIndexName, boolQuery, searchAfter, 5000);
            if (batch.isEmpty()) break;
            result.addAll(batch);
            searchAfter = new Object[]{batch.get(batch.size() - 1)}; // 最后一条 id
        } while (true);
        return result;
    }

    @Cacheable(value = CacheConstants.BET_RECORD_TEMPLATE_LOCAL, key = "#templateIndexName + '_'+ #tag +'_'+ #multipleRangeStart + '_' + #multipleRangeEnd")
    public List<Long> listIdByTagAndMultipleRange(String templateIndexName, String tag, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd, Class<T> clazz) {
        log.info("listIdByMultipleRange templateIndexName: {}, tag: {}, multipleRangeStart: {}, multipleRangeEnd: {}",
                templateIndexName, tag, multipleRangeStart, multipleRangeEnd);
        // 创建布尔查询构建器
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加betType条件
        boolQuery.filter(QueryBuilders.termQuery("tag", tag));
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
            List<Long> batch = fetchBatch(templateIndexName, boolQuery, searchAfter, 5000);
            if (batch.isEmpty()) break;
            result.addAll(batch);
            searchAfter = new Object[]{batch.get(batch.size() - 1)}; // 最后一条 id
        } while (true);
        return result;
    }

    public List<T> listByIdList(String templateIndexName, List<Long> idList, Class<T> clazz) {
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
        SearchHits<T> hits = elasticsearchRestTemplate.search(query, clazz, IndexCoordinates.of(templateIndexName));
        return hits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @CachePut(value = CacheConstants.TEMPLATE_LOCAL, key = "#templateIndexName + '_' + #betRecordId")
    public T putTemplateLocalCache(String templateIndexName, Long betRecordId, T t) {
        return t;
    }

    //@CachePut(value = CacheConstants.TEMPLATE_REDIS, key = "#templateIndexName + '_' + #betRecordId")
    //public T putTemplateRedisCache(String templateIndexName, Long betRecordId, T t) {
    //    return t;
    //}

    @Cacheable(value = CacheConstants.TEMPLATE_LOCAL, key = "#templateIndexName + '_' + #betRecordId")
    public T getTemplateWithLocalCache(String templateIndexName, Long betRecordId, Class<T> clazz) {
        return findById(templateIndexName, betRecordId, clazz);
    }

    private List<Long> fetchBatch(String templateIndexName,
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
            SearchRequest request = new SearchRequest(templateIndexName)
                    .source(source);
            //.preference("_primary");   // 可选：避免副本 reorder
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

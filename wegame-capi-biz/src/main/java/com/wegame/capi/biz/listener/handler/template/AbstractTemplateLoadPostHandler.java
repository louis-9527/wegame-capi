package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.common.CommonTemplateConfig;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.listener.handler.AbstractPostHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 模版预加载后处理器
 */
@Slf4j
public abstract class AbstractTemplateLoadPostHandler<T extends BetRecordTemplateEsDo> extends AbstractPostHandler {


    public static final List<String> enableEnvList = Lists.newArrayList(
            EnvEnum.STS.getValue(),
            //EnvEnum.PRE.getValue(),
            EnvEnum.PROD.getValue()
    );

    @Autowired
    BetRecordTemplateService<T> betRecordTemplateService;

    public boolean supportWarmUpEnv() {
        return enableEnvList.contains(env);
    }

    public Class<T> getClassType() {
        // todo abstract this method
        return null;
    }

    public void commonWarmUp(CommonTemplateConfig config) {
        AssertUtil.assertNotNull(config, ErrorCode.ERR_INTER);
        Map<String, CommonTemplateConfig.Rtp> rtpMap = config.getRtpMap();
        Set<String> hasWarmUpSet = new HashSet<>();
        Map<String, Set<Long>> toWarmUpMap = new HashMap<>();
        // warmUp template IdList
        rtpMap.entrySet().forEach(item -> {
            String indexName = item.getValue().getIndexName();
            TreeMap<BetTypeEnum, TreeMap<String, Integer>> multipleRangeWeightMap = item.getValue().getMultipleRangeWeightMap();
            for (BetTypeEnum betType : BetTypeEnum.values()) {
                Map<String, Integer> weightMap = multipleRangeWeightMap.get(betType);
                weightMap.keySet().forEach(rangStr -> {
                    String warmUpKey = buildWarmUpKey(indexName, betType, rangStr);
                    if (!hasWarmUpSet.contains(warmUpKey)) {
                        log.warn("{} to warm up", warmUpKey);
                        List<Long> idList = loadTemplateIdList(config, indexName, betType, TemplateMultipleRange.build(rangStr));
                        hasWarmUpSet.add(warmUpKey);
                        if (CollectionUtils.isNotEmpty(idList)) {
                            if (toWarmUpMap.containsKey(indexName)) {
                                toWarmUpMap.get(indexName).addAll(idList);
                            } else {
                                toWarmUpMap.putIfAbsent(indexName, idList.stream().collect(Collectors.toSet()));
                            }
                        }
                    } else {
                        log.warn("{} has warm up", warmUpKey);
                    }
                });
            }
        });
        // warmUp template IdList
        toWarmUpMap.forEach((indexName, idList) -> {
            loadLocalCache(indexName, idList.stream().collect(Collectors.toList()), getClassType());
        });
    }

    public List<Long> loadTemplateIdList(CommonTemplateConfig commonTemplateConfig,
                                         String templateIndexName,
                                         BetTypeEnum betType,
                                         TemplateMultipleRange multipleRange) {
        log.info("templateIdList load start, templateIndexName: {}, range: {}", templateIndexName, multipleRange);
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateService.listIdByMultipleRange(templateIndexName, betType, multipleRange.getStart(), multipleRange.getEnd());
            if (!commonTemplateConfig.supportWarmUp(betType, multipleRange)) {
                log.info("templateIdList load end, templateIndexName: {}, range: {}, size: {}, no load betRecordTemplate", templateIndexName, multipleRange, betRecordTemplateIdList.size());
                return null;
            }
            log.info("templateIdList load success, templateIndexName: {}, range: {}, size: {}", templateIndexName, multipleRange, betRecordTemplateIdList.size());
            return betRecordTemplateIdList;
        } catch (Exception e) {
            log.info("templateIdList load error, templateIndexName: {}, range: {}", templateIndexName, multipleRange, e);
            return null;
        }
    }

    public void loadLocalCache(String templateIndexName, List<Long> templateIdList, Class<T> clazz) {
        if (!supportWarmUpEnv()) {
            return;
        }
        try {
            if (CollectionUtil.isNotEmpty(templateIdList)) {
                log.info("loadLocalCache start, templateIndexName: {},  size: {}", templateIndexName, templateIdList.size());
                List<List<Long>> multiList = ListUtil.partition(templateIdList, 1000);
                multiList.forEach(idList -> {
                    List<T> templateList = betRecordTemplateService.listByIdList(templateIndexName, idList, clazz);
                    templateList.forEach(templateItem -> {
                        betRecordTemplateService.putTemplateLocalCache(templateIndexName, templateItem.getId(), templateItem);
                    });
                });
                log.info("loadLocalCache success, templateIndexName: {}, size: {}", templateIndexName, templateIdList.size());
            }
        } catch (Exception e) {
            log.warn("loadLocalCache error, templateIndexName: {}", templateIndexName, e);
        }
    }

    private String buildWarmUpKey(String indexName, BetTypeEnum betType, String rangStr) {
        // indexName_betType_rangStr
        return String.format("%s_%s_%s", indexName, betType.getCode(), rangStr);
    }
}

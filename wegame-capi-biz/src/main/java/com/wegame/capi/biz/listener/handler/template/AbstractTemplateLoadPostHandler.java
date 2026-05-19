package com.wegame.capi.biz.listener.handler.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.EnvEnum;
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
        // todo abstract
        return null;
    }

    public void commonWarmUp(CommonTemplateConfig commonTemplateConfig) {
        Map<String, CommonTemplateConfig.Rtp> rtpMap = commonTemplateConfig.getRtpMap();
        Set<String> hasWarmUpSet = new HashSet<>();
        Map<String, Set<Long>> needWarmUpMap = new HashMap<>();
        // warmUp template IdList
        rtpMap.entrySet().forEach(item -> {
            CommonTemplateConfig.Rtp rtp = item.getValue();
            String indexName = rtp.getIndexName();
            TreeMap<BetTypeEnum, TreeMap<String, Integer>> multipleRangeWeightMap = rtp.getMultipleRangeWeightMap();
            for (BetTypeEnum betType : BetTypeEnum.values()) {
                Map<String, Integer> weightMap = multipleRangeWeightMap.get(betType);
                weightMap.keySet().forEach(rangStr -> {
                    String warmUpKey = buildWarmUpKey(indexName, betType, rangStr);
                    if (!hasWarmUpSet.contains(warmUpKey)) {
                        List<Long> idList = loadTemplateIdList(commonTemplateConfig, indexName, betType, TemplateMultipleRange.build(rangStr));
                        hasWarmUpSet.add(warmUpKey);
                        if (CollectionUtils.isNotEmpty(idList)) {
                            if (needWarmUpMap.containsKey(indexName)) {
                                needWarmUpMap.get(indexName).addAll(idList);
                            } else {
                                needWarmUpMap.putIfAbsent(indexName, idList.stream().collect(Collectors.toSet()));
                            }
                        }
                    }
                });
            }
        });
        // warmUp template IdList
        needWarmUpMap.forEach((indexName, idList) -> {
            loadLocalCache(indexName, idList.stream().collect(Collectors.toList()), getClassType());
        });
    }

    public List<Long> loadTemplateIdList(CommonTemplateConfig commonTemplateConfig,
                                         String templateIndexName,
                                         BetTypeEnum betType,
                                         TemplateMultipleRange multipleRange) {
        log.info("templateIdList load start, templateIndexName: {}, range: {}-{}... {}", templateIndexName, betType, multipleRange.getStart(), multipleRange.getEnd());
        try {
            List<Long> betRecordTemplateIdList = betRecordTemplateService.listIdByMultipleRange(templateIndexName, betType, multipleRange.getStart(), multipleRange.getEnd());
            if (!commonTemplateConfig.supportWarmUp(betType, multipleRange)) {
                log.info("templateIdList load end, templateIndexName: {}, range: {}-{}, size: {}，{}, no load betRecordTemplate", templateIndexName, betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
                return null;
            }
            log.info("templateIdList load success, templateIndexName: {}, range: {}-{}, size: {}, {}", templateIndexName, betType, multipleRange.getStart(), multipleRange.getEnd(), betRecordTemplateIdList.size());
            return betRecordTemplateIdList;
        } catch (Exception e) {
            log.info("templateIdList load error, templateIndexName: {}, range: {}-{}, {}", templateIndexName, betType, multipleRange.getStart(), multipleRange.getEnd(), e);
            return null;
        }
    }

    public void loadLocalCache(String templateIndexName, List<Long> templateIdList, Class<T> clazz) {
        if (!supportWarmUpEnv()) {
            return;
        }
        try {
            if (CollectionUtil.isNotEmpty(templateIdList)) {
                log.info("loadLocalCache start, templateIndexName: {}, betType: {},  size: {}", templateIndexName, templateIdList.size());
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

    /**
     * indexName_betType_rangStr
     */
    private String buildWarmUpKey(String indexName, BetTypeEnum betType, String rangStr) {
        return String.format("%s_%s_%s", indexName, betType.getCode(), rangStr);
    }
}

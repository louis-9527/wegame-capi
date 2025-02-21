//package com.wegame.capi.scheduler.event;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.wegame.capi.biz.service.DynamicOperationEventService;
//import com.wegame.capi.client.enums.GameTypeEnum;
//import com.wegame.capi.common.enums.BetStatusEnum;
//import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
//import com.wegame.capi.scheduler.AbstractScheduler;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Component
//public abstract class AbstractDynamicOperationEventScheduler extends AbstractScheduler implements Runnable {
//    @Autowired
//    private DynamicOperationEventService dynamicOperationEventService;
//
//
//    abstract GameTypeEnum getGameType();
//
//    @Override
//    public void run() {
//        log.info("AbstractDynamicOperationEventScheduler#schedule run start:{}", getGameType().getId());
//        long fiveMinutesAgo = System.currentTimeMillis() - 5 * 60 * 1000;
//        int pageNo = 1;
//        Long lastId = null;
//        List<Long> recordIdList;
//        do {
//            recordIdList = betRecordService.listId(BetRecordQuery.builder().status(BetStatusEnum.ING.getCode())
//                    .endCreateTime(fiveMinutesAgo)
//                    .limit(DEFAULT_PAGE_SIZE)
//                    .offset((pageNo - 1) * DEFAULT_PAGE_SIZE)
//                    .lastId(lastId)
//                    .build(), this.getGameType());
//            if (CollectionUtil.isNotEmpty(recordIdList)) {
//                lastId = recordIdList.get(recordIdList.size() - 1);
//            }
//            Optional.ofNullable(recordIdList)
//                    .orElse(Collections.emptyList())
//                    .forEach(this::handle);
//            pageNo++;
//        } while (CollectionUtils.isNotEmpty(recordIdList));
//        log.info("MariaMakiLingBetRecordBetStatusScheduler#schedule run end");
//    }
//}

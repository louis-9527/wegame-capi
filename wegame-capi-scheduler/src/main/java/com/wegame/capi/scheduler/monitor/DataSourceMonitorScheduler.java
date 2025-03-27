//package com.wegame.capi.scheduler;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.wegame.base.utils.FastJsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * @author apple
// */
//@Slf4j
//@Service
//public class DataSourceMonitorScheduler {
//
//    @Autowired
//    List<DruidDataSource> druidDataSourceList;
//
//    /**
//     * 定时任务
//     */
//     @Scheduled(cron = "0 0/3 * * * ?")
//    public void schedule() {
//        statistic();
//    }
//
//    public void statistic() {
//        if (CollectionUtils.isEmpty(druidDataSourceList)) {
//            return;
//        }
//        druidDataSourceList.forEach(item -> {
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("maxActive", item.getMaxActive());
//            map.put("maxIdle", item.getMaxIdle());
//            map.put("connectCount", item.getConnectCount());
//            log.info("dataSourcePool: {}, info: {}", item.getName(), FastJsonUtils.toJSONString(map));
//        });
//    }
//
//}

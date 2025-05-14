package com.wegame.capi.scheduler.starcard;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.promotion.StarCardUseRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.StarCardReportStatusEnum;
import com.wegame.capi.common.enums.StarCardUseStatusEnum;
import com.wegame.capi.dao.repository.domain.star.StarCardUseRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 抽象星卡使用定时任务
 */
@Slf4j
@Component
public abstract class AbstractStarCardScheduler extends AbstractScheduler
        implements Runnable {

    private final long THREE_MINUTES_AGO = System.currentTimeMillis() - 3 * 60 * 1000;
    private final long MONTH_DAY_AGO = System.currentTimeMillis() - (3L * 24 * 60 * 60 * 1000);

    @Autowired
    private StarCardUseRecordService starCardUseRecordService;
    @Autowired
    public BetRecordService betRecordService;

    abstract GameTypeEnum gameTypeEnum();

    @Override
    public void run() {
        // 查询前一天到前3分钟上报状态为待上报
        try {
            log.info("AbstractStarCardScheduler run start. gameId: {}", gameTypeEnum().getId());
            long startTime = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<StarCardUseRecordDo> detailList;
            StarCardUseRecordQuery query = StarCardUseRecordQuery.builder()
                    .reportStatus(StarCardReportStatusEnum.ING.getCode())
                    .ltCreateTime(THREE_MINUTES_AGO)
                    .gteCreateTime(MONTH_DAY_AGO)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE)
                    .build();
            do {
                query.setGtLastId(lastId);
                detailList = starCardUseRecordService.list(query, gameTypeEnum());
                if (CollectionUtil.isNotEmpty(detailList)) {
                    lastId = detailList.get(detailList.size() - 1).getId();
                }
                Optional.ofNullable(detailList)
                        .orElse(Collections.emptyList())
                        .forEach(this::execUseStatusIngHandle);
            } while (CollectionUtils.isNotEmpty(detailList));
            log.info("AbstractStarCardScheduler run end:{},time:{}",
                    gameTypeEnum().getId(),
                    DateUtil.getCurrentMilli() - startTime);
        } catch (Exception e) {
            log.error("AbstractStarCardScheduler run exception", e);
        }
    }


    /**
     * 执行处理中事件
     */
    private void execUseStatusIngHandle(StarCardUseRecordDo useRecordDo) {
        try {
            Long betRecordId = useRecordDo.getBetRecordId();
            if (Objects.isNull(betRecordId)) {
                return;
            }
            // 查询原始订单的状态
            BetRecordDo recordDo = betRecordService.get(betRecordId, gameTypeEnum());
            StarCardReportStatusEnum starCardReportStatus = null;
            StarCardUseStatusEnum starCardUseStatus = null;

            if (recordDo == null) {
                // 未找到投注记录,
                if (StarCardUseStatusEnum.ING == useRecordDo.getUseStatus()) {
                    // todo 使用状态为使用中,需要查询星卡真实使用状态
                    starCardUseStatus = StarCardUseStatusEnum.FAIL;
                } else if (StarCardUseStatusEnum.SUCCESS == useRecordDo.getUseStatus()) {
                    //todo 上报
                    starCardReportStatus = StarCardReportStatusEnum.ING;
                } else {
                    // 无须上报
                    starCardReportStatus = StarCardReportStatusEnum.NONE;
                }
            } else {
                if (Objects.equals(BetStatusEnum.ING.getCode(), recordDo.getStatus())) {
                    return;
                }
                if (Objects.equals(BetStatusEnum.FAIL.getCode(), recordDo.getStatus())) {
                    if (StarCardUseStatusEnum.SUCCESS == useRecordDo.getUseStatus()) {
                        starCardReportStatus = StarCardReportStatusEnum.ING;
                    } else {
                        starCardReportStatus = StarCardReportStatusEnum.NONE;
                    }
                } else if (Objects.equals(BetStatusEnum.SUCCESS.getCode(), recordDo.getStatus())) {
                    if (StarCardUseStatusEnum.SUCCESS == useRecordDo.getUseStatus()) {
                        starCardReportStatus = StarCardReportStatusEnum.NONE;
                    }
                }
            }
            StarCardUseRecordDo useUpdate = StarCardUseRecordDo.builder()
                    .id(useRecordDo.getId())
                    .reportStatus(starCardReportStatus)
                    .useStatus(starCardUseStatus)
                    .failCode(-1)
                    .failMsg("bet error")
                    .build();
            starCardUseRecordService.update(useUpdate, gameTypeEnum());
        } catch (Exception e) {
            log.error("eventDetailFailHandle exception", e);
        }
    }
}

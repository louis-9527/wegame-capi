package com.wegame.capi.biz.service;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletBetStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.req.BetStatusReqBuilder.buildStatusReq;
import static com.wegame.capi.biz.converter.BetRecordConverter.*;
import static com.wegame.capi.common.enums.SingleWalletBetStatusEnum.valueOfCode;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.betRecordThreadPoolExecutor;

/**
 * 投注状态服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetStatusService {
    @Autowired
    SingleWalletService singleWalletService;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    PulsarProducer pulsarProducer;

    /**
     * 扭转投注状态
     * @param betRecordDo
     */
    public void convertBetStatus(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        try {
            if (!BetStatusEnum.ING.getCode().equals(betRecordDo.getStatus())) {
                return;
            }
            log.info("handle in betting BetRecordDo, betRecordId: {}", betRecordDo.getId());
            // 获取当前订单状态
            BetStatusRes betStatusRes = singleWalletService.getBetStatus(buildStatusReq(betRecordDo));
            AssertUtil.assertNotNull(betStatusRes, ErrorCode.ERR_INTER);
            SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
            AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid single wallet bet status code");
            BetStatusEnum betStatus = BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus);

            if (Objects.equals(BetStatusEnum.SUCCESS, betStatus)) {
                // 更新记录: 状态更新为成功
                betRecordService.update(BetRecordDo.builder()
                        .id(betRecordDo.getId())
                        .betId(betStatusRes.getBetId())
                        .status(BetStatusEnum.SUCCESS.getCode())
                        .build(), gameType);
            } else if (Objects.equals(BetStatusEnum.FAIL, betStatus)) {
                // 更新记录: 状态更新为失败
                betRecordService.update(BetRecordDo.builder()
                                .id(betRecordDo.getId())
                                .betId(betStatusRes.getBetId())
                                .status(BetStatusEnum.FAIL.getCode())
                                .build(), gameType);
            } else {
                log.error("handle invalid betStatus: {}, betRecordId: {}, betStatusRes: {}",
                        betStatus,
                        betRecordDo.getId(),
                        betStatusRes);
            }
        } catch (Exception e) {
            log.error("handle in betting BetRecordDo error, betRecordId: {}, errMsg: {}", betRecordDo.getId(), e.getMessage(), e);
        }
    }

    @Async(betRecordThreadPoolExecutor)
    public void asyncConvertBetStatus(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        convertBetStatus(betRecordDo, gameType);
    }
}
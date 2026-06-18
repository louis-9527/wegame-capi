package com.wegame.capi.biz.module.diawatamayari.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.RetryUtil;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.config.domain.PayoutAmountLimitConfig;
import com.wegame.capi.biz.config.domain.common.CommonTemplateConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.malakas.DiwataBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractGameTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.common.Constants.DIWATA_MAYARI_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.utils.ListUtil.getRandomElement;

@Slf4j
@Component("diwataMayariGameTemplatePlay")
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariGameTemplatePlay extends AbstractGameTemplatePlay<DiwataMayariGameRecordDo, DiwataBetRecordTemplateEsDo> {
    private static final String DEFAULT_SERVICE_NAME = "default";
    @Autowired
    DiwataMayariConfig diwataMayariConfig;
    @Autowired
    DiwataMayariGameCommonTemplatePlay diwataMayariGameCommonTemplatePlay;

    @Override
    public TemplateGame templateGame() {
        return diwataMayariGameCommonTemplatePlay;
    }

    @Override
    public DiwataBetRecordTemplateEsDo getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        CommonTemplateConfig.Rtp rtp = getRtpWithBloodPoolControl(betDo, gamePlayDo, diwataMayariConfig.getCommonBloodPoolConfig(), diwataMayariConfig.getCommonTemplateConfig());
        AssertUtil.assertNotNull(rtp, ErrorCode.ERR_INTER);
        PayoutAmountLimitConfig config = diwataMayariConfig.getPayoutAmountLimitConfig();
        return RetryUtil.execute(() -> fetchTemplate(betDo, gamePlayDo, rtp),
                config.getRetryCount(),
                RetryException.class,
                DEFAULT_SERVICE_NAME);
    }

    // todo use parent method
    private DiwataBetRecordTemplateEsDo fetchTemplate(BetDo betDo, GamePlayDo gamePlayDo, CommonTemplateConfig.Rtp rtp) {
        String templateIndexName = rtp.getIndexName();
        AssertUtil.assertStrNotEmpty(templateIndexName, ErrorCode.ERR_INTER);
        TemplateMultipleRange multipleRange = getTemplateMultipleRange(rtp.getMultipleRangeWeightMap().get(betDo.getBetType()));
        AssertUtil.assertNotNull(multipleRange, ErrorCode.ERR_INTER);
        gamePlayDo.setTemplateMultipleRange(multipleRange);
        List<Long> betRecordIdList = betRecordTemplateService.listIdByMultipleRange(templateIndexName, gamePlayDo.getBetType(), multipleRange.getStart(), multipleRange.getEnd());
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("match template, templateIndexName: {}, betRecordId:{}", templateIndexName, betRecordId);
        DiwataBetRecordTemplateEsDo templateEsDo;
        if (diwataMayariConfig.getCommonTemplateConfig().supportWarmUp(gamePlayDo.getBetType(), multipleRange)) {
            templateEsDo = betRecordTemplateService.getTemplateWithLocalCache(templateIndexName, betRecordId, DiwataBetRecordTemplateEsDo.class);
        } else {
            templateEsDo = betRecordTemplateService.findById(templateIndexName, betRecordId, DiwataBetRecordTemplateEsDo.class);
        }
        templateValidate(betDo, templateEsDo);
        return templateEsDo;
    }

    private void templateValidate(BetDo betDo, DiwataBetRecordTemplateEsDo templateEsDo) {
        if (Objects.nonNull(betDo.getBetBloodPoolControl()) && Objects.nonNull(betDo.getBetBloodPoolControl().getMatchBloodPoolConfig())) {
            BigDecimal singlePayoutLimit = betDo.getBetBloodPoolControl().getMatchBloodPoolConfig().getSinglePayoutLimit();
            BigDecimal currentPayOutAmount = new BigDecimal(betDo.getPayAmount()).divide(new BigDecimal(1000)).multiply(new BigDecimal(templateEsDo.getMultiple()));
            if (currentPayOutAmount.compareTo(singlePayoutLimit) > 0) {
                log.warn("InBloodControl payOutAmount exceed limit, betDo: {}, bloodConfig: {}, templateId: {}, multiple: {}",
                        betDo,
                        betDo.getBetBloodPoolControl().getMatchBloodPoolConfig(),
                        templateEsDo.getId(),
                        templateEsDo.getMultiple());
                throw new RetryException(ErrorCode.ERR_INTER);
            }
        } else {
            PayoutAmountLimitConfig payoutAmountLimitConfig = diwataMayariConfig.getPayoutAmountLimitConfig();
            List<BetTypeEnum> limitBetTypeList = payoutAmountLimitConfig.getLimitBetTypeList();
            long limitAmount = payoutAmountLimitConfig.getLimitAmount();
            if (!limitBetTypeList.contains(betDo.getBetType()) || limitAmount <= 0) {
                // 无任何限制
                return;
            }
            double currentPayOutAmount = betDo.getPayAmount() * templateEsDo.getMultiple();
            if (currentPayOutAmount > limitAmount) {
                log.warn("OutBloodControl payOutAmount exceed limit, betDo: {}, templateId: {}, multiple: {}",
                        betDo,
                        templateEsDo.getId(),
                        templateEsDo.getMultiple());
                throw new RetryException(ErrorCode.ERR_INTER);
            }
        }
    }
}
package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.promotion.GetTreasureReq;
import com.wegame.capi.api.domain.req.promotion.RedeemTreasureReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.GetTreasureRes;
import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.common.enums.SingleWalletPromotionResStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Objects;

/**
 * 宝箱api
 * @see <a href="https://casinoplus.sg.larksuite.com/docx/XZzMdmW4aonhYfxtMEili1qGgkb"/>
 */
@Slf4j
@Component
public class TreasureApi extends PromotionBaseApi {

    private static final String GET_TREASURE = "%s/getTreasure";
    private static final String REDEEM_TREASURE = "%s/redeemTreasure";

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public GetTreasureRes getTreasure(GetTreasureReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<GetTreasureReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_TREASURE, promotionDomain)));
            ResponseEntity<CommonRes<GetTreasureRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetTreasureRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("getTreasure BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("getTreasure Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public RedeemTreasureRes redeemTreasure(RedeemTreasureReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<RedeemTreasureReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(REDEEM_TREASURE, promotionDomain)));
            ResponseEntity<CommonRes<RedeemTreasureRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<RedeemTreasureRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            // todo 麦冬：特殊处理，后续单钱包接口支持幂等后移除
            if (Objects.equals(responseEntity.getBody().getStatus(), SingleWalletPromotionResStatusEnum.TREASURE_ALREADY_REDEEMED.getCode())) {
                throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_TREASURE_REDEEMED, responseEntity.getBody().getMessage());
            }
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("redeemTreasure BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("redeemTreasure Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}

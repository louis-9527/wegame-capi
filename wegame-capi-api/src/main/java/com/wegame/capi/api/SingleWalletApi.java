package com.wegame.capi.api;

import com.wegame.capi.api.domain.req.singlewallet.*;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.*;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.singlewallet.*;
import com.wegame.capi.api.domain.res.singlewallet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * @author jayni
 * SingleWallet client
 */
@Slf4j
//todo 正式对接之后，注解打开@jayni
@Component
public class SingleWalletApi extends BaseApi {

    public static final String GET_PAYOUT_URL_FORMAT = "%s/payout";
    public static final String BOXING_BET_URL_FORMAT = "%s/boxingBet";
    public static final String BOXING_PAYOUT_URL_FORMAT = "%s/boxingPayout";
    public static final String BOXING_BET_STATUS_URL_FORMAT = "%s/betStatus";
    public static final String JACKPOT_URL_FORMAT = "%s/jackpot";

    // todo fix
    private static final String apiSingleBoxingPrivateKey = "6655960c95e6513735a4e0d21b643eae4c03ffe2166c872d9325674b69b8192a";
    private static final String apiSingleBoxingPrivateIv = "cd54bccde37b56cb0788acf5c74c4f28";

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public AwardRes award(AwardReq req) {
        try {
            // 参数签名
            assembleSingleBoxingSignature(req);
            RequestEntity<AwardReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(GET_PAYOUT_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<AwardRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<AwardRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("payOut business err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("payOut err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    /**
     * 拳击投注
     *
     * @param req req
     * @return response
     */
    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public SingleWalletCommonRes<BoxingBetRes> boxingBet(BoxingBetReq req) {
        try {
            // 参数签名
            assembleSingleBoxingSignature(req);
            RequestEntity<BoxingBetReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(BOXING_BET_URL_FORMAT, singleDomain)));
            ResponseEntity<SingleWalletCommonRes<BoxingBetRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<SingleWalletCommonRes<BoxingBetRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody();
        } catch (BusinessException be) {
            log.error("boxing bet business err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("boxing bet err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    /**
     * 拳击派彩
     *
     * @param req req
     * @return response
     */
    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public SingleWalletCommonRes<BoxingPayoutRes> boxingPayout(BoxingPayoutReq req) {
        try {
            // 参数签名
            assembleSingleBoxingSignature(req);
            RequestEntity<BoxingPayoutReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(BOXING_PAYOUT_URL_FORMAT, singleDomain)));
            ResponseEntity<SingleWalletCommonRes<BoxingPayoutRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<SingleWalletCommonRes<BoxingPayoutRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody();
        } catch (BusinessException be) {
            log.error("boxing payOut business err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        }
        //发生任何异常重试，一定要成功
        catch (Exception e) {
            log.error("boxing payOut err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }


    /**
     * 单钱包状态查询
     *
     * @param req req
     * @return response
     */
    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public SingleWalletCommonRes<BoxingBetStatusRes> boxingBetStatus(BoxingBetStatusReq req) {
        try {
            // 参数签名
            assembleSingleBoxingSignature(req);
            RequestEntity<BoxingBetStatusReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(BOXING_BET_STATUS_URL_FORMAT, singleDomain)));
            ResponseEntity<SingleWalletCommonRes<BoxingBetStatusRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<SingleWalletCommonRes<BoxingBetStatusRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody();
        } catch (BusinessException be) {
            log.error("boxing betStatus business err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        }
        //发生任何异常重试，一定要成功
        catch (Exception e) {
            log.error("boxing betStatus err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }


    /**
     * 拳击派彩
     *
     * @param request request
     * @return response
     */
    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public JackpotRes jackpot(JackpotReq request) {
        try {
            // 参数签名
            assembleSingleBoxingSignature(request);
            RequestEntity<JackpotReq> requestEntity = new RequestEntity<>(request, HttpMethod.POST,
                    URI.create(String.format(JACKPOT_URL_FORMAT, singleDomain)));
            ResponseEntity<SingleWalletCommonRes<JackpotRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<SingleWalletCommonRes<JackpotRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("jackpot business err, req: {}, errMsg: {}", request, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("jackpot err, req: {}, errMsg: {}", request, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }


    private void assembleSingleBoxingSignature(SingleWalletBaseReq req) {
        String preSignature = String.format("%s%s%s%s", req.getChannelId(), req.getPlayerName(), req.getCreateTime(), req.getRefId());
        String encrypt = SingleWalletSignatureUtil.encrypt(preSignature, apiSingleBoxingPrivateKey, apiSingleBoxingPrivateIv);
        AssertUtil.assertNotNull(encrypt, ErrorCode.ERR_PARAM);
        req.setHash(encrypt);
    }
}

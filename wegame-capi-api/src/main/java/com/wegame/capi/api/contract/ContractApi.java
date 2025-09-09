package com.wegame.capi.api.contract;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.contract.GetPlayerContractReq;
import com.wegame.capi.api.domain.req.contract.RedeemPlayerContractReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.contract.GetPlayerContractRes;
import com.wegame.capi.api.domain.res.contract.RedeemPlayerContractRes;
import com.wegame.capi.common.enums.AchievementErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * 成就api
 * @see <a href="https://casinoplus.sg.larksuite.com/docx/XZzMdmW4aonhYfxtMEili1qGgkb"/>
 */
@Slf4j
@Component
public class ContractApi extends PromotionBaseApi {

    private static final String REDEEM_PLAYER_CONTRACT_URL_FORMAT = "%s/redeemPlayerContract";
    private static final String GET_PLAYER_CONTRACT_URL_FORMAT = "%s/getPlayerContract";

    @Retryable(value = RetryException.class, maxAttempts = 2, exclude = BusinessException.class)
    public GetPlayerContractRes getPlayerContract(GetPlayerContractReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<GetPlayerContractReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_PLAYER_CONTRACT_URL_FORMAT, promotionDomain)));
            ResponseEntity<CommonRes<GetPlayerContractRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetPlayerContractRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                AchievementErrorCode achievementErrorCode = AchievementErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
                AssertUtil.assertNotNull(achievementErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
                throw new BusinessException(achievementErrorCode.getErrorCode(), achievementErrorCode.getMsg());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("getPlayerContract BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("getPlayerContract Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    public RedeemPlayerContractRes redeemPlayerContract(RedeemPlayerContractReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<RedeemPlayerContractReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(REDEEM_PLAYER_CONTRACT_URL_FORMAT, promotionDomain)));
            ResponseEntity<CommonRes<RedeemPlayerContractRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<RedeemPlayerContractRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                AchievementErrorCode achievementErrorCode = AchievementErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
                AssertUtil.assertNotNull(achievementErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
                throw new BusinessException(achievementErrorCode.getErrorCode(), achievementErrorCode.getMsg());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("redeemPlayerContract BusinessException, req: {}, errorCode: {}, errMsg: {}", req, e.getErrorCode().getCode(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("redeemPlayerContract Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}

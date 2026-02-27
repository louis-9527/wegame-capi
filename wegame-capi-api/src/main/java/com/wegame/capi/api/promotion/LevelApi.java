package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.contract.GetPlayerContractReq;
import com.wegame.capi.api.domain.req.contract.RedeemPlayerContractReq;
import com.wegame.capi.api.domain.req.level.LevelConfigReq;
import com.wegame.capi.api.domain.req.level.PlayerLevelReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.contract.GetPlayerContractRes;
import com.wegame.capi.api.domain.res.contract.RedeemPlayerContractRes;
import com.wegame.capi.api.domain.res.promotion.level.LevelConfigRes;
import com.wegame.capi.api.domain.res.promotion.level.PlayerLevelRes;
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
 * 等级 Level-api
 *
 * @see https://casinoplus.sg.larksuite.com/docx/Dkmbd9iCgorf3HxeqBrlULplgYb
 */
@Slf4j
@Component
public class LevelApi extends PromotionBaseApi {

    private static final String LIST_LEVEL_CONFIG_URL_FORMAT = "%s/getLevelConfig";
    private static final String GET_PLAYER_LEVEL_URL_FORMAT = "%s/getPlayerLevel";

    @Retryable(value = RetryException.class, maxAttempts = 2, exclude = BusinessException.class)
    public LevelConfigRes getLevelConfig(LevelConfigReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<LevelConfigReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(LIST_LEVEL_CONFIG_URL_FORMAT, promotionDomain(req.getPId()))));
            ResponseEntity<CommonRes<LevelConfigRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<LevelConfigRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                throw new BusinessException(PROMOTION_PREFIX_ERR_CODE + responseEntity.getBody().getStatus(), responseEntity.getBody().getMessage());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("getLevelConfig BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("getLevelConfig Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    @Retryable(value = RetryException.class, maxAttempts = 2, exclude = BusinessException.class)
    public PlayerLevelRes getPlayerLevel(PlayerLevelReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<PlayerLevelReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_PLAYER_LEVEL_URL_FORMAT, promotionDomain(req.getPId()))));
            ResponseEntity<CommonRes<PlayerLevelRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<PlayerLevelRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                throw new BusinessException(PROMOTION_PREFIX_ERR_CODE + responseEntity.getBody().getStatus(), responseEntity.getBody().getMessage());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("getPlayerLevel BusinessException, req: {}, errorCode: {}, errMsg: {}", req, e.getErrCode(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("getPlayerLevel Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}

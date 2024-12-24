package com.wegame.capi.api;

import com.wegame.capi.api.domain.req.GetAuthLoginTokenReq;
import com.wegame.capi.api.domain.res.AuthLoginTokenRes;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.common.annotation.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Client
@Component
public class CommonApi extends BaseApi {
    public static final String AUTH_LOGIN_TOKEN_URL_FORMAT = "%s/authLoginToken";

    public AuthLoginTokenRes authLoginToken(GetAuthLoginTokenReq request) {
        try {
            //设置token签名
            String preSignature = String.format("%s%s%s%s", request.getChannelId(), request.getPlayerName(),request.getCreateTime(), request.getRefId());
            String key = StringUtils.equals(String.valueOf(request.getPId()), cpPlatformId) ? cpPrivateKey : igoPrivateKey;
            String iv = StringUtils.equals(String.valueOf(request.getPId()), cpPlatformId) ? cpPrivateIv : igoPrivateIv;
            log.info("platformId: {}; preSignature: {}; key: {}; iv: {}", request.getPId(), preSignature, key, iv);
            String encrypt = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
            AssertUtil.assertNotNull(encrypt, ErrorCode.ERR_PARAM);
            request.setHash(encrypt);
            RequestEntity<GetAuthLoginTokenReq> requestEntity = new RequestEntity<>(request, HttpMethod.POST,
                    URI.create(String.format(AUTH_LOGIN_TOKEN_URL_FORMAT, singleDomain)));

            ResponseEntity<CommonRes<AuthLoginTokenRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<AuthLoginTokenRes>>() {});
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (Exception e) {
            log.error("authTokenLogin err, req: {}, errMsg: {}", request, e.getMessage(), e);
            throw e;
        }
    }
}

package com.wegame.capi.api;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.SingleWalletBaseApi;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsStatusReq;
import com.wegame.capi.api.domain.res.dynamicoperation.UpdateEventsStatusRes;
import com.wegame.capi.api.helper.DynamicOperationEncryptionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

@Slf4j
@Component
public class DynamicOperationApi extends SingleWalletBaseApi {
    private static final String UPDATE_EVENTS_STATUS = "%s/updateEventsStatus";
    @Autowired
    DynamicOperationEncryptionHelper dynamicOperationEncryptionHelper;
    @Value("${dynamic.operation.domain}")
    String dynamicOperationDomain;

    /**
     * 游戏状态上报
     */
    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public UpdateEventsStatusRes updateEventsStatus(UpdateEventsStatusReq req) {
        try {
            // 参数加密
            String reqString = JSON.toJSONString(req);
            String sign = SecureUtil.md5(reqString);
            String keyString = dynamicOperationEncryptionHelper.encryptWithPublicKey(cpPrivateKey);
            String ivString = dynamicOperationEncryptionHelper.encryptWithPublicKey(cpPrivateIv);
            // 构建header
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            headers.add("Keep-Alive", "timeout=180");
            headers.add("sign", sign);
            headers.add("keyString", keyString);
            headers.add("ivString", ivString);
            UpdateEventsStatusReq.DynamicOperationReq operationReq = UpdateEventsStatusReq.DynamicOperationReq.builder()
                    .agentCode(cpChannelId)
                    .message(dynamicOperationEncryptionHelper.encryptWithAES256CBC(reqString))
                    .build();
            RequestEntity<UpdateEventsStatusReq.DynamicOperationReq> requestEntity = new RequestEntity<>(operationReq,
                    headers,
                    HttpMethod.POST,
                    URI.create(String.format(UPDATE_EVENTS_STATUS, dynamicOperationDomain)));
            ResponseEntity<UpdateEventsStatusRes> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<UpdateEventsStatusRes>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            return responseEntity.getBody();
        } catch (BusinessException be) {
            log.error("updateEventsStatus err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("updateEventsStatus err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error, please retry again later", e);
        }
    }
}

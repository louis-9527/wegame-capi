package com.wegame.capi.api.operation;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.SingleWalletBaseApi;
import com.wegame.capi.api.domain.req.operation.UpdateEventsStatusReq;
import com.wegame.capi.api.domain.res.operation.UpdateEventsStatusRes;
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

/**
 * 干预事件Api
 */
@Slf4j
@Component
public class OperationEventApi extends SingleWalletBaseApi {
    private static final String UPDATE_EVENTS_STATUS_URL_FORMAT = "%s/updateEventsStatus";
    @Autowired
    DynamicOperationEncryptionHelper dynamicOperationEncryptionHelper;
    @Value("${dynamic.operation.domain}")
    String dynamicOperationDomain;

    /**
     * 更新事件状态
     * @param req 请求参数
     * @return 更新结果
     */
    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public UpdateEventsStatusRes updateEventsStatus(UpdateEventsStatusReq req) {
        try {
            // 参数加密
            String reqString = JSON.toJSONString(req);
            log.info("com.wegame.capi.api.operation.OperationEventApi.updateEventsStatus:reqString={}", reqString);
            String sign = SecureUtil.md5(reqString);
            String keyString = dynamicOperationEncryptionHelper.encryptWithPublicKey(cpPrivateKey);
            String ivString = dynamicOperationEncryptionHelper.encryptWithPublicKey(cpPrivateIv);
            // 构建header
            MultiValueMap<String, String> headers = buildHeaders(sign, keyString, ivString);
            UpdateEventsStatusReq.OperationReq operationReq = UpdateEventsStatusReq.OperationReq.builder()
                    .agentCode(cpChannelId)
                    .message(dynamicOperationEncryptionHelper.encryptWithAES256CBC(reqString))
                    .build();
            RequestEntity<UpdateEventsStatusReq.OperationReq> requestEntity = new RequestEntity<>(operationReq,
                    headers,
                    HttpMethod.POST,
                    URI.create(String.format(UPDATE_EVENTS_STATUS_URL_FORMAT, dynamicOperationDomain)));
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

    private MultiValueMap<String, String> buildHeaders(String sign, String keyString, String ivString) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=180");
        headers.add("sign", sign);
        headers.add("keyString", keyString);
        headers.add("ivString", ivString);
        return headers;
    }
}

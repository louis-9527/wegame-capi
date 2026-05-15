package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.base.SingleWalletBaseApi;
import com.wegame.capi.api.domain.req.singlewallet.GetPoolConfigReq;
import com.wegame.capi.api.domain.res.CommonConfigRes;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.api.helper.DynamicOperationEncryptionHelper;
import com.wegame.capi.infrastructure.helper.LarkHelper;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * 文档导航：<a href="https://casinoplus.sg.larksuite.com/wiki/PrJHwDl0SiLVM7kqHaslEXhbgRb"/>
 */
@Slf4j
@Component
public class SingleWalletConfigApi extends SingleWalletBaseApi {

    public static final String EMPTY_LIST_WARNING_MSG_FORMAT = "血池控制配置列表为空，gameId: %s";
    public static final String NO_MATCH_WARNING_MSG_FORMAT = "血池控制配置匹配为空，pid: %s，channelId: %s，betAmount: %s, gameId: %s";
    public static final String MULTI_MATCH_WARNING_MSG_FORMAT = "血池控制配置匹配到多条，pid: %s，channelId: %s，betAmount: %s， gameId: %s";
    public static final String RTP_EX_MATCH_WARNING_MSG_FORMAT = "未获取到Rtp倍率范围权重配置，poolCategory: %s，poolLevel: %s，betType: %s, gameId: %s";
    public static final String EXCEPTION_WARNING_MSG_FORMAT = "血池控制配置获取异常，pid：%s，channelId：%s，gameId: %s";
    private static final String GET_FOUND_BLOOD_CONFIG_URL_FORMAT = "%s/fund-pool/game/getPoolList";
    @Value("${lark.warningRobot}")
    public String warningRobot;
    @Autowired
    DynamicOperationEncryptionHelper dynamicOperationEncryptionHelper;
    @Autowired
    GameApiProperty gameApiProperty;
    @Autowired
    LarkHelper larkHelper;

    //    0	成功
    //1001	参数错误
    //1002	指定的游戏code不存在
    //1003	未查询到资金池
    //1004	服务内部错误
    @Retryable(value = RetryException.class, maxAttempts = 2, backoff = @Backoff(delay = 0), exclude = BusinessException.class)
    public GetPoolConfigRes getPoolConfigList(String pid, String channelId, String gameId) {
        try {
            // 参数签名
            GameApiProperty.PrivateInfo privateInfo = gameApiProperty.getDosPrivateInfo(channelId);
            String encryptKeyString = dynamicOperationEncryptionHelper.encryptWithPublicKey(channelId, privateInfo.getPrivateKey());
            String encryptIvString = dynamicOperationEncryptionHelper.encryptWithPublicKey(channelId, privateInfo.getPrivateIv());
            GetPoolConfigReq req = GetPoolConfigReq.builder()
                    .agentCode(channelId)
                    .message(dynamicOperationEncryptionHelper.encryptWithAES256CBC(channelId, FastJsonUtils.toJSONString(GetPoolConfigReq.MessageBody.builder()
                            .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                            .gameCode(gameId)
                            .build())))
                    .build();
            // 构建header
            MultiValueMap<String, String> headers = buildHeaders(encryptKeyString, encryptIvString);
            RequestEntity<GetPoolConfigReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST,
                    URI.create(String.format(GET_FOUND_BLOOD_CONFIG_URL_FORMAT, singlePoolConfigDomain(pid))));
            ResponseEntity<CommonConfigRes<GetPoolConfigRes>> responseEntity = walletRestTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonConfigRes<GetPoolConfigRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("getPoolConfigList BusinessException, req: {}, errMsg: {}", String.format("%s_%s_%s", pid, channelId, gameId), be.getMessage(), be);
            larkHelper.sendWarningMsg(String.format(EXCEPTION_WARNING_MSG_FORMAT, pid, channelId, gameId));
            throw be;
        } catch (Exception e) {
            log.error("getPoolConfigList Exception, req: {}, errMsg: {}", String.format("%s_%s_%s", pid, channelId, gameId), e.getMessage(), e);
            larkHelper.sendWarningMsg(String.format(EXCEPTION_WARNING_MSG_FORMAT, pid, channelId, gameId));
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    private MultiValueMap<String, String> buildHeaders(String keyString, String ivString) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=180");
        headers.add("keyString", keyString);
        headers.add("ivString", ivString);
        return headers;
    }
}

package com.wegame.capi.api;

import com.wegame.capi.api.domain.req.BaseReq;
import com.wegame.capi.api.domain.req.singlewallet.SingleWalletBaseReq;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.BeanUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.base.utils.SportsSdkSignatureUtil;
import com.wegame.capi.common.annotation.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * base客户端
 */
@Slf4j
@Client
@Component
public class BaseApi {

    // domain
    @Value("${api.sport.platformDomain}")
    String platformDomain;
    @Value("${api.sport.singleDomain}")
    String singleDomain;

    // cp
    @Value("${api.sport.cp.platformId}")
    public String cpPlatformId;
    @Value("${api.sport.cp.channelId}")
    public String cpChannelId;
    @Value("${api.sport.cp.privateKey}")
    String cpPrivateKey;
    @Value("${api.sport.cp.privateIv}")
    String cpPrivateIv;
    @Value("${api.sport.cp.agentKey}")
    String cpAgentKey;

    // igo
    @Value("${api.sport.igo.platformId}")
    public String igoPlatformId;
    @Value("${api.sport.igo.channelId}")
    public String igoChannelId;
    @Value("${api.sport.igo.privateKey}")
    String igoPrivateKey;
    @Value("${api.sport.igo.privateIv}")
    String igoPrivateIv;
    @Value("${api.sport.igo.agentKey}")
    String igoAgentKey;

    @Autowired
    RestTemplate restTemplate;

    public void assembleSignature(BaseReq req) {
        String agentKey;
        if (StringUtils.equals(req.getPlatformId(), cpPlatformId)) {
            agentKey = cpAgentKey;
        } else if (StringUtils.equals(req.getPlatformId(), igoPlatformId)) {
            agentKey = igoAgentKey;
        } else {
            throw new BusinessException(ErrorCode.ERR_PARAM);
        }
        req.setSignature(SportsSdkSignatureUtil.sign(agentKey, BeanUtil.toMap(req)));
    }

    public void assembleSingleSignature(SingleWalletBaseReq req) {
        String preSignature = String.format("%s%s%s%s", req.getChannelId(), req.getPlayerName(),req.getCreateTime(), req.getRefId());
        String key = StringUtils.equals(String.valueOf(req.getPId()), cpPlatformId) ? cpPrivateKey : igoPrivateKey;
        String iv = StringUtils.equals(String.valueOf(req.getPId()), cpPlatformId) ? cpPrivateIv : igoPrivateIv;
        log.info("platformId: {}; preSignature: {}; key: {}; iv: {}", req.getPId(), preSignature, key, iv);
        String encrypt = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
        AssertUtil.assertNotNull(encrypt, ErrorCode.ERR_PARAM);
        req.setHash(encrypt);
    }

}



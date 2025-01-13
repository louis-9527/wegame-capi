package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.singlewallet.SingleWalletBaseReq;
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
    @Value("${api.go.wegameGoDomain}")
    String wegameGoDomain;

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

    public void assembleSingleSignature(SingleWalletBaseReq req) {
        String hash = hash(req.getChannelId(), req.getPlayerName(), req.getCreateTime(), req.getRefId());
        req.setHash(hash);
    }

    public String hash(String channelId, String playerName, String createTime, String refId) {
        String preSignature = String.format("%s%s%s%s", channelId, playerName, createTime, refId);
        String key = StringUtils.equals(channelId, cpChannelId) ? cpPrivateKey : igoPrivateKey;
        String iv = StringUtils.equals(channelId, cpChannelId) ? cpPrivateIv : igoPrivateIv;
        String hash = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
        AssertUtil.assertNotNull(hash, ErrorCode.ERR_PARAM);
        return hash;
    }
}



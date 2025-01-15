package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.singlewallet.SingleWalletBaseReq;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.annotation.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * base客户端
 */
@Slf4j
@Client
@Component
public class BaseApi {

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

    // go
    @Value("${api.go.secretString}")
    String secretString;

    @Autowired
    RestTemplate restTemplate;


    public void assembleSingleSignature(SingleWalletBaseReq req) {
        String channelId = getChannelId(req.getPId());
        Pair<String, String> privateKeyAndIv = getPrivateKeyAndIv(req.getPId());
        String hash = hash(channelId, req.getPlayerName(), req.getCreateTime(), req.getRefId(), privateKeyAndIv.getLeft(), privateKeyAndIv.getRight());
        req.setChannelId(channelId);
        req.setHash(hash);
    }


    public String hash(String channelId, String playerName, String createTime, String refId, String key, String iv) {
        String preSignature = String.format("%s%s%s%s", channelId, playerName, createTime, refId);
        String hash = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
        AssertUtil.assertNotNull(hash, ErrorCode.ERR_PARAM);
        return hash;
    }

    public MultiValueMap<String, String> buildWeGameGoHeaders() {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add(Constants.WE_GAME_GO_SEC_HEADER, secretString);
        return multiValueMap;
    }


    public String getChannelId(String platformId) {
        if (StringUtils.equals(platformId, cpPlatformId)) {
            return cpChannelId;
        }
        if (StringUtils.equals(platformId, igoPlatformId)) {
            return igoChannelId;
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

    public Pair<String, String> getPrivateKeyAndIv(String platformId) {
        if (StringUtils.equals(platformId, cpPlatformId)) {
            return Pair.of(cpPrivateKey, cpPrivateIv);
        }
        if (StringUtils.equals(platformId, igoPlatformId)) {
            return Pair.of(igoPrivateKey, igoPrivateIv);
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

    public Pair<String, String> getPrivateKeyAndIvByChannelId(String channelId) {
        if (StringUtils.equals(channelId, cpChannelId)) {
            return Pair.of(cpPrivateKey, cpPrivateIv);
        }
        if (StringUtils.equals(channelId, igoChannelId)) {
            return Pair.of(igoPrivateKey, igoPrivateIv);
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

}



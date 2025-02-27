package com.wegame.capi.api.base;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.singlewallet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
//@Component
public class SingleWalletBaseApi extends BaseApi {

    private static final String AUTH_LOGIN_TOKEN_URL_FORMAT = "%s/authLoginToken";
    private static final String GET_CREDIT_URL_FORMAT = "%s/getCredit";
    private static final String BET_WITH_PAYOUT_URL_FORMAT = "%s/betWithPayout";
    private static final String BET_STATUS_URL_FORMAT = "%s/betStatus";
    @Value("${api.sport.singleDomain}")
    public String singleDomain;
    // cp
    @Value("${api.sport.cp.platformId}")
    String cpPlatformId;
    @Value("${api.sport.cp.channelId}")
    String cpChannelId;
    @Value("${api.sport.cp.privateKey}")
    String cpPrivateKey;
    @Value("${api.sport.cp.privateIv}")
    String cpPrivateIv;
    // igo
    @Value("${api.sport.igo.platformId}")
    String igoPlatformId;
    @Value("${api.sport.igo.channelId}")
    String igoChannelId;
    @Value("${api.sport.igo.privateKey}")
    String igoPrivateKey;
    @Value("${api.sport.igo.privateIv}")
    String igoPrivateIv;

    public Pair<String, String> getPrivateKeyAndIvByChannelId(String channelId) {
        if (StringUtils.equals(channelId, cpChannelId)) {
            return Pair.of(cpPrivateKey, cpPrivateIv);
        }
        if (StringUtils.equals(channelId, igoChannelId)) {
            return Pair.of(igoPrivateKey, igoPrivateIv);
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

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

    private String getChannelId(String platformId) {
        if (StringUtils.equals(platformId, cpPlatformId)) {
            return cpChannelId;
        }
        if (StringUtils.equals(platformId, igoPlatformId)) {
            return igoChannelId;
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

    private Pair<String, String> getPrivateKeyAndIv(String platformId) {
        if (StringUtils.equals(platformId, cpPlatformId)) {
            return Pair.of(cpPrivateKey, cpPrivateIv);
        }
        if (StringUtils.equals(platformId, igoPlatformId)) {
            return Pair.of(igoPrivateKey, igoPrivateIv);
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }
}

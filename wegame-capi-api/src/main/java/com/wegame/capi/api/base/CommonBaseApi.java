package com.wegame.capi.api.base;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.BaseReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class CommonBaseApi extends BaseApi {
    // cp
    @Value("${api.game.cp.platformId}")
    public String cpPlatformId;
    @Value("${api.game.cp.channelId}")
    public String cpChannelId;
    @Value("${api.game.cp.privateKey}")
    public String cpPrivateKey;
    @Value("${api.game.cp.privateIv}")
    public String cpPrivateIv;
    // igo
    @Value("${api.game.igo.platformId}")
    public String igoPlatformId;
    @Value("${api.game.igo.channelId}")
    public String igoChannelId;
    @Value("${api.game.igo.privateKey}")
    public String igoPrivateKey;
    @Value("${api.game.igo.privateIv}")
    public String igoPrivateIv;

    public Pair<String, String> getPrivateKeyAndIvByChannelId(String channelId) {
        if (StringUtils.equals(channelId, cpChannelId)) {
            return Pair.of(cpPrivateKey, cpPrivateIv);
        }
        if (StringUtils.equals(channelId, igoChannelId)) {
            return Pair.of(igoPrivateKey, igoPrivateIv);
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

    public void assembleSingleSignature(BaseReq req) {
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

    public static void main(String[] args) {
        String channelId ="1050";
        String createTime = "2025-03-13T10:30:01.768Z";
        String key = "739cf125387017046c02d5610a122859844502203284d3509ff897dc54f7bc09";
        String iv = "32f8e4c142810c8d9a75bce4666198ea";
        String refId = "ad4b269c-22ac-4262-b305-56272ce52226711";
        String playerName="slottest01";
        String preSignature = String.format("%s%s%s%s", channelId, playerName, createTime, refId);
        String hash = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);

        System.out.println(hash);
    }
}

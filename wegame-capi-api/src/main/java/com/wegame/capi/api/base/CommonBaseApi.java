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

/**
 *     promotionDomain: https://reward-http-internal-stress.igo8.me/v1/health-check
 *     singleDomain: https://sw-http-main-apisix-internal-stress.igo8.me/v1/health-check
 *     freeSpinDomain: https://sw-http-freespin-stress.igo8.me/v1/health-check
 *     operationDomain: http://dos-egame-stresstest-internal.dos88.me/healthCheck
 */
@Slf4j
public class CommonBaseApi extends BaseApi {
    public static final String HEALTH_CHECK_FORMAT = "%s/v1/health-check?timestamp=%s";
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
}

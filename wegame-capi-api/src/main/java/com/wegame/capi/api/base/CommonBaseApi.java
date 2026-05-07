package com.wegame.capi.api.base;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.BaseReq;
import com.wegame.capi.common.enums.DomainTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * promotionDomain: <a href="https://reward-http-internal-stress.igo8.me/v1/health-check">...</a>
 * singleDomain: <a href="https://sw-http-main-apisix-internal-stress.igo8.me/v1/health-check">...</a>
 * freeSpinDomain: <a href="https://sw-http-freespin-stress.igo8.me/v1/health-check">...</a>
 * operationDomain: <a href="http://dos-egame-stresstest-internal.dos88.me/healthCheck">...</a>
 */
@Slf4j
public class CommonBaseApi extends BaseApi {
    public static final String HEALTH_CHECK_FORMAT = "%s/v1/health-check";

    @Autowired
    GameApiProperty gameApiProperty;

    public void assembleSingleSignature(BaseReq req) {
        GameApiProperty.PrivateInfo privateInfo = gameApiProperty.getPrivateInfo(req.getPId(), req.getChannelId());
        AssertUtil.assertNotNull(privateInfo, ErrorCode.ERR_INTER);
        String hash = hash(req.getChannelId(), req.getPlayerName(), req.getCreateTime(), req.getRefId(), privateInfo.getPrivateKey(), privateInfo.getPrivateIv());
        req.setHash(hash);
    }

    public void assembleSignature(BaseReq req) {
        GameApiProperty.PrivateInfo privateInfo = gameApiProperty.getPrivateInfo(req.getPId(), req.getChannelId());
        AssertUtil.assertNotNull(privateInfo, ErrorCode.ERR_INTER);
        String hash = hash(req.getPId(), req.getPlayerName(), req.getCreateTime(), req.getRefId(), privateInfo.getPrivateKey(), privateInfo.getPrivateIv());
        req.setHash(hash);
    }

    public void assembleSignature(BaseReq req, String preSignature) {
        GameApiProperty.PrivateInfo privateInfo = gameApiProperty.getPrivateInfo(req.getPId(), req.getChannelId());
        AssertUtil.assertNotNull(privateInfo, ErrorCode.ERR_INTER);
        String hash = hash(preSignature, privateInfo.getPrivateKey(), privateInfo.getPrivateIv());
        req.setHash(hash);
    }

    public String hash(String channelId, String playerName, String createTime, String refId, String key, String iv) {
        String preSignature = String.format("%s%s%s%s", channelId, playerName, createTime, refId);
        String hash = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
        AssertUtil.assertNotNull(hash, ErrorCode.ERR_PARAM);
        return hash;
    }

    private String hash(String preSignature, String key, String iv) {
        String hash = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
        AssertUtil.assertNotNull(hash, ErrorCode.ERR_PARAM);
        return hash;
    }

    public String singleDomain(String pid) {
        return gameApiProperty.getDomain(pid, DomainTypeEnum.SINGLE_DOMAIN);
    }

    public String freeSpinDomain(String pid) {
        return gameApiProperty.getDomain(pid, DomainTypeEnum.FREE_SPIN_DOMAIN);
    }

    public String promotionDomain(String pid) {
        return gameApiProperty.getDomain(pid, DomainTypeEnum.PROMOTION_DOMAIN);
    }

    public String operationDomain(String channelId) {
        return gameApiProperty.getDosDomain(channelId);
    }
}

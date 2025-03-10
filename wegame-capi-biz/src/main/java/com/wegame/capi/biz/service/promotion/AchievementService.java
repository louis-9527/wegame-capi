package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.GetAchievementRes;
import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
import com.wegame.capi.api.promotion.AchievementApi;
import com.wegame.capi.biz.builder.req.AchievementReqBuilder;
import com.wegame.capi.biz.converter.promotion.AchievementConverter;
import com.wegame.capi.client.domain.vo.promotion.AchievementVo;
import com.wegame.capi.client.domain.vo.promotion.RedeemAchievementVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 成就service
 */
@Component
public class AchievementService {

    @Autowired
    AchievementApi achievementApi;

    public List<AchievementVo> getAchievement() {
        GetAchievementRes getAchievementRes = achievementApi.getAchievement(AchievementReqBuilder.buildGetAchievementReq());
        return AchievementConverter.getAchievementToVo(getAchievementRes.getAchievementList());
    }

    public RedeemAchievementVo redeemAchievement(String achievementId, String redeemId) {
        RedeemAchievementRes redeemAchievementRes = achievementApi.redeemAchievement(AchievementReqBuilder.buildRedeemAchievementReq(achievementId, redeemId));
        return AchievementConverter.redeemAchievementRewardToVo(redeemAchievementRes);
    }
}

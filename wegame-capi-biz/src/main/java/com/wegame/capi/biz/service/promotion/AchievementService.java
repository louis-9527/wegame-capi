package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.GetAchievementRes;
import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
import com.wegame.capi.api.promotion.AchievementApi;
import com.wegame.capi.biz.builder.req.AchievementReqBuilder;
import com.wegame.capi.biz.converter.promotion.AchievementConverter;
import com.wegame.capi.biz.converter.promotion.RedeemAchievementConverter;
import com.wegame.capi.biz.domain.biz.promotion.AchievementDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemAchievementDo;
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

    public List<AchievementDo.Achievement> getAchievement() {
        GetAchievementRes getAchievementRes = achievementApi.getAchievement(AchievementReqBuilder.buildGetAchievementReq());
        return AchievementConverter.toDoList(getAchievementRes.getAchievementList());
    }

    public RedeemAchievementDo redeemAchievement(String achievementId, String redeemId) {
        RedeemAchievementRes redeemAchievementRes = achievementApi.redeemAchievement(AchievementReqBuilder.buildRedeemAchievementReq(achievementId, redeemId));
        return RedeemAchievementConverter.toDo(redeemAchievementRes);
    }
}

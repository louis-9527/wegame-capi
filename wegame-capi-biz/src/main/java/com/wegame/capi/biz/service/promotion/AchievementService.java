package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.promotion.AchievementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 成就service
 */
@Component
public class AchievementService {

    @Autowired
    AchievementApi achievementApi;
}

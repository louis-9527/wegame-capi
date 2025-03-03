package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class PromotionBaseApi extends CommonBaseApi {

    @Value("${api.game.promotionDomain}")
    public String promotionDomain;

}

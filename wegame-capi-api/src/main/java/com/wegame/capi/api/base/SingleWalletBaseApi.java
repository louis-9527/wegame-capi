package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class SingleWalletBaseApi extends CommonBaseApi {

    @Value("${api.game.singleDomain}")
    public String singleDomain;

}

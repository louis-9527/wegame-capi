package com.wegame.capi.api.base;

import com.wegame.capi.common.annotation.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * base客户端
 */
@Slf4j
@Client
@Component
public class BaseApi {

    @Autowired
    public RestTemplate restTemplate;

}



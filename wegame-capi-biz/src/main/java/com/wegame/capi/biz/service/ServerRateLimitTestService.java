package com.wegame.capi.biz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServerRateLimitTestService {

    public String rateLimit() {
        return "success";
    }
}

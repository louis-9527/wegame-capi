package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.req.wegamego.MinMaxRandomReq;
import com.wegame.capi.api.domain.res.wegamego.MinMaxRandomRes;
import com.wegame.capi.api.domain.res.wegamego.WeGameGoCommonRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
public class WeGameGoApi extends BaseApi {
    private static final String GET_RANDOM = "%s/minMaxRandom";

    @Autowired
    RestTemplate restTemplate;

    public MinMaxRandomRes random(MinMaxRandomReq req) {
        try {
            RequestEntity<MinMaxRandomReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST, URI.create(String.format(GET_RANDOM, wegameGoDomain)));
            ResponseEntity<WeGameGoCommonRes<MinMaxRandomRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WeGameGoCommonRes<MinMaxRandomRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().success(), ErrorCode.ERR_INTER);
            return responseEntity.getBody().getData();
        } catch (Exception e) {
            log.error("WeGameGoApi-random err, errMsg: {}", e.getMessage(), e);
            throw e;
        }
    }


}

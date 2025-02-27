package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.req.wegamego.MinMaxRandomReq;
import com.wegame.capi.api.domain.req.wegamego.MultiMinMaxRandomReq;
import com.wegame.capi.api.domain.res.wegamego.WeGameGoCommonRes;
import com.wegame.capi.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class WeGameGoApi extends BaseApi {

    private static final String MIN_MAX_RANDOM_URL = "%s/minMaxRandom";
    private static final String MULTI_MIN_MAX_RANDOM_URL = "%s/multiMinMaxRandom";
    @Value("${api.go.wegameGoDomain}")
    String wegameGoDomain;
    @Value("${api.go.secretString}")
    String secretString;

    public Integer minMaxRandom(MinMaxRandomReq req) {
        try {
            RequestEntity<MinMaxRandomReq> requestEntity = new RequestEntity<>(req, buildWeGameGoHeaders(), HttpMethod.POST, URI.create(String.format(MIN_MAX_RANDOM_URL, wegameGoDomain)));
            ResponseEntity<WeGameGoCommonRes<Integer>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WeGameGoCommonRes<Integer>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuccess(), ErrorCode.ERR_INTER);
            return responseEntity.getBody().getData();
        } catch (Exception e) {
            log.error("WeGameGoApi#minMaxRandom err, errMsg: {}, req: {}", e.getMessage(), req, e);
            throw e;
        }
    }

    public List<Integer> multiMinMaxRandom(MultiMinMaxRandomReq req) {
        try {
            RequestEntity<MultiMinMaxRandomReq> requestEntity = new RequestEntity<>(req, buildWeGameGoHeaders(), HttpMethod.POST, URI.create(String.format(MULTI_MIN_MAX_RANDOM_URL, wegameGoDomain)));
            ResponseEntity<WeGameGoCommonRes<List<Integer>>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<WeGameGoCommonRes<List<Integer>>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuccess(), ErrorCode.ERR_INTER);
            return responseEntity.getBody().getData();
        } catch (Exception e) {
            log.error("WeGameGoApi#multiMinMaxRandom err, errMsg: {}, req: {}", e.getMessage(), req, e);
            throw e;
        }
    }

    private MultiValueMap<String, String> buildWeGameGoHeaders() {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add(Constants.WE_GAME_GO_SEC_HEADER, secretString);
        return multiValueMap;
    }
}

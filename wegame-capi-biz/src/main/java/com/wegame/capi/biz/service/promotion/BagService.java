package com.wegame.capi.biz.service.promotion;

import com.wegame.base.annotation.Authority;
import com.wegame.capi.api.promotion.BagApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 背包service
 */
@Component
public class BagService {

    @Autowired
    BagApi bagApi;
}

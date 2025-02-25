package com.wegame.capi.test.biz.service;

import com.wegame.capi.api.util.DynamicOperationEncryptionUtil;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOperationEncryptionUtilTest  extends BaseTest {
    @Autowired
    DynamicOperationEncryptionUtil dynamicOperationEncryptionUtil;

    @Test
    public void encryptWithAES256CBC(){
        String test ="1w1sqwqr3";
        String s = dynamicOperationEncryptionUtil.encryptWithAES256CBC(test);
        System.out.println(s);
    }
}

package com.wegame.capi.test.api.helper;

import com.wegame.capi.api.helper.DynamicOperationEncryptionHelper;
import com.wegame.capi.test.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOperationEncryptionHelperTest extends BaseTest {

    @Autowired
    DynamicOperationEncryptionHelper dynamicOperationEncryptionUtil;

    @Test
    public void test_encryptWithAES256CBC(){
        String key ="1w1sqwqr3";
        String entryKey = "ea1839bfb2184a134974425de3435f52";
        String encryptWithAES256CBC = dynamicOperationEncryptionUtil.encryptWithAES256CBC(key);
        Assert.assertTrue(StringUtils.equals(entryKey, encryptWithAES256CBC));
    }
}

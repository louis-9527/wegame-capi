package com.wegame.capi.test.api.util;

import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;

public class SingleWalletSignatureUtilTest extends BaseTest {

    @Test
    public void test_encrypt() {
        String hashEncrypt = "83e13b70d667d5cf98cd2be132e7765ed5643b6f10ac0f046cdcac1447df39d65e23b60659690ebe955c6f6170048f153b5ce252886afed1a8b924bd93fc1e2a61a47485165bb5db071ab975da529239";
        String channelId = "1050";
        String createTime = "2025-03-13T10:30:01.768Z";
        String key = "739cf125387017046c02d5610a122859844502203284d3509ff897dc54f7bc09";
        String iv = "32f8e4c142810c8d9a75bce4666198ea";
        String refId = "ad4b269c-22ac-4262-b305-56272ce52226711";
        String playerName = "slottest01";
        String preSignature = String.format("%s%s%s%s", channelId, playerName, createTime, refId);
        String hash = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
        Assert.assertTrue(hash.equals(hashEncrypt));
    }
}

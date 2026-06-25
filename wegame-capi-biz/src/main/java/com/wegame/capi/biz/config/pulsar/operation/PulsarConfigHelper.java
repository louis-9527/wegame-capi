package com.wegame.capi.biz.config.pulsar.operation;

import com.wegame.capi.client.enums.GameTypeEnum;

public class PulsarConfigHelper {

    private static final String format = "%s_%s_%s_%s";

    public static String buildSubscriptionName(String appName, String env, String key, GameTypeEnum gameType) {
        return String.format(format, appName, env, key, gameType.getName());
    }
}

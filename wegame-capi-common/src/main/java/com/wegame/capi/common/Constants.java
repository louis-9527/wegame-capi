package com.wegame.capi.common;

public class Constants {
    public static final String CUSER_LOGIN_TOKEN = "wegame:cuser:login:token:%s";
    public static final String CUSER_CREDIT_CHANGE = "wegame:cuser:credit:change:%s";
    public static final String REWARD_MESSAGE = "wegame:reward:message:%s";
    public static final String SYSTEM = "system";
    public static final Integer TOKEN_EXPIRED = 60;

    // token&签名相关
    public static final String _TOKEN_ = "token-game";
    public static final String APP_ID = "com.wegame.capi";
    public static final String APP_SECRET = "MIIEowIBAAKCAQEAmP8M8b1C12VA9hB3iIHjvQYnda1QovLp+zKTbY2WRRAIGPfT";
    public static final Long REQUEST_TIME_OUT = 5 * 60 * 1000L;

    public static final String BUSY_OPT_TIP = "操作繁忙，请稍后重试";

    // redis
    // 推送中大奖redis key
    public static final String PUSH_PRIZE_REDIS_KEY = "wegame:push_prize:%s";
    // 推送大奖lua脚本
    public static final String PUSH_PRIZE_LUA_SCRIPT = "local key = KEYS[1]\n" +
            "local value = ARGV[1]\n" +
            "local maxSize = tonumber(ARGV[2])\n" +
            "\n" +
            "redis.call('RPUSH', key, value)\n" +
            "if redis.call('LLEN', key) > maxSize then\n" +
            "    redis.call('LTRIM', key, -maxSize, -1)\n" +
            "end";
    public static final Integer PUSH_PRIZE_LIMIT = 10;
    // 推送大奖tips内容
    public static final String PUSH_PRIZE_CONTENT_FORMAT = "%s %s Won ₱%s in %s free spins";

}

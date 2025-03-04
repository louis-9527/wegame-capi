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

    public static final String BUSY_OPT_TIP = "please try again later";

    // 推送中大奖redis key %s gameId
    public static final String WEGAME_REWARD_NOTIFY_KEY_FORMAT = "wegame:reward.notify:%s";
    // 推送大奖lua脚本
    public static final String WEGAME_REWARD_NOTIFY_LUA_SCRIPT = "local key = KEYS[1]\n" +
            "local value = ARGV[1]\n" +
            "local maxSize = tonumber(ARGV[2])\n" +
            "\n" +
            "redis.call('RPUSH', key, value)\n" +
            "if redis.call('LLEN', key) > maxSize then\n" +
            "    redis.call('LTRIM', key, -maxSize, -1)\n" +
            "end";
    public static final Integer WEGAME_MARIA_REWARD_NOTIFY_SIZE = 10;
    public static final Integer WEGAME_MALAKA_REWARD_NOTIFY_SIZE = 10;
    public static final Integer WEGAME_IBONG_REWARD_NOTIFY_SIZE = 10;
    // maria推送大奖tips内容
    public static final String MARIA_REWARD_NOTIFY_CONTENT_FORMAT = "%s %s Won ₱%s in %s free spins";
    public static final String MALAKA_REWARD_NOTIFY_CONTENT_FORMAT = "%s %s Won ₱%s in %s free spins";
    // ibong推送大奖tips内容 XXXX won 20x rewards ，₱ 99999
    public static final String IBONG_REWARD_NOTIFY_CONTENT_FORMAT = "%s won %sx rewards ，₱ %s ";

    // go sec header
    public static final String WE_GAME_GO_SEC_HEADER = "we-game-go-sec";


    public static final int CONST_100 = 100;
    public static final int CONST_10000 = 10000;
    public static final int CONST_1000000 = 1000000;

}

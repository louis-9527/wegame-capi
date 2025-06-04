package com.wegame.capi.common;

public class Constants {
    public static final String CUSER_LOGIN_TOKEN = "wegame:cuser:login:token:%s";
    public static final String SYSTEM = "system";
    public static final Integer TOKEN_EXPIRED = 60;

    // token&签名相关
    public static final String _TOKEN_ = "token-game";
    public static final String APP_ID = "com.wegame.capi";
    public static final String APP_SECRET = "MIIEowIBAAKCAQEAmP8M8b1C12VA9hB3iIHjvQYnda1QovLp+zKTbY2WRRAIGPfT";
    public static final Long REQUEST_TIME_OUT = 5 * 60 * 1000L;


    // 推送中大奖redis key
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
    public static final Integer MARIA_MARKILING_REWARD_NOTIFY_SIZE = 10;
    public static final Integer MALAKAS_REWARD_NOTIFY_SIZE = 10;
    public static final Integer IBONG_ADARNA_REWARD_NOTIFY_SIZE = 10;
    public static final Integer BAKUNAWA_ADARNA_REWARD_NOTIFY_SIZE = 10;

    public static final Integer LAMANG_REWARD_NOTIFY_SIZE = 20;
    // maria推送大奖tips内容
    public static final String MARIA_MARKILING_REWARD_NOTIFY_CONTENT_FORMAT = "%s %s Won ₱%s in %s free spins";
    // malakas推送大奖tips内容
    public static final String MALAKAS_REWARD_NOTIFY_CONTENT_FORMAT = "%s %s Won ₱%s in %s free spins";
    // ibong推送大奖tips内容 XXXX won 20x rewards ，₱ 99999
    public static final String IBONG_ADARNA_REWARD_NOTIFY_CONTENT_FORMAT = "%s won %sx rewards ，₱ %s ";
    // bakunawa推送大奖tips内容 XXXX won 100x rewards ，₱ 99999
    public static final String BAKUNAWA_REWARD_NOTIFY_CONTENT_FORMAT = "%s won %sx rewards ，₱ %s ";

    // lamang推送大奖tips内容
    public static final String LAMANG_REWARD_NOTIFY_CONTENT_FORMAT = "%s %s Won ₱%s in %s free spins";

    // go sec header
    public static final String WE_GAME_GO_SEC_HEADER = "we-game-go-sec";


    public static final int CONST_100 = 100;
    public static final int CONST_10000 = 10000;
    public static final int CONST_1000000 = 1000000;
    public static final int CONST_10000000 = 1000_000_0;
    public static final int CONST_1000000000 = 1000_000_000;

    public static final String OPERATION_EVENT_CACHE_KEY = "wegame:cuser:operation_event_2:%s_%s";
    public static final String OPERATION_EVENT_ING_KEY = "wegame:cuser:operation_event_ing:%s_%s";
    public static final Long OPERATION_EVENT_ING_EXPIRED = 20 * 60L;


    public static final String OPERATION_EVENT_CONFIG_INCREMENT_CACHE_KEY = "wegame:cuser:operation_config_increment:%s_%s_%s";

    public static final String OPT_BUSY = "Too many requests. Please try again later.";


    // property
    public static final String TRUE_VAL = "true";
    public static final String MARIA_MAKILING_PROP = "game.enable.MariaMakiling";
    public static final String MALAKAS_PROP = "game.enable.Malakas";
    public static final String IBONG_ADARNA_PROP = "game.enable.IbongAdarna";
    public static final String BAKUNAWA_ADARNA_PROP = "game.enable.Bakunawa";
    public static final String LAMANG_PROP = "game.enable.lamang";


    public static final long FIVE_MINUTES_AGO = 5 * 60 * 1000;

    public static final long TWO_HOURS_AGO = 2 * 60 * 60 * 1000;

    public static final Double MIN_BET_AMOUNT_M = 1.1;

    public static final String MONGOTEMPLATE = "MongoTemplate";
    public static final String PRIMARY_MONGOTEMPLATE = "PrimaryMongoTemplate";

    /**
     * 奖池流水过期时间（秒）
     */
    public static final long JACKPOT_REWARD_POOL_EXPIRED_SECONDS = 24 * 60 * 60;
}

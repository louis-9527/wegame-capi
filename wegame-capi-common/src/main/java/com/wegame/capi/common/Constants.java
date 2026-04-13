package com.wegame.capi.common;

import java.math.BigDecimal;

public class Constants {
    public static final String CUSER_LOGIN_TOKEN = "wegame:cuser:login:token:%s";
    public static final String SYSTEM = "system";
    public static final Integer TOKEN_EXPIRED = 60;

    // token&签名相关
    public static final String _TOKEN_ = "token-game";
    public static final String APP_ID = "com.wegame.capi";
    public static final String APP_SECRET = "MIIEowIBAAKCAQEAmP8M8b1C12VA9hB3iIHjvQYnda1QovLp+zKTbY2WRRAIGPfT";
    public static final Long REQUEST_TIME_OUT = 5 * 60 * 1000L;

    public static final String COMMON_JSON_CONFIG_SEPARATOR = "_";


    // 推送中大奖redis key
    public static final String WEGAME_REWARD_NOTIFY_KEY_FORMAT = "wegame:reward.notify:%s_%s_%s";
    /** 所有游戏通用延迟大奖通知内容模板：Player {playerId} {时间}\n₱{派奖金额} won at %sX */
    public static final String DELAY_REWARD_NOTIFY_CONTENT_FORMAT = "Player %s %s\n₱%s won at %sX";
    /** 延迟大奖通知列表在 Redis 中的默认最大条数（子类可重写 getDelayRewardNotifyListSize 使用不同值） */
    public static final int DEFAULT_DELAY_REWARD_NOTIFY_LIST_SIZE = 10;
    /** 延迟大奖通知的默认倍数阈值 30X*/
    public static final BigDecimal DELAY_REWARD_30X_MULTIPLIER_THRESHOLD = new BigDecimal(30);
    /** 延迟大奖通知的默认倍数阈值 100X*/
    public static final BigDecimal DELAY_REWARD_100X_MULTIPLIER_THRESHOLD = new BigDecimal(100);
    // 推送大奖lua脚本
    public static final String WEGAME_REWARD_NOTIFY_LUA_SCRIPT = "local key = KEYS[1]\n" +
            "local value = ARGV[1]\n" +
            "local maxSize = tonumber(ARGV[2])\n" +
            "\n" +
            "redis.call('RPUSH', key, value)\n" +
            "if redis.call('LLEN', key) > maxSize then\n" +
            "    redis.call('LTRIM', key, -maxSize, -1)\n" +
            "end";
    // go sec header
    public static final String WE_GAME_GO_SEC_HEADER = "we-game-go-sec";


    public static final int CONST_0 = 0;
    public static final int CONST_1 = 1;
    public static final int CONST_100 = 100;
    public static final int CONST_10000 = 10000;
    public static final int CONST_100000 = 100000;
    public static final int CONST_1000000 = 1000000;
    public static final int CONST_10000000 = 1000_000_0;
    public static final int CONST_1000000000 = 1000_000_000;

    /**
     * map结构：
     * key：为干预事件主键id 或者 -1（标志，不存在干预事件）
     */
    public static final String OPERATION_EVENT_CACHE_KEY = "wegame:cuser:operation_event_channel:%s_%s_%s";
    /**
     * key-val结构
     * 用于存储当前干预事件的干预状态
     */
    public static final String OPERATION_EVENT_ING_KEY = "wegame:cuser:operation_event_ing:%s_%s";
    public static final Long OPERATION_EVENT_ING_EXPIRED = 20 * 60L;


    public static final String OPERATION_EVENT_CONFIG_INCREMENT_CACHE_KEY = "wegame:cuser:operation_config_increment:%s_%s_%s";

    public static final String OPT_BUSY = "Too many requests. Please try again later.";


    // property
    public static final String TRUE_VAL = "true";
    public static final String DYNAMIC_OPERATION_SWITCH = "dynamic.operation.switch";
    public static final String MARIA_MAKILING_PROP = "game.enable.MariaMakiling";
    public static final String MALAKAS_PROP = "game.enable.Malakas";
    public static final String IBONG_ADARNA_PROP = "game.enable.IbongAdarna";
    public static final String LAMANG_PROP = "game.enable.Lamang";
    public static final String BAKUNAWA_PROP = "game.enable.Bakunawa";
    public static final String DIWATA_MAYARI_PROP = "game.enable.DiwataMayari";
    public static final String KAPRE_PROP = "game.enable.Kapre";
    public static final String MANGKUKULAM_PROP = "game.enable.Mangkukulam";
    public static final String SIRENA_PROP = "game.enable.Sirena";



    public static final long FIVE_MINUTES_AGO = 5 * 60 * 1000;

    public static final long TWO_HOURS_AGO = 2 * 60 * 60 * 1000;

    public static final Double MIN_BET_AMOUNT_M = 1.1;

    public static final String MONGOTEMPLATE = "MongoTemplate";
    public static final String PRIMARY_MONGOTEMPLATE = "PrimaryMongoTemplate";

    /**
     * 奖池流水过期时间（秒）
     */
    public static final long JACKPOT_REWARD_POOL_EXPIRED_SECONDS = 10 * 24 * 60 * 60;
    public static final String BUILD_BEAN_FORMAT = "build bean: {}";
    public static final String COMMON_CLOSE_METHOD = "close";

    public static final String JACKPOT_LAMANG_PULSAR_SUBSCRIPTION = "_jackpot";
}

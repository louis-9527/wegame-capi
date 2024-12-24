package com.wegame.capi.common;

public class Constants {
    public static final String CUSER_LOGIN_TOKEN = "wegame:cuser:login:token:%s";
    public static final String CUSER_LOGIN_IDENTITY = "wegame:cuser:login:identity:%s";
    public static final String SYSTEM = "system";
    public static final Integer TOKEN_EXPIRED = 60;

    // token&签名相关
    public static final String _TOKEN_ = "token-live-sports";
    public static final String APP_ID = "com.wegame.capi";
    public static final String APP_SECRET = "MIIEowIBAAKCAQEAmP8M8b1C12VA9hB3iIHjvQYnda1QovLp+zKTbY2WRRAIGPfT";
    public static final Long REQUEST_TIME_OUT = 5 * 60 * 1000L;

    public static final String BET_RECORD_EXIST_CACHE_KEY = "wegame:betRecord:exits:{%s}-%s";

    // 中台playerId字母前缀
    public static final String PLAYER_ID_PREFIX = "a";

    // 拳击相关
    // pid
    public static final String P_ID = "50";
    // channelId
    public static final String CHANNEL_ID = "1030";
    // gameId
    public static final String BOXING_GAME_ID = "10001";
    // channelId
    public static final String C_ID = "50";


    // 地区
    public static final String REGION_ROOT = "443174";

    public static final String BUSY_OPT_TIP = "操作繁忙，请稍后重试";
}

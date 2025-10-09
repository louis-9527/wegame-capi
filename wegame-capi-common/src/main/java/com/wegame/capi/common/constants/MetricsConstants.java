package com.wegame.capi.common.constants;

public class MetricsConstants {
    //===================betRecord extInfoMap key constants ======start=====================
    public static final String EXT_MAP_KEY_GAME_PLAY_TYPE = "gamePlayType";

    //===================betRecord extInfoMap key constants ======end=====================

    /**
     * 用户指标缓存Format
     * capi:player:aggregation:cache:{pid}:{channelId}:{gameId}:{playerId}
     */
    private static final String USER_PLAY_METRICS_CACHE_FORMAT = "capi:player:aggregation:cache:%s:%s:%s:%s";
    /**
     * 大盘指标缓存Format
     * capi:game:aggregation:cache:{pid}:{channelId}:{gameId}
     */
    private static final String GAME_METRICS_CACHE_FORMAT = "capi:game:aggregation:cache:%s:%s:%s";
    /**
     * 大盘生效中的干预类型缓存Format
     * capi:game:inuse:dos:type:cache:{pid}:{channelId}:{gameId}
     */
    private static final String GAME_INUSE_DOS_TYPE_CACHE_FORMAT = "capi:game:inuse:dos:type:cache:%s:%s:%s";
    /**
     * 用户指标缓存时间:秒
     * 暂定30分钟，每次操作都续约
     */
    public static final long USER_METRICS_CACHE_TIME = 60 * 30;

    /**
     * 大盘指标缓存时间:秒
     * 暂定120分钟，每次操作都续约
     */
    public static final long GAME_METRICS_CACHE_TIME = 60 * 60 * 2;

    public static String buildGameMetricsCacheKey(String pId, String channelId, String gameId) {
        return String.format(GAME_METRICS_CACHE_FORMAT, pId, channelId, gameId);
    }

    public static String buildGameInUseDosTypeCacheKey(String pId, String channelId, String gameId) {
        return String.format(GAME_INUSE_DOS_TYPE_CACHE_FORMAT, pId, channelId, gameId);
    }

    public static String buildUserMetricsCacheKey(String pId, String channelId, String gameId, String playerId) {
        return String.format(USER_PLAY_METRICS_CACHE_FORMAT, pId, channelId, gameId, playerId);
    }

}

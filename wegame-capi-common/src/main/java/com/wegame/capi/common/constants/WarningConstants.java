package com.wegame.capi.common.constants;

public class WarningConstants {

    public static final String EMPTY_LIST_WARNING_MSG_FORMAT = "血池控制配置列表为空，gameId: %s";
    public static final String NO_MATCH_WARNING_MSG_FORMAT = "血池控制配置匹配为空，pid: %s，channelId: %s，betAmount: %s, gameId: %s";
    public static final String MULTI_MATCH_WARNING_MSG_FORMAT = "血池控制配置匹配到多条，pid: %s，channelId: %s，betAmount: %s， gameId: %s";
    public static final String RTP_EX_MATCH_WARNING_MSG_FORMAT = "未获取到Rtp配置，rtpKey: %s，rtpSubKey: %s，betType: %s, gameId: %s";
    public static final String EXCEPTION_WARNING_MSG_FORMAT = "血池控制配置获取异常，pid：%s，channelId：%s，gameId: %s";


}

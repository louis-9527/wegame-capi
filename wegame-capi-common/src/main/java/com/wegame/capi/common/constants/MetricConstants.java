package com.wegame.capi.common.constants;

public class MetricConstants {

    public static final String common_spit = "_";

    // 注册时间
    public static final String user_metric_registerTime = "registerTime";
    // 上一次时间
    public static final String user_metric_lastLoginTime = "lastLoginTime";
    // 今日登陆时间
    public static final String user_metric_currentLoginTime = "currentLoginTime";
    // 退出游戏时余额
    public static final String user_metric_exitBalance = "exitBalance";
    // 进入游戏时余额
    public static final String user_metric_enterBalance = "enterBalance";
    // 当前余额
    public static final String user_metric_currentBalance = "currentBalance";
    // 昨日是否有投注
    public static final String user_metric_yesterdayHasBet = "yesterdayHasBet";

    // 总投注次数
    public static final String user_metric_totalBetTimes = "totalBetTimes";
    // 当日总注次数
    public static final String user_metric_todayBetTimes = "todayBetTimes";
    // 总投注金额（支付金额）
    public static final String user_metric_totalBetAmount = "totalBetAmount";
    // 总派彩金额
    public static final String user_metric_totalPayoutAmount = "totalPayoutAmount";
    // 空盘累计次数
    public static final String user_metric_continueEmptyTimes = "continueEmptyTimes";
    // 不中FG次数(中后清0后再累计)
    public static final String user_metric_continueNoFgTimes = "continueNoFgTimes";
    // 用户干预记录： Map<干预规则Id, 干预时间戳>
    public static final String user_metric_interventionRecord = "interventionRecord";
}

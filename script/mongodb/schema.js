const bet_record = {
    _id: Number,//主键
    pId: String,//平台id
    channelId: String,//渠道id
    gameId: String,// 游戏id
    userId: Number,//用户id
    playerId: String,//玩家id
    playerName: Number,//玩家名称
    betType: String,//投注类型
    betSubType: String,//投注子类型
    betPromotionType: Number,//投注营销类型
    betId: String,//投注id
    betSerialNo: String,//投注流水号（对应单钱包refId）
    betAmount: Number,//投注金额 单位：分
    payAmount: Number,//支付金额 单位：分
    reachMaxMultiple: Boolean,//是否达到最大倍数
    payOutAmount: Number,//派彩金额 单位：分
    marketFlag: Number,//营销标识
    status: Number,//状态 0进行中 1已完结 2无效
    extendInfo: {//扩展信息
        ieId: String,//干预事件Id(当这笔订单属于干预订单时生成,对于 EventId 需要传给单钱包)
        rewardUrl: String,//（星卡）奖励图片url
        campaignId: String,//预定投注Id
    },
    creator: String,//创建人
    createTime: Number,//创建时间
    updater: String,//更新人
    updateTime: Number,//更新时间
};

const game_record = {
    _id: Number,
    batchId: Number,
    betRecordId: Number,
    freeWheelFlag: Boolean,
    gameRecordIds: [],
    index: Number,
    overallRewardAmount: Number,
    parentId: Number,
    playerId: String,
    playerName: String,
    rewardResult: {

    },
    wheel: {

    },
    accumulateReachMaxMultiple: Boolean,
    lastGameFlag: Boolean
}

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

const operation_event = {
    _id: Long, // 主键ID
    eventId: String, // 干预事件ID
    playerName: String, // 玩家名称
    bonus: Long, // 干预金额
    minBonus: Long, // 最小干预金额
    maxBonus: Long, // 最大干预金额
    finishBonus: Long, // 已完成干预金额
    eventKind: Integer, // 干预事件类型：代码：1, 表示正常游戏紧急干预；代码:  2, 表示正常游戏普通干预，可以由游戏方根据情况调整干预手段；代码:  3,  表示免费游戏紧急干预；代码:  4, 表示免费游戏普通干预
    gameId: String, // 游戏ID
    channelId: String, // 渠道ID
    effectedTime: Long, // 生效时间
    expiredTime: Long, // 过期时间
    eventStatus: Integer, // 干预状态
    eventSubStatus: Integer, // 干预子状态
    reportStatus: Integer, // 上报状态
    eventMsgDetail: String, // 干预事件消息快照
    reportFailMsg: String, // 上报失败信息
    reportTime: Long, // 上报时间
    createTime: Long, // 创建时间
    updateTime: Long, // 更新时间
};

const operation_event_config = {
    _id: Number, // 主键ID
    gameId: String, // 游戏ID
    betType: String, // 投注类型
    multiple: Number, // 倍数
};

const operation_event_config_record = {
    _id: Number, // 主键ID
    parentId: Number, // 父ID
    batchId: Number, // 批次ID
    betRecordId: Number, // 投注记录ID
    index: Number, // 序号
    gameRecordIds: [], // 免费轮游戏记录ID列表
    wheel: {}, // 滚轮信息
    extInfo: {} // 扩展信息
};

const operation_event_detail = {
    _id: Number, // 主键ID
    operatorRecordId: Number, // 干预事件ID(系统)
    eventId: String, // 干预事件ID
    operatorGameId: String, // 游戏ID
    configId: Number, // 配置ID,命中那条倍数
    playerId: String, // 玩家ID(三方)
    playerName: String, // 玩家名称(三方)
    userId: String, // 玩家ID
    betAmount: Number, // 投注金额 单位：分
    payOutAmount: Number, // 派奖金额 单位：分
    eventAmount: Number, // 事件干预金额 单位：分
    payOutTime: Number, // 派奖时间
    betTime: Number, // 下注时间
    betId: String, // 投注id（单钱包）
    betRecordId: Number, // 投注记录Id
    betStatus: Number, // 订单状态
    channelId: String, // 渠道ID
    gameId: String, // 游戏ID
    createTime: Number, // 创建时间
    updateTime: Number, // 更新时间
};

const star_card_use_record = {
    _id: Number, // 主键ID
    rewardId: String, // 奖励ID
    rewardConfigId: String, // 奖励配置ID
    gameId: String, // 游戏ID
    channelId: String, // 渠道ID
    betRecordId: Number, // 投注记录ID
    playerId: String, // 玩家ID
    playName: String, // 玩家名称
    betAmount: Number, // 投注金额 单位：分
    payOutAmount: Number, // 派奖金额 单位：分
    useStatus: Number, // 使用状态
    failMsg: String, // 核销失败原因
    failCode: Number, // 核销失败代码
    reportStatus: Number, // 上报状态
    createTime: Number, // 创建时间
    updateTime: Number, // 更新时间
};


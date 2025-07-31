// 切换到目标数据库（若不存在会自动创建）
use lamang;

// ----------------------------
//  Collection structure for lamang_bet_record
// ----------------------------
db.getCollection("lamang_bet_record").drop();
db.createCollection("lamang_bet_record");
// 索引lamang_bet_record-c端查询需要
db.lamang_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1});
db.lamang_bet_record.createIndex({createTime: -1, status: 1, _id: 1});
// 索引lamang_bet_record-b端查询需要-使用覆盖索引
db.lamang_bet_record.createIndex({
    createTime: -1,
    channelId: 1,
    status: 1,
    payAmount: 1,
    payOutAmount: 1,
    userId: 1,
    betPromotionType: 1
});


// ----------------------------
//  Collection structure for lamang_game_record
// ----------------------------
db.getCollection("lamang_game_record").drop();
db.createCollection("lamang_game_record");
// 索引lamang_game_record-c端查询需要-新
db.lamang_game_record.createIndex({betRecordId: 1, batchId: 1});



// ----------------------------
//  Collection structure for lamang_star_card_record
// ----------------------------
db.getCollection("lamang_star_card_record").drop();
db.createCollection("lamang_star_card_record");
db.lamang_star_card_record.createIndex({createTime:-1, channelId: 1});
db.lamang_star_card_record.createIndex({reportStatus: 1});

// ----------------------------
//  Collection structure for lamang_operation_event
// ----------------------------
db.getCollection("lamang_operation_event").drop();
db.createCollection("lamang_operation_event");
db.lamang_operation_event.createIndex({createTime: -1, channelId: 1});
db.lamang_operation_event.createIndex({updateTime: 1, reportStatus: 1});
db.lamang_operation_event.createIndex({updateTime: 1, eventStatus: 1});
db.lamang_operation_event.createIndex({playerName: 1, effectedTime: 1, expiredTime: 1, eventStatus: 1});
db.lamang_operation_event.createIndex({eventId: 1}, {unique: true});
db.lamang_operation_event.createIndex({expiredTime: 1, reportStatus: 1});
db.lamang_operation_event.createIndex({eventStatus:1, reportStatus:1, _id:1 , updateTime : -1});

// ----------------------------
//  Collection structure for lamang_operation_event_detail
// ----------------------------
db.getCollection("lamang_operation_event_detail").drop();
db.createCollection("lamang_operation_event_detail");
db.lamang_operation_event_detail.createIndex({createTime: 1});
db.lamang_operation_event_detail.createIndex({operatorRecordId: 1});
db.lamang_operation_event_detail.createIndex({betStatus: 1, _id: 1,  createTime: -1});

// ----------------------------
//  Collection structure for lamang_operation_event_config
// ----------------------------
db.getCollection("lamang_operation_event_config").drop();
db.createCollection("lamang_operation_event_config");
db.lamang_operation_event_config.createIndex({multiple: 1});

// ----------------------------
//  Collection structure for lamang_operation_event_config_record
// ----------------------------
db.getCollection("lamang_operation_event_config_record").drop();
db.createCollection("lamang_operation_event_config_record");
db.lamang_operation_event_config_record.createIndex({betRecordId: 1});


// ----------------------------
//  Collection structure for lamang_jackpot_reward_pool
// ----------------------------
db.getCollection("lamang_jackpot_reward_pool").drop();
db.createCollection("lamang_jackpot_reward_pool");
// 批量插入数据
db.lamang_jackpot_reward_pool.insertMany([
    { _id: 1,type:1, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
    { _id: 2,type:2, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
    { _id: 3,type:3, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
    { _id: 4,type:4, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
]);
// jackpot中奖记录唯一键
db.lamang_jackpot_reward_pool.createIndex(
    { type: 1},
    { unique: true}
);

// ----------------------------
//  Collection structure for lamang_jackpot_user_reward_record
// ----------------------------
db.getCollection("lamang_jackpot_user_reward_record").drop();
db.createCollection("lamang_jackpot_user_reward_record");
// jackpot中奖记录唯一键
db.lamang_jackpot_user_reward_record.createIndex(
    { gameRecordId: 1,betRecordId: 1},
    { unique: true}
);
// 中奖金额降序索引
db.lamang_jackpot_user_reward_record.createIndex(
    {rewardAmount: -1}
);

// ----------------------------
//  Collection structure for lamang_jackpot_user_reward_record
// ----------------------------
db.getCollection("lamang_jackpot_reward_pool_input_record").drop();
db.createCollection("lamang_jackpot_reward_pool_input_record");
// 奖池流水ttl，索引:在expireAtTime时间的时候删除
db.getCollection("lamang_jackpot_reward_pool_input_record").createIndex(
    { "expireAtTime": 1 },
    { expireAfterSeconds: 0 }
);
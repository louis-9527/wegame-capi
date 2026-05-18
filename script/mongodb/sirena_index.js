

// 切换到目标数据库（若不存在会自动创建）
use sirena;

// ----------------------------
//  Collection structure for sirena_bet_record
// ----------------------------
db.getCollection("sirena_bet_record").drop();
db.createCollection("sirena_bet_record");
// 索引sirena_bet_record-c端查询需要
db.sirena_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1});
db.sirena_bet_record.createIndex({playerId: 1, createTime: -1});
db.sirena_bet_record.createIndex({createTime: -1, status: 1, betSubType: 1, userId : 1,  _id: 1});


// ----------------------------
//  Collection structure for sirena_game_record
// ----------------------------
db.getCollection("sirena_game_record").drop();
db.createCollection("sirena_game_record");
// 索引sirena_game_record-c端查询需要-新
db.sirena_game_record.createIndex({betRecordId: 1, batchId: 1});



// ----------------------------
//  Collection structure for sirena_star_card_record
// ----------------------------
db.getCollection("sirena_star_card_record").drop();
db.createCollection("sirena_star_card_record");
db.sirena_star_card_record.createIndex({createTime:-1, channelId: 1});
db.sirena_star_card_record.createIndex({reportStatus: 1});

// ----------------------------
//  Collection structure for sirena_operation_event
// ----------------------------
db.getCollection("sirena_operation_event").drop();
db.createCollection("sirena_operation_event");
db.sirena_operation_event.createIndex({createTime: -1, channelId: 1});
db.sirena_operation_event.createIndex({updateTime: 1, reportStatus: 1});
db.sirena_operation_event.createIndex({updateTime: 1, eventStatus: 1});
db.sirena_operation_event.createIndex({playerName: 1, effectedTime: 1, expiredTime: 1, eventStatus: 1});
db.sirena_operation_event.createIndex({eventId: 1}, {unique: true});
db.sirena_operation_event.createIndex({expiredTime: 1, reportStatus: 1});
db.sirena_operation_event.createIndex({eventStatus:1, reportStatus:1, _id:1 , updateTime : -1});

// ----------------------------
//  Collection structure for sirena_operation_event_detail
// ----------------------------
db.getCollection("sirena_operation_event_detail").drop();
db.createCollection("sirena_operation_event_detail");
db.sirena_operation_event_detail.createIndex({createTime: 1});
db.sirena_operation_event_detail.createIndex({operatorRecordId: 1});
db.sirena_operation_event_detail.createIndex({betStatus: 1, _id: 1,  createTime: -1});

// ----------------------------
//  Collection structure for sirena_operation_event_config
// ----------------------------
db.getCollection("sirena_operation_event_config").drop();
db.createCollection("sirena_operation_event_config");
db.sirena_operation_event_config.createIndex({multiple: 1});

// ----------------------------
//  Collection structure for sirena_operation_event_config_record
// ----------------------------
db.getCollection("sirena_operation_event_config_record").drop();
db.createCollection("sirena_operation_event_config_record");
db.sirena_operation_event_config_record.createIndex({betRecordId: 1});
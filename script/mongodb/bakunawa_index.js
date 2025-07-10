// ----------------------------
//  Collection structure for mariamakiling_bet_record
// ----------------------------
use bakunawa;
db.getCollection("bakunawa_bet_record").drop();
db.createCollection("bakunawa_bet_record");
// 索引bakunawa_bet_record-c端查询需要
db.bakunawa_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1})
db.bakunawa_bet_record.createIndex({createTime: -1, status: 1, _id: 1})
// 索引bakunawa_bet_record-b端查询需要-使用覆盖索引
db.bakunawa_bet_record.createIndex({createTime: -1, channelId: 1, status:1, payAmount:1, payOutAmount:1 ,userId:1, betPromotionType:1})

// ----------------------------
//  Collection structure for bakunawa_game_record
// ----------------------------
db.getCollection("bakunawa_game_record").drop();
db.createCollection("bakunawa_game_record");
// 索引game_record-c端查询需要-新
db.bakunawa_game_record.createIndex({betRecordId: 1, batchId: 1})

// ----------------------------
//  Collection structure for bakunawa_operation_event
// ----------------------------
db.getCollection("bakunawa_operation_event").drop();
db.createCollection("bakunawa_operation_event");
db.bakunawa_operation_event.createIndex({createTime: -1, channelId: 1});
db.bakunawa_operation_event.createIndex({updateTime: 1, reportStatus: 1});
db.bakunawa_operation_event.createIndex({updateTime: 1, eventStatus: 1});
db.bakunawa_operation_event.createIndex({playerName: 1, effectedTime: 1, expiredTime: 1, eventStatus: 1});
db.bakunawa_operation_event.createIndex({eventId: 1}, {unique: true});
db.bakunawa_operation_event.createIndex({expiredTime: 1, reportStatus: 1});
db.bakunawa_operation_event.createIndex({eventStatus:1, reportStatus:1, _id:1 , updateTime : -1});

// ----------------------------
//  Collection structure for bakunawa_operation_event_detail
// ----------------------------
db.getCollection("bakunawa_operation_event_detail").drop();
db.createCollection("bakunawa_operation_event_detail");
db.bakunawa_operation_event_detail.createIndex({createTime: 1});
db.bakunawa_operation_event_detail.createIndex({operatorRecordId: 1});
db.bakunawa_operation_event_detail.createIndex({betStatus: 1, _id: 1,  createTime: -1});

// ----------------------------
//  Collection structure for operation_event_config
// ----------------------------
db.getCollection("bakunawa_operation_event_config").drop();
db.createCollection("bakunawa_operation_event_config");
db.bakunawa_operation_event_config.createIndex({multiple: 1});

// ----------------------------
//  Collection structure for bakunawa_operation_event_config_record
// ----------------------------
db.getCollection("bakunawa_operation_event_config_record").drop();
db.createCollection("bakunawa_operation_event_config_record");
db.bakunawa_operation_event_config_record.createIndex({betRecordId: 1});
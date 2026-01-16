// ----------------------------
//  Collection structure for atlantis_bet_record
// ----------------------------
use malakas;
db.getCollection("atlantis_bet_record").drop();
db.createCollection("atlantis_bet_record");
// 索引atlantis_bet_record-c端查询需要
db.atlantis_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1})
db.atlantis_bet_record.createIndex({createTime: -1, status: 1, _id: 1})
// 索引atlantis_bet_record-b端查询需要-使用覆盖索引
db.atlantis_bet_record.createIndex({createTime: -1, channelId: 1, status:1, payAmount:1, payOutAmount:1 ,userId:1, betPromotionType:1})


// ----------------------------
//  Collection structure for atlantis_game_record
// ----------------------------
db.getCollection("atlantis_game_record").drop();
db.createCollection("atlantis_game_record");
// 索引game_record-c端查询需要-新
db.atlantis_game_record.createIndex({betRecordId: 1, batchId: 1})

// ----------------------------
//  Collection structure for atlantis_star_card_record
// ----------------------------
db.getCollection("atlantis_star_card_record").drop();
db.createCollection("atlantis_star_card_record");
db.atlantis_star_card_record.createIndex({createTime:-1, channelId: 1});
db.atlantis_star_card_record.createIndex({reportStatus: 1});

// ----------------------------
//  Collection structure for atlantis_operation_event
// ----------------------------
db.getCollection("atlantis_operation_event").drop();
db.createCollection("atlantis_operation_event");
db.atlantis_operation_event.createIndex({createTime: -1, channelId: 1});
db.atlantis_operation_event.createIndex({updateTime: 1, reportStatus: 1});
db.atlantis_operation_event.createIndex({updateTime: 1, eventStatus: 1});
db.atlantis_operation_event.createIndex({playerName: 1, effectedTime: 1, expiredTime: 1, eventStatus: 1});
db.atlantis_operation_event.createIndex({eventId: 1}, {unique: true});
db.atlantis_operation_event.createIndex({expiredTime: 1, reportStatus: 1});
db.atlantis_operation_event.createIndex({eventStatus:1, reportStatus:1, _id:1 , updateTime : -1});

// ----------------------------
//  Collection structure for atlantis_operation_event_detail
// ----------------------------
db.getCollection("atlantis_operation_event_detail").drop();
db.createCollection("atlantis_operation_event_detail");
db.atlantis_operation_event_detail.createIndex({createTime: 1});
db.atlantis_operation_event_detail.createIndex({operatorRecordId: 1});
db.atlantis_operation_event_detail.createIndex({betStatus: 1, _id: 1,  createTime: -1});

// ----------------------------
//  Collection structure for operation_event_config
// ----------------------------
db.getCollection("atlantis_operation_event_config").drop();
db.createCollection("atlantis_operation_event_config");
db.atlantis_operation_event_config.createIndex({multiple: 1});

// ----------------------------
//  Collection structure for atlantis_operation_event_config_record
// ----------------------------
db.getCollection("atlantis_operation_event_config_record").drop();
db.createCollection("atlantis_operation_event_config_record");
db.atlantis_operation_event_config_record.createIndex({betRecordId: 1});

// ----------------------------
//  Collection structure for bayanihan_bet_record
// ----------------------------
use bayanihan;
db.getCollection("bayanihan_bet_record").drop();
db.createCollection("bayanihan_bet_record");
// 索引bayanihan_bet_record-c端查询需要
db.bayanihan_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1})
db.bayanihan_bet_record.createIndex({createTime: -1, status: 1, _id: 1})

// ----------------------------
//  Collection structure for bayanihan_game_record
// ----------------------------
db.getCollection("bayanihan_game_record").drop();
db.createCollection("bayanihan_game_record");
// 索引game_record-c端查询需要-新
db.bayanihan_game_record.createIndex({betRecordId: 1, batchId: 1})

// ----------------------------
//  Collection structure for bayanihan_star_card_record
// ----------------------------
db.getCollection("bayanihan_star_card_record").drop();
db.createCollection("bayanihan_star_card_record");
db.bayanihan_star_card_record.createIndex({createTime:-1, channelId: 1});
db.bayanihan_star_card_record.createIndex({reportStatus: 1});

// ----------------------------
//  Collection structure for bayanihan_operation_event
// ----------------------------
db.getCollection("bayanihan_operation_event").drop();
db.createCollection("bayanihan_operation_event");
db.bayanihan_operation_event.createIndex({createTime: -1, channelId: 1});
db.bayanihan_operation_event.createIndex({updateTime: 1, reportStatus: 1});
db.bayanihan_operation_event.createIndex({updateTime: 1, eventStatus: 1});
db.bayanihan_operation_event.createIndex({playerName: 1, effectedTime: 1, expiredTime: 1, eventStatus: 1});
db.bayanihan_operation_event.createIndex({eventId: 1}, {unique: true});
db.bayanihan_operation_event.createIndex({expiredTime: 1, reportStatus: 1});
db.bayanihan_operation_event.createIndex({eventStatus:1, reportStatus:1, _id:1 , updateTime : -1});

// ----------------------------
//  Collection structure for bayanihan_operation_event_detail
// ----------------------------
db.getCollection("bayanihan_operation_event_detail").drop();
db.createCollection("bayanihan_operation_event_detail");
db.bayanihan_operation_event_detail.createIndex({createTime: 1});
db.bayanihan_operation_event_detail.createIndex({operatorRecordId: 1});
db.bayanihan_operation_event_detail.createIndex({betStatus: 1, _id: 1,  createTime: -1});

// ----------------------------
//  Collection structure for operation_event_config
// ----------------------------
db.getCollection("bayanihan_operation_event_config").drop();
db.createCollection("bayanihan_operation_event_config");
db.bayanihan_operation_event_config.createIndex({multiple: 1});

// ----------------------------
//  Collection structure for bayanihan_operation_event_config_record
// ----------------------------
db.getCollection("bayanihan_operation_event_config_record").drop();
db.createCollection("bayanihan_operation_event_config_record");
db.bayanihan_operation_event_config_record.createIndex({betRecordId: 1});

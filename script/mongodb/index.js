// ----------------------------
//  Collection structure for mariamakiling_bet_record
// ----------------------------
db.getCollection("mariamakiling_bet_record").drop();
db.createCollection("mariamakiling_bet_record");
// 索引mariamakiling_bet_record-c端查询需要-新
db.mariamakiling_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1})
db.mariamakiling_bet_record.createIndex({status: 1, createTime:-1, _id: 1})

// 索引mariamakiling_bet_record-b端查询需要-新
db.mariamakiling_bet_record.createIndex({createTime: -1, channelId: 1, status:1})

// ----------------------------
//  Collection structure for mariamakiling_game_record
// ----------------------------
db.getCollection("mariamakiling_game_record").drop();
db.createCollection("mariamakiling_game_record");

// 索引game_record-c端查询需要-新
db.mariamakiling_game_record.createIndex({betRecordId: 1, batchId: 1})


// ----------------------------
//  Collection structure for mariamakiling_star_card_record
// ----------------------------
db.getCollection("mariamakiling_star_card_record").drop();
db.createCollection("mariamakiling_star_card_record");

db.mariamakiling_star_card_record.createIndex({createTime:-1, channelId: 1});
db.mariamakiling_star_card_record.createIndex({reportStatus:1});

// ----------------------------
//  Collection structure for mariamakiling_operation_event
// ----------------------------
db.getCollection("mariamakiling_operation_event").drop();
db.createCollection("mariamakiling_operation_event");

db.mariamakiling_operation_event.createIndex({createTime: -1,channelId: 1});
db.mariamakiling_operation_event.createIndex({updateTime:1,reportStatus:1});
db.mariamakiling_operation_event.createIndex({updateTime:1,eventStatus:1});
db.mariamakiling_operation_event.createIndex({playerName: 1,effectedTime: 1,expiredTime: 1,eventStatus: 1});
db.mariamakiling_operation_event.createIndex({ eventId: 1 }, { unique: true });
db.mariamakiling_operation_event.createIndex({expiredTime: 1,reportStatus: 1});

// ----------------------------
//  Collection structure for mariamakiling_operation_event_detail
// ----------------------------
db.getCollection("mariamakiling_operation_event_detail").drop();
db.createCollection("mariamakiling_operation_event_detail");
db.mariamakiling_operation_event_detail.createIndex({ createTime: 1});
db.mariamakiling_operation_event_detail.createIndex({ operatorRecordId: 1});
db.mariamakiling_operation_event_detail.createIndex({ betStatus: 1, _id : 1,  createTime : -1});

// ----------------------------
//  Collection structure for operation_event_config
// ----------------------------
db.getCollection("mariamakiling_operation_event_config").drop();
db.createCollection("mariamakiling_operation_event_config");
db.mariamakiling_operation_event_config.createIndex({multiple:1});

// ----------------------------
//  Collection structure for mariamakiling_operation_event_config_record
// ----------------------------
db.getCollection("mariamakiling_operation_event_config_record").drop();
db.createCollection("mariamakiling_operation_event_config_record");
db.mariamakiling_operation_event_config_record.createIndex({betRecordId:1});
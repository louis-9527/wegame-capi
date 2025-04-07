// ----------------------------
//  Collection structure for mariamakiling_bet_record
// ----------------------------
db.getCollection("mariamakiling_mariamakiling_bet_record").drop();
db.createCollection("mariamakiling_bet_record");
// 索引mariamakiling_bet_record-c端查询需要-新
db.mariamakiling_bet_record.createIndex({playerId: 1, status: 1, createTime: -1})
db.mariamakiling_bet_record.createIndex({status: 1, createTime:-1, _id: 1})

// ----------------------------
//  Collection structure for mariamakiling_game_record
// ----------------------------
db.getCollection("mariamakiling_game_record").drop();
db.createCollection("mariamakiling_game_record");

// 索引game_record-c端查询需要-新
db.mariamakiling_game_record.createIndex({betRecordId: 1, batchId: 1})

db.getCollection("mariamakiling_star_card_record").drop();
db.createCollection("mariamakiling_star_card_record");
db.mariamakiling_star_card_record.createIndex({ createTime: -1 });
db.getCollection("malakas_bet_record").drop();
db.createCollection("malakas_bet_record");
// malakas_bet_record-c端查询需要
db.malakas_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1})
db.malakas_bet_record.createIndex({status: 1, createTime:-1, _id: 1})
// malakas_bet_record-b端查询需要
db.malakas_bet_record.createIndex({createTime: -1, channelId: 1, status:1})

// ----------------------------
//  Collection structure for mariamakiling_game_record
// ----------------------------
db.getCollection("malakas_game_record").drop();
db.createCollection("malakas_game_record");
// malakas_game_record-c端查询需要-新
db.malakas_game_record.createIndex({betRecordId: 1, batchId: 1})
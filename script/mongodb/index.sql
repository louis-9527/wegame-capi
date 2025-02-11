// ----------------------------
// Collection structure for bet_record
// ----------------------------
db.getCollection("bet_record").drop();
db.getCollection("game_record").drop();
db.createCollection
("bet_record");
db.createCollection
("game_record");

// 索引bet_record-c端查询需要-新
db.bet_record.createIndex({createTime:-1, status:1, _id:1})
db.bet_record.createIndex({createTime:-1, playerId:1,_id:1})
// 索引game_record-c端查询需要-新
db.game_record.createIndex({betRecordId:1,batchId:1})


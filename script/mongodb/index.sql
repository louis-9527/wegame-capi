
// ----------------------------
// Collection structure for bet_record
// ----------------------------
db.getCollection("maria_makiling_bet_record").drop();
db.createCollection("maria_makiling_bet_record");

分片策略：表：bet_record  字段：userId

// 索引-c端查询需要
db.maria_makiling_bet_record.createIndex({createTime:-1, playerId:1, _id:1})
// 索引-b端查询需要
// db.maria_makiling_bet_record.createIndex({createTime:-1,playerId:1});


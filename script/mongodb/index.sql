
// ----------------------------
// Collection structure for bet_record
// ----------------------------
db.getCollection("bet_record").drop();
db.createCollection("bet_record");

分片策略：表：bet_record  字段：playerId
索引：
db.bet_record.createIndex(
   {
      "playerId": 1,
   }
)

// 索引-c端查询需要
db.maria_makiling_bet_record.createIndex({userId:1,createTime:-1})
// b端查询需要
db.maria_makiling_bet_record.createIndex({gameId:1,createTime:-1})


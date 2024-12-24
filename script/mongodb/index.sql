
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


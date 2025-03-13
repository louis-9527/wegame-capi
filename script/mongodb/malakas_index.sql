-- ------------------------------------------------
--  Collection structure for malakas_bet_record
-- ------------------------------------------------
db.getCollection("malakas_bet_record").drop();
db.createCollection("malakas_bet_record");

-- 索引malakas_bet_record-c端查询需要-新
db.malakas_bet_record.createIndex({playerId: 1, status: 1, createTime: -1})
db.malakas_bet_record.createIndex({status: 1, createTime:-1, _id: 1})

-- ------------------------------------------------
--   Collection structure for malakas_game_record
-- ------------------------------------------------
db.getCollection("malakas_game_record").drop();
db.createCollection("malakas_game_record");

-- 索引malakas_game_record-c端查询需要-新
db.malakas_game_record.createIndex({betRecordId: 1, batchId: 1})


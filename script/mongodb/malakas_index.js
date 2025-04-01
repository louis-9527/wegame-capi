db.getCollection("malakas_bet_record").drop();
db.createCollection("malakas_bet_record");
db.malakas_bet_record.createIndex({playerId: 1, status: 1, createTime: -1})
db.malakas_bet_record.createIndex({status: 1, _id : 1})

db.getCollection("malakas_game_record").drop();
db.createCollection("malakas_game_record");
db.malakas_game_record.createIndex({betRecordId: 1, batchId: 1})


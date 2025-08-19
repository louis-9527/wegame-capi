// ----------------------------
//  Collection structure for mariamakiling_bet_record
// ----------------------------
use makiling;
db.getCollection("mariamakiling_bet_record_template").drop();
db.createCollection("mariamakiling_bet_record_template");
// 索引mariamakiling_bet_record_template
db.mariamakiling_bet_record_template.createIndex({multiple: 1, betType: 1, _id: 1})


db.getCollection("mariamakiling_game_record_template").drop();
db.createCollection("mariamakiling_game_record_template");
// 索引mariamakiling_bet_record_template
db.mariamakiling_game_record_template.createIndex({betRecordId: 1})


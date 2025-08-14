// ----------------------------
//  Collection structure for mariamakiling_bet_record
// ----------------------------
use makiling;
db.getCollection("mariamakiling_bet_record_template_temp").drop();
db.createCollection("mariamakiling_bet_record_template_temp");
// 索引mariamakiling_bet_record_template_temp
db.mariamakiling_bet_record_template_temp.createIndex({multiple: 1, betType: 1, _id: 1})


db.getCollection("mariamakiling_game_record_template_temp").drop();
db.createCollection("mariamakiling_game_record_template_temp");
// 索引mariamakiling_bet_record_template_temp
db.mariamakiling_game_record_template_temp.createIndex({betRecordId: 1})


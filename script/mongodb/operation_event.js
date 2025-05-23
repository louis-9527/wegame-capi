// ----------------------------
// Collection structure for mariamakiling_operation_event
// ----------------------------
db.getCollection("mariamakiling_operation_event").drop();
db.createCollection("mariamakiling_operation_event");
db.mariamakiling_operation_event.createIndex({ "eventId": 1 }, { unique: true });
db.mariamakiling_operation_event.createIndex({"playerName": 1,"gameId": 1,"eventStatus": 1,"reportStatus":1,"expiredTime": 1,"effectedTime": 1,"createTime": 1})


db.mariamakiling_operation_event_config.createIndex({"multiple": 1})
db.mariamakiling_operation_event_config_record.createIndex({"betRecordId": 1})



// db["mariamakiling_operation_event"].createIndex({ "eventId": 1 }, { unique: true });
// ----------------------------
db.getCollection("malakas_operation_event").drop();
db.createCollection("malakas_operation_event");
db.malakas_operation_event.createIndex({ "eventId": 1 }, { unique: true });
// ----------------------------
db.getCollection("ibongadarna_operation_event").drop();
db.createCollection("ibongadarna_operation_event");
db.malakas_operation_event.createIndex({ "eventId": 1 }, { unique: true });

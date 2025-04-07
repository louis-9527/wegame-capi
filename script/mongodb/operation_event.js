// ----------------------------
// Collection structure for mariamakiling_operation_event
// ----------------------------
db.getCollection("mariamakiling_operation_event").drop();
db.createCollection("mariamakiling_operation_event");
db.mariamakiling_operation_event.createIndex({ "eventId": 1 }, { unique: true });
// db["mariamakiling_operation_event"].createIndex({ "eventId": 1 }, { unique: true });
// ----------------------------
db.getCollection("malakas_operation_event").drop();
db.createCollection("malakas_operation_event");
db.malakas_operation_event.createIndex({ "eventId": 1 }, { unique: true });
// ----------------------------
db.getCollection("ibongadarna_operation_event").drop();
db.createCollection("ibongadarna_operation_event");
db.malakas_operation_event.createIndex({ "eventId": 1 }, { unique: true });

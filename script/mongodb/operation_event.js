// ----------------------------
// Collection structure for mariamakiling_operation_event
// ----------------------------
db.getCollection("mariamakiling_operation_event").drop();
db.createCollection("mariamakiling_operation_event");
db.mariamakiling_operation_event.createIndex({ "eventId": 1 }, { unique: true });
// db["mariamakiling_operation_event"].createIndex({ "eventId": 1 }, { unique: true });

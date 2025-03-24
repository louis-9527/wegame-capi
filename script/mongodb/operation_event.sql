----------------------------
Collection structure for mariamakiling_dynamicOperation_event
----------------------------
db.getCollection("mariamakiling_dynamicOperation_event").drop();
db.createCollection("mariamakiling_dynamicOperation_event");
db.mariamakiling_dynamicOperation_event.createIndex({ "eventId": 1 }, { unique: true });
# db["mariamakiling_dynamicOperation_event"].createIndex({ "eventId": 1 }, { unique: true });

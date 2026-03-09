CREATE TEMPORARY TABLE mongo_operation_event_config_source (
  `_id` STRING,
  gameId STRING,
  betType STRING,
  multiple DOUBLE,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:D180HNf5aXMuhIvt@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/bakunawa?ssl=false&readPreference=secondary',
  'database' = 'bakunawa',
  'collection' = 'bakunawa_operation_event_config',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_operation_event_config_sink (
  `_id` STRING,
  gameId STRING,
  betType STRING,
  multiple DOUBLE,
  syncCreateTime TIMESTAMP,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'bakunawa.bakunawa_operation_event_config',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_operation_event_config_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

INSERT INTO doris_operation_event_config_sink
SELECT
  `_id`,
  gameId,
  betType,
  multiple,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_operation_event_config_source;
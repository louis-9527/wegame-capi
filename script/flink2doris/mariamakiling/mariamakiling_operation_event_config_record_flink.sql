CREATE TEMPORARY TABLE mongo_operation_event_config_record_source (
  `_id` STRING,
  parentId BIGINT,
  batchId BIGINT,
  betRecordId BIGINT,
  index INT,
  gameRecordIds ARRAY<BIGINT>,
  wheel ROW<id BIGINT, name STRING, `value` BIGINT>,
  extInfo ROW<key STRING, `value` STRING>,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/makiling?ssl=false&readPreference=secondary',
  'database' = 'makiling',
  'collection' = 'mariamakiling_operation_event_config_record',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_operation_event_config_record_sink (
  `_id` STRING,
  parentId BIGINT,
  batchId BIGINT,
  betRecordId BIGINT,
  index INT,
  gameRecordIds VARCHAR,
  wheel VARCHAR,
  extInfo VARCHAR,
  syncCreateTime TIMESTAMP,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = '10.147.68.89:8031',
  'table.identifier' = 'makiling.mariamakiling_operation_event_config_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'gNIFe0zzbC4U7D24h3L',
  'sink.label-prefix' = 'doris_operation_event_config_record_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

INSERT INTO doris_operation_event_config_record_sink
SELECT
  `_id`,
  parentId,
  batchId,
  betRecordId,
  index,
  CAST(gameRecordIds AS STRING) AS gameRecordIds,
  CAST(wheel AS STRING) AS wheel,
  CAST(extInfo AS STRING) AS extInfo,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_operation_event_config_record_source;
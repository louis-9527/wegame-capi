CREATE TEMPORARY TABLE mongo_operation_event_config_record_source (
  `_id` STRING,
  parentId BIGINT,
  batchId BIGINT,
  betRecordId BIGINT,
  `index` INT,
  gameRecordIds STRING,
  wheel STRING,
  extInfo STRING,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:D180HNf5aXMuhIvt@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/kapre?ssl=false&readPreference=secondary',
  'database' = 'kapre',
  'collection' = 'kapre_operation_event_config_record',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_operation_event_config_record_sink (
  `_id` STRING,
  parentId BIGINT,
  batchId BIGINT,
  betRecordId BIGINT,
  `index` INT,
  gameRecordIds VARCHAR,
  wheel VARCHAR,
  extInfo VARCHAR,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'kapre.kapre_operation_event_config_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
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
  gameRecordIds,
  wheel,
  extInfo,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000
FROM mongo_operation_event_config_record_source;
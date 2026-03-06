CREATE TEMPORARY TABLE mongo_game_metrics_source (
  `_id` BIGINT,
  channelId STRING,
  currentRtp STRING,
  gameId STRING,
  pId STRING,
  totalBetAmount BIGINT,
  totalPayoutAmount BIGINT,
  updateTime BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_game_metrics',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_game_metrics_sink (
  `_id` STRING,
  pId STRING,
  channelId STRING,
  gameId STRING,
  currentRtp STRING,
  totalBetAmount BIGINT,
  totalPayoutAmount BIGINT,
  updateTime TIMESTAMP,
  syncCreateTime TIMESTAMP,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = '10.147.68.89:8031',
  'table.identifier' = 'ibong.ibongadarna_game_metrics',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'gNIFe0zzbC4U7D24h3L',
  'sink.label-prefix' = 'doris_game_metrics_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

INSERT INTO doris_game_metrics_sink
SELECT
  CAST(`_id` AS STRING) AS `_id`,
  pId,
  channelId,
  gameId,
  currentRtp,
  totalBetAmount,
  totalPayoutAmount,
  TO_TIMESTAMP_LTZ(updateTime, 3) AS updateTime,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_game_metrics_source;


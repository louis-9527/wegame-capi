CREATE TEMPORARY TABLE mongo_game_record_source (
  `_id` STRING,
  betRecordId BIGINT,
  parentId BIGINT,
  batchId BIGINT,
  index INT,
  playerId STRING,
  playerName STRING,
  overallRewardAmount BIGINT,
  accumulateReachMaxMultiple BOOLEAN,
  freeWheelFlag BOOLEAN,
  lastGameFlag BOOLEAN,
  gameRecordIds STRING,
  rewardResult STRING,
  wheel STRING,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/makiling?ssl=false&readPreference=secondary',
  'database' = 'makiling',
  'collection' = 'mariamakiling_game_record',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_game_record_sink (
  `_id` STRING,
  betRecordId BIGINT,
  parentId BIGINT,
  batchId BIGINT,
  index INT,
  playerId STRING,
  playerName STRING,
  overallRewardAmount BIGINT,
  accumulateReachMaxMultiple BOOLEAN,
  freeWheelFlag BOOLEAN,
  lastGameFlag BOOLEAN,
  gameRecordIds STRING,
  rewardResult STRING,
  wheel STRING,
  syncCreateTime TIMESTAMP,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = '10.147.68.89:8031',
  'table.identifier' = 'makiling.mariamakiling_game_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'gNIFe0zzbC4U7D24h3L',
  'sink.label-prefix' = 'doris_game_record_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

INSERT INTO doris_game_record_sink
SELECT
  `_id`,
  betRecordId,
  parentId,
  batchId,
  index,
  playerId,
  playerName,
  overallRewardAmount,
  accumulateReachMaxMultiple,
  freeWheelFlag,
  lastGameFlag,
  gameRecordIds,
  rewardResult,
  wheel,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_game_record_source;
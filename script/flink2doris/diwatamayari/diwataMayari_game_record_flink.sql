CREATE TEMPORARY TABLE mongo_game_record_source (
  `_id` STRING,
  betRecordId BIGINT,
  playerId STRING,
  playerName STRING,
  rewardResult STRING,
  overallRewardAmount BIGINT,
  wheel STRING,
  creator STRING,
  updater STRING,
  updateTime BIGINT,
  createTime BIGINT,
  expireAt BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:D180HNf5aXMuhIvt@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/diwata_mayari?ssl=false&readPreference=secondary',
  'database' = 'diwata_mayari',
  'collection' = 'diwataMayarigame_record',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_game_record_sink (
  `_id` STRING,
  betRecordId BIGINT,
  playerId STRING,
  playerName STRING,
  rewardResult STRING,
  overallRewardAmount BIGINT,
  wheel STRING,
  creator STRING,
  updater STRING,
  updateTime BIGINT,
  createTime BIGINT,
  expireAt BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'diwata_mayari.diwataMayari_game_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
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
  playerId,
  playerName,
  rewardResult,
  overallRewardAmount,
  wheel,
  creator,
  createTime,
  updater,
  updateTime,
  expireAt,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000
FROM mongo_game_record_source;
CREATE TEMPORARY TABLE mongo_jackpot_user_reward_record_source (
  `_id` BIGINT,
  _class STRING,
  betId STRING,
  betRecordId BIGINT,
  createTime BIGINT,
  creator STRING,
  gameRecordId BIGINT,
  playerName STRING,
  rewardAmount BIGINT,
  rewardGrantSerialNo STRING,
  rewardPoolId BIGINT,
  updateTime BIGINT,
  updater STRING,
  userId BIGINT,
  rewardGrantStatus INT,
  rewardPoolType INT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:D180HNf5aXMuhIvt@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_game_record',
  'scan.startup.mode' = 'initial'
);
CREATE TEMPORARY TABLE doris_jackpot_user_reward_record_sink (
  `_id` STRING,
  betId STRING,
  betRecordId BIGINT,
  createTime TIMESTAMP,
  creator STRING,
  gameRecordId BIGINT,
  playerName STRING,
  rewardAmount BIGINT,
  rewardGrantSerialNo STRING,
  rewardPoolId BIGINT,
  updateTime TIMESTAMP,
  updater STRING,
  userId BIGINT,
  rewardGrantStatus INT,
  rewardPoolType INT,
  syncCreateTime TIMESTAMP,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'lamang.lamang_game_record',
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

INSERT INTO doris_jackpot_user_reward_record_sink
SELECT
  CAST(`_id` AS STRING) AS `_id`,
  betId,
  betRecordId,
  TO_TIMESTAMP_LTZ(createTime, 3) AS createTime,
  creator,
  gameRecordId,
  playerName,
  rewardAmount,
  rewardGrantSerialNo,
  rewardPoolId,
  TO_TIMESTAMP_LTZ(updateTime, 3) AS updateTime,
  updater,
  userId,
  rewardGrantStatus,
  rewardPoolType,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_jackpot_user_reward_record_source;

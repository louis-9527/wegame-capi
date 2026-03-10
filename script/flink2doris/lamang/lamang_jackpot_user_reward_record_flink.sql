CREATE TEMPORARY TABLE mongo_jackpot_user_reward_record_source (
  `_id` BIGINT,
  betId STRING,
  betRecordId BIGINT,
  gameRecordId BIGINT,
  playerName STRING,
  rewardAmount BIGINT,
  rewardGrantSerialNo STRING,
  rewardPoolId BIGINT,
  userId BIGINT,
  rewardGrantStatus INT,
  rewardPoolType INT,
  creator STRING,
  createTime BIGINT,
  updater STRING,
  updateTime BIGINT,
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
  gameRecordId BIGINT,
  playerName STRING,
  rewardAmount BIGINT,
  rewardGrantSerialNo STRING,
  rewardPoolId BIGINT,
  userId BIGINT,
  rewardGrantStatus INT,
  rewardPoolType INT,
  creator STRING,
  createTime BIGINT,
  updater STRING,
  updateTime BIGINT,
  syncCreateTime BIGINT,
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
  `_id`,
  betId,
  betRecordId,
  gameRecordId,
  playerName,
  rewardAmount,
  rewardGrantSerialNo,
  rewardPoolId,
  userId,
  rewardGrantStatus,
  rewardPoolType,
  creator,
  createTime,
  updater,
  updateTime,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000
FROM mongo_jackpot_user_reward_record_source;

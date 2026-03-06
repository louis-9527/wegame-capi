CREATE TEMPORARY TABLE mongo_jackpot_reward_pool_input_record_source (
  `_id` BIGINT,
  accFlag INT,
  createTime BIGINT,
  creator STRING,
  expireAtTime TIMESTAMP,
  grandAmount BIGINT,
  majorAmount BIGINT,
  miniAmount BIGINT,
  type INT,
  updateTime BIGINT,
  updater STRING,
  _class STRING,
  minorAmount BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:D180HNf5aXMuhIvt@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_game_record',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_jackpot_reward_pool_input_record_sink (
  `_id` STRING,
  accFlag INT,
  createTime TIMESTAMP,
  creator STRING,
  expireAtTime TIMESTAMP,
  grandAmount BIGINT,
  majorAmount BIGINT,
  miniAmount BIGINT,
  type INT,
  updateTime TIMESTAMP,
  updater STRING,
  minorAmount BIGINT,
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

INSERT INTO doris_jackpot_reward_pool_input_record_sink
SELECT
  CAST(`_id` AS STRING) AS `_id`,
  accFlag,
  TO_TIMESTAMP_LTZ(createTime, 3) AS createTime,
  creator,
  expireAtTime,
  grandAmount,
  majorAmount,
  miniAmount,
  type,
  TO_TIMESTAMP_LTZ(updateTime, 3) AS updateTime,
  updater,
  minorAmount,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_jackpot_reward_pool_input_record_source;

CREATE TEMPORARY TABLE mongo_user_play_metrics_source (
  `_id` STRING,
  pId STRING,
  channelId STRING,
  gameId STRING,
  playerId STRING,
  playerName STRING,
  totalBetAmount BIGINT,
  totalPayoutAmount BIGINT,
  totalGameCount BIGINT,
  continuousLostCount INT,
  continuousNoFreeSpinCount BIGINT,
  lastBetTime BIGINT,
  version BIGINT,
  extend STRING,
  createTime BIGINT,
  creator STRING,
  updateTime BIGINT,
  updater STRING,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_user_play_metrics',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_user_play_metrics_sink (
  `_id` STRING,
  pId STRING,
  channelId STRING,
  gameId STRING,
  playerId STRING,
  playerName STRING,
  totalBetAmount BIGINT,
  totalPayoutAmount BIGINT,
  totalGameCount BIGINT,
  continuousLostCount INT,
  continuousNoFreeSpinCount BIGINT,
  lastBetTime TIMESTAMP,
  version BIGINT,
  extend VARCHAR,
  createTime TIMESTAMP,
  creator STRING,
  updateTime TIMESTAMP,
  updater STRING,
  syncCreateTime TIMESTAMP,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = '10.147.68.89:8031',
  'table.identifier' = 'ibong.ibongadarna_user_play_metrics',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'gNIFe0zzbC4U7D24h3L',
  'sink.label-prefix' = 'doris_user_play_metrics_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

INSERT INTO doris_user_play_metrics_sink
SELECT
  `_id`,
  pId,
  channelId,
  gameId,
  playerId,
  playerName,
  totalBetAmount,
  totalPayoutAmount,
  totalGameCount,
  continuousLostCount,
  continuousNoFreeSpinCount,
  TO_TIMESTAMP_LTZ(lastBetTime, 3) AS lastBetTime,
  version,
  extend,
  TO_TIMESTAMP_LTZ(createTime, 3) AS createTime,
  creator,
  TO_TIMESTAMP_LTZ(updateTime, 3) AS updateTime,
  updater,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_user_play_metrics_source;


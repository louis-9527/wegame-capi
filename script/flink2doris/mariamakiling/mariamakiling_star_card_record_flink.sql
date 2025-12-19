CREATE TEMPORARY TABLE mongo_star_card_record_source (
  `_id` STRING,
  rewardId STRING,
  rewardConfigId STRING,
  gameId STRING,
  channelId STRING,
  betRecordId BIGINT,
  playerId STRING,
  playName STRING,
  betAmount BIGINT,
  payOutAmount BIGINT,
  useStatus INT,
  failMsg STRING,
  failCode INT,
  reportStatus INT,
  createTime BIGINT,
  updateTime BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/makiling?ssl=false&readPreference=secondary',
  'database' = 'makiling',
  'collection' = 'mariamakiling_star_card_record',
  'scan.startup.mode' = 'initial'
);

CREATE TEMPORARY TABLE doris_star_card_record_sink (
  `_id` STRING,
  rewardId STRING,
  rewardConfigId STRING,
  gameId STRING,
  channelId STRING,
  betRecordId BIGINT,
  playerId STRING,
  playName STRING,
  betAmount BIGINT,
  payOutAmount BIGINT,
  useStatus INT,
  failMsg VARCHAR,
  failCode INT,
  reportStatus INT,
  createTime TIMESTAMP,
  updateTime TIMESTAMP,
  syncCreateTime TIMESTAMP,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = '10.147.68.89:8031',
  'table.identifier' = 'makiling.mariamakiling_star_card_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'gNIFe0zzbC4U7D24h3L',
  'sink.label-prefix' = 'doris_star_card_record_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

INSERT INTO doris_star_card_record_sink
SELECT
  `_id`,
  rewardId,
  rewardConfigId,
  gameId,
  channelId,
  betRecordId,
  playerId,
  playName,
  betAmount,
  payOutAmount,
  useStatus,
  failMsg,
  failCode,
  reportStatus,
  TO_TIMESTAMP_LTZ(createTime, 3) AS createTime,
  TO_TIMESTAMP_LTZ(updateTime, 3) AS updateTime,
  LOCALTIMESTAMP AS syncCreateTime
FROM mongo_star_card_record_source;
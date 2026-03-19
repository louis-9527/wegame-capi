CREATE TEMPORARY TABLE mongo_bet_record_source (
  `_id` STRING,
  betAmount BIGINT,
  betId STRING,
  betPromotionType INT,
  betSerialNo STRING,
  betSubType STRING,
  betType STRING,
  bizInterfereType INT,
  bizInterfereSubType INT,
  channelId STRING,
  gameId STRING,
  pId STRING,
  payAmount BIGINT,
  payOutAmount BIGINT,
  playerId STRING,
  playerName STRING,
  userId BIGINT,
  status INT,
  reachMaxMultiple BOOLEAN,
  createTime BIGINT,
  betTime BIGINT,
  updateTime BIGINT,
  creator STRING,
  updater STRING,
  expireAt BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_bet_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_game_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE mongo_operation_event_config_source (
  `_id` STRING,
  gameId STRING,
  betType STRING,
  multiple DOUBLE,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_operation_event_config',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_operation_event_config_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE mongo_operation_event_detail_source (
  `_id` STRING,
  operatorRecordId BIGINT,
  eventId STRING,
  operatorGameId STRING,
  configId BIGINT,
  playerId STRING,
  playerName STRING,
  userId STRING,
  betAmount BIGINT,
  payOutAmount BIGINT,
  eventAmount BIGINT,
  payOutTime BIGINT,
  betTime BIGINT,
  betId STRING,
  betRecordId BIGINT,
  betStatus INT,
  channelId STRING,
  gameId STRING,
  createTime BIGINT,
  updateTime BIGINT,
  expireAt BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_operation_event_detail',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE mongo_operation_event_source (
  `_id` STRING,
  eventId STRING,
  playerName STRING,
  bonus BIGINT,
  minBonus BIGINT,
  maxBonus BIGINT,
  finishBonus BIGINT,
  eventKind INT,
  gameId STRING,
  channelId STRING,
  effectedTime BIGINT,
  expiredTime BIGINT,
  eventStatus INT,
  eventSubStatus INT,
  reportStatus INT,
  eventMsgDetail STRING,
  reportFailMsg STRING,
  reportTime BIGINT,
  createTime BIGINT,
  updateTime BIGINT,
  expireAt BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_operation_event',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE mongo_star_card_record_source (
  `_id` STRING,
  pId STRING,
  channelId STRING,
  gameId STRING,
  rewardId STRING,
  rewardConfigId STRING,
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
  expireAt BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/ibong?ssl=false&readPreference=secondary',
  'database' = 'ibong',
  'collection' = 'ibongadarna_star_card_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE doris_bet_record_sink (
  `_id` STRING,
  betAmount BIGINT,
  betId STRING,
  betPromotionType INT,
  betSerialNo STRING,
  betSubType STRING,
  betType STRING,
  bizInterfereType INT,
  bizInterfereSubType INT,
  channelId STRING,
  gameId STRING,
  pId STRING,
  payAmount BIGINT,
  payOutAmount BIGINT,
  playerId STRING,
  playerName STRING,
  userId BIGINT,
  status INT,
  reachMaxMultiple BOOLEAN,
  createTime BIGINT,
  betTime BIGINT,
  updateTime BIGINT,
  creator STRING,
  updater STRING,
  expireAt BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'ibong.ibongadarna_bet_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_bet_record_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
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
  'table.identifier' = 'ibong.ibongadarna_game_record',
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

CREATE TEMPORARY TABLE doris_operation_event_config_sink (
  `_id` STRING,
  gameId STRING,
  betType STRING,
  multiple DOUBLE,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'ibong.ibongadarna_operation_event_config',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_operation_event_config_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
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
  'table.identifier' = 'ibong.ibongadarna_operation_event_config_record',
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

CREATE TEMPORARY TABLE doris_operation_event_detail_sink (
  `_id` STRING,
  operatorRecordId BIGINT,
  eventId STRING,
  operatorGameId STRING,
  configId BIGINT,
  playerId STRING,
  playerName STRING,
  userId STRING,
  betAmount BIGINT,
  payOutAmount BIGINT,
  eventAmount BIGINT,
  payOutTime BIGINT,
  betTime BIGINT,
  betId STRING,
  betRecordId BIGINT,
  betStatus INT,
  channelId STRING,
  gameId STRING,
  createTime BIGINT,
  updateTime BIGINT,
  expireAt BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'ibong.ibongadarna_operation_event_detail',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_operation_event_detail_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

CREATE TEMPORARY TABLE doris_operation_event_sink (
  `_id` STRING,
  eventId STRING,
  playerName STRING,
  bonus BIGINT,
  minBonus BIGINT,
  maxBonus BIGINT,
  finishBonus BIGINT,
  eventKind INT,
  gameId STRING,
  channelId STRING,
  effectedTime BIGINT,
  expiredTime BIGINT,
  eventStatus INT,
  eventSubStatus INT,
  reportStatus INT,
  eventMsgDetail VARCHAR,
  reportFailMsg VARCHAR,
  reportTime BIGINT,
  createTime BIGINT,
  updateTime BIGINT,
  expireAt BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'ibong.ibongadarna_operation_event',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_operation_event_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

CREATE TEMPORARY TABLE doris_star_card_record_sink (
  `_id` STRING,
  pId STRING,
  channelId STRING,
  gameId STRING,
  rewardId STRING,
  rewardConfigId STRING,
  betRecordId BIGINT,
  playerId STRING,
  playName STRING,
  betAmount BIGINT,
  payOutAmount BIGINT,
  useStatus INT,
  failMsg VARCHAR,
  failCode INT,
  reportStatus INT,
  createTime BIGINT,
  updateTime BIGINT,
  expireAt BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'ibong.ibongadarna_star_card_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_star_card_record_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

BEGIN STATEMENT SET;

INSERT INTO doris_bet_record_sink
SELECT
  `_id`,
  betAmount,
  betId,
  betPromotionType,
  betSerialNo,
  betSubType,
  betType,
  bizInterfereType,
  bizInterfereSubType,
  channelId,
  gameId,
  pId,
  payAmount,
  payOutAmount,
  playerId,
  playerName,
  userId,
  status,
  reachMaxMultiple,
  createTime,
  betTime,
  updateTime,
  creator,
  updater,
  expireAt,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_bet_record_source;

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
  updater,
  updateTime,
  createTime,
  expireAt,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_game_record_source;

INSERT INTO doris_operation_event_config_sink
SELECT
  `_id`,
  gameId,
  betType,
  multiple,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_operation_event_config_source;

INSERT INTO doris_operation_event_config_record_sink
SELECT
  `_id`,
  parentId,
  batchId,
  betRecordId,
  `index`,
  gameRecordIds,
  wheel,
  extInfo,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_operation_event_config_record_source;

INSERT INTO doris_operation_event_detail_sink
SELECT
  `_id`,
  operatorRecordId,
  eventId,
  operatorGameId,
  configId,
  playerId,
  playerName,
  userId,
  betAmount,
  payOutAmount,
  eventAmount,
  payOutTime,
  betTime,
  betId,
  betRecordId,
  betStatus,
  channelId,
  gameId,
  createTime,
  updateTime,
  expireAt,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_operation_event_detail_source;

INSERT INTO doris_operation_event_sink
SELECT
  `_id`,
  eventId,
  playerName,
  bonus,
  minBonus,
  maxBonus,
  finishBonus,
  eventKind,
  gameId,
  channelId,
  effectedTime,
  expiredTime,
  eventStatus,
  eventSubStatus,
  reportStatus,
  eventMsgDetail,
  reportFailMsg,
  reportTime,
  createTime,
  updateTime,
  expireAt,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_operation_event_source;

INSERT INTO doris_star_card_record_sink
SELECT
  `_id`,
  pId,
  channelId,
  gameId,
  rewardId,
  rewardConfigId,
  betRecordId,
  playerId,
  playName,
  betAmount,
  payOutAmount,
  useStatus,
  failMsg,
  failCode,
  reportStatus,
  createTime,
  updateTime,
  expireAt,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_star_card_record_source;

END;
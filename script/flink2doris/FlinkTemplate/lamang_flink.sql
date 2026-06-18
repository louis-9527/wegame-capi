CREATE TEMPORARY TABLE mongo_bet_record_source (
  `_id` STRING,
  betAmount BIGINT,
  betId STRING,
  betPromotionType INT,
  betSerialNo STRING,
  betSubType STRING,
  betType STRING,
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
  credit BIGINT,
  extendInfo STRING,
  updateTime BIGINT,
  creator STRING,
  updater STRING,
  expireAt BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_bet_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE mongo_game_record_source (
  `_id` STRING,
  betRecordId BIGINT,
  parentId BIGINT,
  batchId BIGINT,
  `index` INT,
  jackpotDrawTimes INT,
  playerId STRING,
  playerName STRING,
  freeWheelFlag BOOLEAN,
  lastGameFlag BOOLEAN,
  rewardResult STRING,
  wheel STRING,
  creator STRING,
  updater STRING,
  updateTime BIGINT,
  createTime BIGINT,
  expireAt BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_game_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE mongo_jackpot_reward_pool_source (
  `_id` String,
  amount BIGINT,
  `type` INT,
  creator STRING,
  createTime BIGINT,
  updater STRING,
  updateTime BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_jackpot_reward_pool',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE mongo_jackpot_reward_pool_input_record_source (
  `_id` BIGINT,
  accFlag INT,
  expireAtTime BIGINT,
  grandAmount BIGINT,
  majorAmount BIGINT,
  miniAmount BIGINT,
  `type` INT,
  minorAmount BIGINT,
  creator STRING,
  createTime BIGINT,
  updater STRING,
  updateTime BIGINT,
  PRIMARY KEY(_id) NOT ENFORCED
) WITH (
  'connector' = 'mongodb',
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_jackpot_reward_pool_input_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_jackpot_user_reward_record',
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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_operation_event_config',
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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_operation_event_config_record',
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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_operation_event_detail',
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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_operation_event',
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
  'uri' = 'mongodb+srv://QATmakiling_Doris_Service:gNIFe0zzbC4U7D24h3L@qat-makiling-pri.mongodb.singapore.nosql.aliyuncs.com/lamang?ssl=false&readPreference=secondary',
  'database' = 'lamang',
  'collection' = 'lamang_star_card_record',
  'scan.startup.mode' = 'initial',
  'scan.ignore-delete.enabled' = 'true'
);

CREATE TEMPORARY TABLE doris_bet_record_sink (
  `_id` STRING,
  betAmount BIGINT,
  betId STRING,
  betPromotionType INT,
  betSerialNo STRING,
  betType STRING,
  betSubType STRING,
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
  credit BIGINT,
  extendInfo STRING,
  updateTime BIGINT,
  creator STRING,
  updater STRING,
  expireAt BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'lamang.lamang_bet_record',
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
  parentId BIGINT,
  batchId BIGINT,
  `index` INT,
  jackpotDrawTimes INT,
  playerId STRING,
  playerName STRING,
  freeWheelFlag BOOLEAN,
  lastGameFlag BOOLEAN,
  rewardResult STRING,
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

CREATE TEMPORARY TABLE doris_jackpot_reward_pool_sink (
  `_id` STRING,
  amount BIGINT,
  `type` INT,
  creator STRING,
  createTime BIGINT,
  updater STRING,
  updateTime BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'lamang.lamang_jackpot_reward_pool',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_jackpot_reward_pool_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

CREATE TEMPORARY TABLE doris_jackpot_reward_pool_input_record_sink (
  `_id` BIGINT,
  accFlag INT,
  expireAtTime BIGINT,
  grandAmount BIGINT,
  majorAmount BIGINT,
  miniAmount BIGINT,
  `type` INT,
  minorAmount BIGINT,
  creator STRING,
  createTime BIGINT,
  updater STRING,
  updateTime BIGINT,
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'lamang.lamang_jackpot_reward_pool_input_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_jackpot_reward_pool_input_record_label',
  'sink.properties.format' = 'json',
  'sink.properties.read_json_by_line' = 'true',
  'sink.max-retries' = '3',
  'sink.properties.timeout' = '600',
  'sink.properties.max_filter_ratio' = '0.1',
  'sink.enable.batch-mode' = 'true',
  'sink.buffer-flush.interval' = '1s'
);

CREATE TEMPORARY TABLE doris_jackpot_user_reward_record_sink (
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
  syncCreateTime BIGINT,
  PRIMARY KEY(`_id`) NOT ENFORCED
) WITH (
  'connector' = 'doris',
  'fenodes' = 'selectdb-sg-l0z4l7mu701.selectdbfe.singapore.rds.aliyuncs.com:8080',
  'table.identifier' = 'lamang.lamang_jackpot_user_reward_record',
  'username' = 'QATmakiling_Doris_Service',
  'password' = 'D180HNf5aXMuhIvt',
  'sink.label-prefix' = 'doris_jackpot_user_reward_record_label',
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
  'table.identifier' = 'lamang.lamang_operation_event_config',
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
  'table.identifier' = 'lamang.lamang_operation_event_config_record',
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
  'table.identifier' = 'lamang.lamang_operation_event_detail',
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
  'table.identifier' = 'lamang.lamang_operation_event',
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
  'table.identifier' = 'lamang.lamang_star_card_record',
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
  betType,
  betSubType,
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
  credit,
  extendInfo,
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
  parentId,
  batchId,
  `index`,
  jackpotDrawTimes,
  playerId,
  playerName,
  freeWheelFlag,
  lastGameFlag,
  rewardResult,
  wheel,
  creator,
  updater,
  updateTime,
  createTime,
  expireAt,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_game_record_source;

INSERT INTO doris_jackpot_reward_pool_sink
SELECT
  `_id`,
  amount,
  `type`,
  creator,
  createTime,
  updater,
  updateTime,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_jackpot_reward_pool_source;

INSERT INTO doris_jackpot_reward_pool_input_record_sink
SELECT
  `_id`,
  accFlag,
  expireAtTime,
  grandAmount,
  majorAmount,
  miniAmount,
  `type`,
  minorAmount,
  creator,
  createTime,
  updater,
  updateTime,
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_jackpot_reward_pool_input_record_source;

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
  UNIX_TIMESTAMP(CURRENT_TIMESTAMP) * 1000 AS syncCreateTime
FROM mongo_jackpot_user_reward_record_source;

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
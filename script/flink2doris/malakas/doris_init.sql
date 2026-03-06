-- doris初始化脚本

-- ============================================
-- Doris 数据库建表脚本
-- 数据库: malakas
-- 包含所有表的创建语句
-- ============================================

-- 使用数据库
USE malakas;

-- ============================================
-- 表1: malakas_bet_record (投注记录表)
-- ============================================
CREATE TABLE IF NOT EXISTS malakas_bet_record (
  `_id` VARCHAR(255) NOT NULL COMMENT '主键ID',
  pId VARCHAR(255) COMMENT '平台id',
  channelId VARCHAR(255) COMMENT '渠道id',
  gameId VARCHAR(255) COMMENT '游戏id',
  userId BIGINT COMMENT '用户id',
  playerId VARCHAR(255) COMMENT '玩家id',
  playerName VARCHAR(255) COMMENT '玩家名称',
  betType VARCHAR(255) COMMENT '投注类型',
  betSubType VARCHAR(255) COMMENT '投注子类型',
  betPromotionType INT COMMENT '投注营销类型',
  betId VARCHAR(255) COMMENT '投注id（单钱包）',
  betSerialNo VARCHAR(255) COMMENT '投注流水号（对应单钱包refId）',
  betAmount BIGINT COMMENT '投注金额 单位：厘',
  payAmount BIGINT COMMENT '支付金额 单位：厘',
  reachMaxMultiple BOOLEAN COMMENT '是否达到最大倍数',
  payOutAmount BIGINT COMMENT '派彩金额 单位：分',
  status INT COMMENT '状态 0进行中 1已完结 2无效',
  betTime DATETIME COMMENT '投注时间',
  creator VARCHAR(255) COMMENT '创建人',
  createTime DATETIME COMMENT '创建时间',
  updater VARCHAR(255) COMMENT '更新人',
  updateTime DATETIME COMMENT '更新时间',
  syncCreateTime DATETIME COMMENT '数据同步写入时间'
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
  -- 副本配置（生产环境建议改为 3）
  "replication_allocation" = "tag.location.default: 1",
  
  -- Schema 变更优化（推荐启用）
  "light_schema_change" = "true",
  
  -- Compaction 配置（推荐启用自动 Compaction）
  "disable_auto_compaction" = "false",
  
  -- 存储配置
  "in_memory" = "false",
  "storage_format" = "V2",
  "enable_unique_key_merge_on_write" = "true"
  
  -- 压缩配置（可选）
  -- "compression" = "LZ4",
  
  -- Bloom Filter（可选，适合等值查询频繁的列）
  -- "bloom_filter_columns" = "playerId,userId,betId"
);

-- ============================================
-- 表2: malakas_game_record (游戏记录表)
-- ============================================
CREATE TABLE IF NOT EXISTS malakas_game_record (
  `_id` VARCHAR(255) NOT NULL COMMENT 'id',
  parentId BIGINT COMMENT 'parentId: 无，则为0',
  batchId BIGINT COMMENT '批次id',
  betRecordId BIGINT COMMENT '投注记录id',
  playerId VARCHAR(255) COMMENT '玩家id',
  playerName VARCHAR(255) COMMENT '玩家名称',
  `index` INT COMMENT '序号',
  freeWheelFlag BOOLEAN COMMENT '免费轮标识 false否 true是',
  wheel TEXT COMMENT '滚轮（JSON格式）',
  rewardResult TEXT COMMENT '中奖结果（JSON格式）',
  gameRecordIds TEXT COMMENT '免费轮id列表（JSON格式）',
  overallRewardAmount BIGINT COMMENT '累计中奖总金额',
  accumulateReachMaxMultiple BOOLEAN COMMENT '达到触发最大中奖倍数',
  lastGameFlag BOOLEAN COMMENT '最后一轮标识',
  syncCreateTime DATETIME COMMENT '数据同步写入时间'
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
  -- 副本配置（生产环境建议改为 3）
  "replication_allocation" = "tag.location.default: 1",
  -- Schema 变更优化（推荐启用）
  "light_schema_change" = "true",
  -- Compaction 配置（推荐启用自动 Compaction）
  "disable_auto_compaction" = "false",
  -- 存储配置
  "in_memory" = "false",
  "storage_format" = "V2",
  "enable_unique_key_merge_on_write" = "true"
  -- 压缩配置（可选）
  -- "compression" = "LZ4",
);

-- ============================================
-- 表3: malakas_operation_event (干预事件表)
-- ============================================
CREATE TABLE IF NOT EXISTS malakas_operation_event (
  `_id` VARCHAR(255) NOT NULL COMMENT '主键ID',
  eventId VARCHAR(255) COMMENT '干预事件ID',
  playerName VARCHAR(255) COMMENT '玩家名称',
  bonus BIGINT COMMENT '干预金额',
  minBonus BIGINT COMMENT '最小干预金额',
  maxBonus BIGINT COMMENT '最大干预金额',
  finishBonus BIGINT COMMENT '已完成干预金额',
  eventKind INT COMMENT '干预事件类型 1-正常投注紧急干预;2-正常投注普通干预;3-免费游戏紧急干预;4-免费游戏普通干预',
  gameId VARCHAR(255) COMMENT '游戏ID',
  channelId VARCHAR(255) COMMENT '渠道ID',
  effectedTime DATETIME COMMENT '生效时间',
  expiredTime DATETIME COMMENT '过期时间',
  eventStatus INT COMMENT '干预状态 0-可干预;1-干预中(锁定状态);2-干预完成',
  eventSubStatus INT COMMENT '干预子状态 0-处理中;1-干预完成(正常);2-干预完成(事件超时);3-干预完成(投注金额不符,有干预明细);4-干预完成(投注金额不符,无干预明细)',
  reportStatus INT COMMENT '上报状态 0-未上报(默认);1-上报成功;2-上报失败',
  eventMsgDetail TEXT COMMENT '干预事件消息快照',
  reportFailMsg TEXT COMMENT 'reportFailMsg',
  reportTime DATETIME COMMENT '上报时间',
  createTime DATETIME COMMENT '创建时间',
  updateTime DATETIME COMMENT '更新时间',
  syncCreateTime DATETIME COMMENT '数据同步写入时间'
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
  -- 副本配置（生产环境建议改为 3）
  "replication_allocation" = "tag.location.default: 1",
  
  -- Schema 变更优化（推荐启用）
  "light_schema_change" = "true",
  
  -- Compaction 配置（推荐启用自动 Compaction）
  "disable_auto_compaction" = "false",
  
  -- 存储配置
  "in_memory" = "false",
  "storage_format" = "V2",
  "enable_unique_key_merge_on_write" = "true"
  
  -- 压缩配置（可选）
  -- "compression" = "LZ4",
);

-- ============================================
-- 表4: malakas_operation_event_config (干预事件配置表)
-- ============================================
CREATE TABLE IF NOT EXISTS malakas_operation_event_config (
  `_id` VARCHAR(255) NOT NULL COMMENT '主键ID',
  gameId VARCHAR(255) COMMENT '游戏ID',
  betType VARCHAR(255) COMMENT '投注类型',
  multiple DOUBLE COMMENT '倍数',
  syncCreateTime DATETIME COMMENT '数据同步写入时间'
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
  -- 副本配置（生产环境建议改为 3）
  "replication_allocation" = "tag.location.default: 1",
  
  -- Schema 变更优化（推荐启用）
  "light_schema_change" = "true",
  
  -- Compaction 配置（推荐启用自动 Compaction）
  "disable_auto_compaction" = "false",
  
  -- 存储配置
  "in_memory" = "false",
  "storage_format" = "V2",
  "enable_unique_key_merge_on_write" = "true"
  
  -- 压缩配置（可选）
  -- "compression" = "LZ4",
);

-- ============================================
-- 表5: malakas_operation_event_config_record (干预事件配置记录表)
-- ============================================
CREATE TABLE IF NOT EXISTS malakas_operation_event_config_record (
  `_id` VARCHAR(255) NOT NULL COMMENT '主键ID',
  parentId BIGINT COMMENT 'parentId',
  batchId BIGINT COMMENT 'batchId',
  betRecordId BIGINT COMMENT 'betRecordId',
  `index` INT COMMENT '序号',
  gameRecordIds TEXT COMMENT '免费轮（JSON格式）',
  wheel TEXT COMMENT '滚轮（JSON格式）',
  extInfo TEXT COMMENT '扩展属性，每个游戏根据实际需要定义值（JSON格式）',
  syncCreateTime DATETIME COMMENT '数据同步写入时间'
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
  -- 副本配置（生产环境建议改为 3）
  "replication_allocation" = "tag.location.default: 1",
  
  -- Schema 变更优化（推荐启用）
  "light_schema_change" = "true",
  
  -- Compaction 配置（推荐启用自动 Compaction）
  "disable_auto_compaction" = "false",
  
  -- 存储配置
  "in_memory" = "false",
  "storage_format" = "V2",
  "enable_unique_key_merge_on_write" = "true"
  
  -- 压缩配置（可选）
  -- "compression" = "LZ4",
);

-- ============================================
-- 表7: malakas_star_card_record (星卡使用记录表)
-- ============================================
CREATE TABLE IF NOT EXISTS malakas_star_card_record (
  `_id` VARCHAR(255) NOT NULL COMMENT '主键ID',
  rewardId VARCHAR(255) COMMENT '奖励ID',
  rewardConfigId VARCHAR(255) COMMENT '奖励配置id',
  gameId VARCHAR(255) COMMENT '游戏ID',
  channelId VARCHAR(255) COMMENT '渠道ID',
  betRecordId BIGINT COMMENT '投注记录ID',
  playerId VARCHAR(255) COMMENT '玩家ID',
  playName VARCHAR(255) COMMENT '玩家名称',
  betAmount BIGINT COMMENT '投注金额',
  payOutAmount BIGINT COMMENT '派奖金额',
  useStatus INT COMMENT '使用状态',
  failMsg TEXT COMMENT '核销失败原因',
  failCode INT COMMENT 'failCode',
  reportStatus INT COMMENT '上报状态',
  createTime DATETIME COMMENT '创建时间',
  updateTime DATETIME COMMENT '更新时间',
  syncCreateTime DATETIME COMMENT '数据同步写入时间'
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
  -- 副本配置（生产环境建议改为 3）
  "replication_allocation" = "tag.location.default: 1",
  
  -- Schema 变更优化（推荐启用）
  "light_schema_change" = "true",
  
  -- Compaction 配置（推荐启用自动 Compaction）
  "disable_auto_compaction" = "false",
  
  -- 存储配置
  "in_memory" = "false",
  "storage_format" = "V2",
  "enable_unique_key_merge_on_write" = "true"
  
  -- 压缩配置（可选）
  -- "compression" = "LZ4",
);

-- ============================================
-- 表6: malakas_operation_event_detail (干预事件明细表)
-- ============================================
CREATE TABLE IF NOT EXISTS malakas_operation_event_detail (
  `_id` VARCHAR(255) NOT NULL COMMENT '主键ID',
  operatorRecordId BIGINT COMMENT '干预事件ID(系统)',
  eventId VARCHAR(255) COMMENT '干预事件ID',
  operatorGameId VARCHAR(255) COMMENT '游戏ID',
  configId BIGINT COMMENT '配置ID,命中那条倍数',
  playerId VARCHAR(255) COMMENT '玩家ID(三方)',
  playerName VARCHAR(255) COMMENT '玩家名称(三方)',
  userId VARCHAR(255) COMMENT '玩家ID',
  betAmount BIGINT COMMENT '投注金额',
  payOutAmount BIGINT COMMENT '派奖金额',
  eventAmount BIGINT COMMENT '事件干预金额',
  payOutTime DATETIME COMMENT '派奖时间',
  betTime DATETIME COMMENT '下注时间',
  betId VARCHAR(255) COMMENT '投注id（单钱包）',
  betRecordId BIGINT COMMENT '投注记录Id',
  betStatus INT COMMENT '订单状态',
  channelId VARCHAR(255) COMMENT '渠道ID',
  gameId VARCHAR(255) COMMENT '游戏ID',
  createTime DATETIME COMMENT '创建时间',
  updateTime DATETIME COMMENT '更新时间',
  syncCreateTime DATETIME COMMENT '数据同步写入时间'
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
  -- 副本配置（生产环境建议改为 3）
  "replication_allocation" = "tag.location.default: 1",
  
  -- Schema 变更优化（推荐启用）
  "light_schema_change" = "true",
  
  -- Compaction 配置（推荐启用自动 Compaction）
  "disable_auto_compaction" = "false",
  
  -- 存储配置
  "in_memory" = "false",
  "storage_format" = "V2",
  "enable_unique_key_merge_on_write" = "true"
  
  -- 压缩配置（可选）
  -- "compression" = "LZ4",
);

-- ============================================
-- malakas_bet_record 表索引
-- 参考 MongoDB 索引：
-- 1. {playerId: 1, channelId: 1, status: 1, createTime: -1} - C端查询
-- 2. {createTime: -1, status: 1, _id: 1} - 通用查询
-- 3. {createTime: -1, channelId: 1, status:1, payAmount:1, payOutAmount:1 ,userId:1, betPromotionType:1} - B端查询覆盖索引
-- ============================================
ALTER TABLE malakas_bet_record ADD INDEX idx_player_channel_status_createtime (playerId, channelId, status, createTime);
ALTER TABLE malakas_bet_record ADD INDEX idx_createtime_status_id (createTime, status, `_id`);
ALTER TABLE malakas_bet_record ADD INDEX idx_createtime_channel_status_pay_user_promotion (createTime, channelId, status, payAmount, payOutAmount, userId, betPromotionType);

-- ============================================
-- malakas_game_record 表索引
-- 参考 MongoDB 索引：
-- 1. {betRecordId: 1, batchId: 1} - C端查询
-- ============================================
ALTER TABLE malakas_game_record ADD INDEX idx_bet_record_batch (betRecordId, batchId);

-- ============================================
-- malakas_operation_event 表索引
-- 参考 MongoDB 索引：
-- 1. {createTime: -1, channelId: 1}
-- 2. {updateTime: 1, reportStatus: 1}
-- 3. {updateTime: 1, eventStatus: 1}
-- 4. {playerName: 1, effectedTime: 1, expiredTime: 1, eventStatus: 1}
-- 5. {eventId: 1}, {unique: true}
-- 6. {expiredTime: 1, reportStatus: 1}
-- 7. {eventStatus:1, reportStatus:1, _id:1 , updateTime : -1}
-- ============================================
ALTER TABLE malakas_operation_event ADD INDEX idx_createtime_channel (createTime, channelId);
ALTER TABLE malakas_operation_event ADD INDEX idx_updatetime_report_status (updateTime, reportStatus);
ALTER TABLE malakas_operation_event ADD INDEX idx_updatetime_event_status (updateTime, eventStatus);
ALTER TABLE malakas_operation_event ADD INDEX idx_player_effected_expired_status (playerName, effectedTime, expiredTime, eventStatus);
ALTER TABLE malakas_operation_event ADD INDEX idx_event_id (eventId);
ALTER TABLE malakas_operation_event ADD INDEX idx_expired_report_status (expiredTime, reportStatus);
ALTER TABLE malakas_operation_event ADD INDEX idx_status_report_id_updatetime (eventStatus, reportStatus, `_id`, updateTime);

-- ============================================
-- malakas_operation_event_config 表索引
-- 参考 MongoDB 索引：
-- 1. {multiple: 1}
-- ============================================
ALTER TABLE malakas_operation_event_config ADD INDEX idx_multiple (multiple);

-- ============================================
-- malakas_operation_event_config_record 表索引
-- 参考 MongoDB 索引：
-- 1. {betRecordId: 1}
-- ============================================
ALTER TABLE malakas_operation_event_config_record ADD INDEX idx_bet_record_id (betRecordId);

-- ============================================
-- malakas_operation_event_detail 表索引
-- 参考 MongoDB 索引：
-- 1. {createTime: 1}
-- 2. {operatorRecordId: 1}
-- 3. {betStatus: 1, _id: 1, createTime: -1}
-- ============================================
ALTER TABLE malakas_operation_event_detail ADD INDEX idx_create_time (createTime);
ALTER TABLE malakas_operation_event_detail ADD INDEX idx_operator_record_id (operatorRecordId);
ALTER TABLE malakas_operation_event_detail ADD INDEX idx_bet_status_id_createtime (betStatus, `_id`, createTime);

-- ============================================
-- malakas_star_card_record 表索引
-- 参考 MongoDB 索引：
-- 1. {createTime:-1, channelId: 1}
-- 2. {reportStatus: 1}
-- ============================================
ALTER TABLE malakas_star_card_record ADD INDEX idx_createtime_channel (createTime, channelId);
ALTER TABLE malakas_star_card_record ADD INDEX idx_report_status (reportStatus);

-- ============================================
-- 说明：
-- 1. 本脚本包含 malakas 数据库下所有表的建表语句
-- 2. 当前包含的表：
--    - malakas_bet_record (投注记录表)
--    - malakas_game_record (游戏记录表)
--    - malakas_operation_event (干预事件表)
--    - malakas_operation_event_config (干预事件配置表)
--    - malakas_operation_event_config_record (干预事件配置记录表)
--    - malakas_operation_event_detail (干预事件明细表)
--    - malakas_star_card_record (星卡使用记录表)
-- 3. 所有表使用相同的 PROPERTIES 配置
-- 4. 根据实际数据量和查询需求调整 BUCKETS 数量
-- 5. 根据业务查询需求创建相应的索引
-- 6. 生产环境建议将 replication_allocation 改为 3 副本
-- ============================================


    
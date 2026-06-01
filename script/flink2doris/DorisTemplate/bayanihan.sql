-- =======================================
-- DATABASE: bayanihan
-- Mongo 投注集合存 BetRecordPo（BetRecordService 经 BetRecordConverter.doToPo 写入）；
-- 游戏记录集合存 BayanihanGameRecordDo；星卡集合存 StarCardUseRecordPo。
-- 干预相关集合与 OperationEvent*Po 对齐。另含 syncCreateTime 同步元数据。
-- 说明：BetRecordDo.gameRecordList 不落库到 bet_record 文档，由 bayanihan_game_record 承载。
-- =======================================

-- Table: bayanihan_bet_record（BetRecordPo + syncCreateTime）
CREATE TABLE `bayanihan_bet_record` (
  `_id` varchar(255) NOT NULL COMMENT "主键ID",
  `parentId` bigint NULL COMMENT "parentID",
  `pId` varchar(255) NULL COMMENT "平台id",
  `channelId` varchar(255) NULL COMMENT "渠道id",
  `gameId` varchar(255) NULL COMMENT "游戏id",
  `userId` bigint NULL COMMENT "用户id",
  `playerId` varchar(255) NULL COMMENT "玩家id",
  `playerName` varchar(255) NULL COMMENT "玩家名称",
  `betType` varchar(255) NULL COMMENT "投注类型",
  `betSubType` varchar(255) NULL COMMENT "投注子类型",
  `betPromotionType` int NULL COMMENT "投注营销类型",
  `bizInterfereType` int NULL COMMENT "业务干预类型",
  `bizInterfereSubType` int NULL COMMENT "业务干预子类型",
  `betId` varchar(255) NULL COMMENT "投注id（单钱包）",
  `betSerialNo` varchar(255) NULL COMMENT "投注流水号（对应单钱包refId）",
  `betAmount` bigint NULL COMMENT "投注金额 单位：分",
  `payAmount` bigint NULL COMMENT "支付金额 单位：分",
  `reachMaxMultiple` boolean NULL COMMENT "是否达到最大倍数",
  `payOutAmount` bigint NULL COMMENT "派彩金额 单位：分",
  `marketFlag` int NULL COMMENT "营销标识 0否 1是（BetRecordPo，当前转换器未赋值时多为空）",
  `status` int NULL COMMENT "状态 0进行中 1已完结 2无效",
  `betTime` bigint NULL COMMENT "投注时间",
  `jackpot` text NULL COMMENT "jackpot（嵌套对象 JSON）",
  `inventory` text NULL COMMENT "库存信息（BetRecordDo.Inventory JSON）",
  `extendInfo` text NULL COMMENT "扩展信息（BetRecordDo.ExtendInfo JSON）",
  `creator` varchar(255) NULL COMMENT "创建人",
  `createTime` bigint NULL COMMENT "创建时间",
  `updater` varchar(255) NULL COMMENT "更新人",
  `updateTime` bigint NULL COMMENT "更新时间",
  `expireAt` bigint NULL COMMENT "过期时间",
  `freeWheelHandleStatus` varchar(255) NULL COMMENT "免费轮处理状态",
  `credit` bigint NULL COMMENT "投注后用户余额（厘）",
  `templateType` varchar(255) NULL COMMENT "模板类型",
  `nextTemplateId` bigint NULL COMMENT "下一局模板id",
  `freeGameInfo` text NULL COMMENT "免费轮汇总信息（BetRecordDo.FreeGameInfo JSON）",
  `syncCreateTime` bigint NULL COMMENT "数据同步写入时间"
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
"file_cache_ttl_seconds" = "0",
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

;

-- Table: bayanihan_game_record（BayanihanGameRecordDo + syncCreateTime）
CREATE TABLE `bayanihan_game_record` (
  `_id` varchar(255) NOT NULL COMMENT "id",
  `parentId` bigint NULL COMMENT "parentId: 无，则为0",
  `batchId` bigint NULL COMMENT "批次id",
  `betRecordId` bigint NULL COMMENT "投注记录id",
  `playerId` varchar(255) NULL COMMENT "玩家id",
  `playerName` varchar(255) NULL COMMENT "玩家名称",
  `index` int NULL COMMENT "序号",
  `freeWheelFlag` boolean NULL COMMENT "免费轮标识 false否 true是",
  `wheel` text NULL COMMENT "滚轮（JSON）",
  `rewardResult` text NULL COMMENT "中奖结果（JSON）",
  `gameRecordIds` text NULL COMMENT "免费轮游戏记录id列表（JSON）",
  `lastGameFlag` boolean NULL COMMENT "最后一轮标识",
  `collectionInfo` text NULL COMMENT "收集玩法快照（BayanihanGameRecordDo.CollectionSnapshot JSON）",
  `creator` varchar(255) NULL COMMENT "创建人",
  `createTime` bigint NULL COMMENT "创建时间",
  `updater` varchar(255) NULL COMMENT "更新人",
  `updateTime` bigint NULL COMMENT "更新时间",
  `expireAt` bigint NULL COMMENT "过期时间",
  `syncCreateTime` bigint NULL COMMENT "数据同步写入时间"
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
"file_cache_ttl_seconds" = "0",
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

;

-- Table: bayanihan_operation_event（OperationEventPo + syncCreateTime）
CREATE TABLE `bayanihan_operation_event` (
  `_id` varchar(255) NOT NULL COMMENT "主键ID",
  `eventId` varchar(255) NULL COMMENT "干预事件ID",
  `playerName` varchar(255) NULL COMMENT "玩家名称",
  `bonus` bigint NULL COMMENT "干预金额",
  `minBonus` bigint NULL COMMENT "最小干预金额",
  `maxBonus` bigint NULL COMMENT "最大干预金额",
  `finishBonus` bigint NULL COMMENT "已完成干预金额",
  `eventKind` int NULL COMMENT "干预事件类型",
  `gameId` varchar(255) NULL COMMENT "游戏ID",
  `channelId` varchar(255) NULL COMMENT "渠道ID",
  `effectedTime` bigint NULL COMMENT "生效时间",
  `expiredTime` bigint NULL COMMENT "过期时间",
  `eventStatus` int NULL COMMENT "干预状态",
  `eventSubStatus` int NULL COMMENT "干预子状态",
  `reportStatus` int NULL COMMENT "上报状态",
  `eventMsgDetail` text NULL COMMENT "干预事件消息快照",
  `reportFailMsg` text NULL COMMENT "reportFailMsg",
  `reportTime` bigint NULL COMMENT "上报时间",
  `createTime` bigint NULL COMMENT "创建时间",
  `updateTime` bigint NULL COMMENT "更新时间",
  `expireAt` bigint NULL COMMENT "过期时间",
  `syncCreateTime` bigint NULL COMMENT "数据同步写入时间"
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
"file_cache_ttl_seconds" = "0",
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

;

-- Table: bayanihan_operation_event_config（OperationEventConfigPo + syncCreateTime）
CREATE TABLE `bayanihan_operation_event_config` (
  `_id` varchar(255) NOT NULL COMMENT "主键ID",
  `gameId` varchar(255) NULL COMMENT "游戏ID",
  `betType` varchar(255) NULL COMMENT "投注类型",
  `multiple` double NULL COMMENT "倍数",
  `syncCreateTime` bigint NULL COMMENT "数据同步写入时间"
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
"file_cache_ttl_seconds" = "0",
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

;

-- Table: bayanihan_operation_event_config_record（OperationEventConfigRecordPo + syncCreateTime）
CREATE TABLE `bayanihan_operation_event_config_record` (
  `_id` varchar(255) NOT NULL COMMENT "主键ID",
  `parentId` bigint NULL COMMENT "parentId",
  `batchId` bigint NULL COMMENT "batchId",
  `betRecordId` bigint NULL COMMENT "betRecordId",
  `index` int NULL COMMENT "序号",
  `gameRecordIds` text NULL COMMENT "免费轮（JSON）",
  `wheel` text NULL COMMENT "滚轮（JSON）",
  `extInfo` text NULL COMMENT "扩展属性（JSON）",
  `syncCreateTime` bigint NULL COMMENT "数据同步写入时间"
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
"file_cache_ttl_seconds" = "0",
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

;

-- Table: bayanihan_operation_event_detail（OperationEventDetailPo + syncCreateTime）
CREATE TABLE `bayanihan_operation_event_detail` (
  `_id` varchar(255) NOT NULL COMMENT "主键ID",
  `operatorRecordId` bigint NULL COMMENT "干预事件ID(系统)",
  `eventId` varchar(255) NULL COMMENT "干预事件ID",
  `operatorGameId` varchar(255) NULL COMMENT "游戏ID",
  `configId` bigint NULL COMMENT "配置ID,命中那条倍数",
  `playerId` varchar(255) NULL COMMENT "玩家ID(三方)",
  `playerName` varchar(255) NULL COMMENT "玩家名称(三方)",
  `userId` varchar(255) NULL COMMENT "玩家ID",
  `betAmount` bigint NULL COMMENT "投注金额",
  `payOutAmount` bigint NULL COMMENT "派奖金额",
  `eventAmount` bigint NULL COMMENT "事件干预金额",
  `payOutTime` bigint NULL COMMENT "派奖时间",
  `betTime` bigint NULL COMMENT "下注时间",
  `betId` varchar(255) NULL COMMENT "投注id（单钱包）",
  `betRecordId` bigint NULL COMMENT "投注记录Id",
  `betStatus` int NULL COMMENT "订单状态",
  `channelId` varchar(255) NULL COMMENT "渠道ID",
  `gameId` varchar(255) NULL COMMENT "游戏ID",
  `createTime` bigint NULL COMMENT "创建时间",
  `updateTime` bigint NULL COMMENT "更新时间",
  `expireAt` bigint NULL COMMENT "过期时间",
  `syncCreateTime` bigint NULL COMMENT "数据同步写入时间"
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
"file_cache_ttl_seconds" = "0",
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

;

-- Table: bayanihan_star_card_record（StarCardUseRecordPo + syncCreateTime）
CREATE TABLE `bayanihan_star_card_record` (
  `_id` varchar(255) NOT NULL COMMENT "主键ID",
  `pId` varchar(255) NULL COMMENT "平台ID",
  `rewardId` varchar(255) NULL COMMENT "奖励ID",
  `rewardConfigId` varchar(255) NULL COMMENT "奖励配置id",
  `gameId` varchar(255) NULL COMMENT "游戏ID",
  `channelId` varchar(255) NULL COMMENT "渠道ID",
  `betRecordId` bigint NULL COMMENT "投注记录ID",
  `playerId` varchar(255) NULL COMMENT "玩家ID",
  `playName` varchar(255) NULL COMMENT "玩家名称",
  `betAmount` bigint NULL COMMENT "投注金额",
  `payOutAmount` bigint NULL COMMENT "派奖金额",
  `useStatus` int NULL COMMENT "使用状态",
  `failMsg` text NULL COMMENT "核销失败原因",
  `failCode` int NULL COMMENT "failCode",
  `reportStatus` int NULL COMMENT "上报状态",
  `createTime` bigint NULL COMMENT "创建时间",
  `updateTime` bigint NULL COMMENT "更新时间",
  `expireAt` bigint NULL COMMENT "过期时间",
  `syncCreateTime` bigint NULL COMMENT "数据同步写入时间"
) ENGINE=OLAP
UNIQUE KEY(`_id`)
DISTRIBUTED BY HASH(`_id`) BUCKETS 10
PROPERTIES (
"file_cache_ttl_seconds" = "0",
"is_being_synced" = "false",
"storage_medium" = "hdd",
"storage_format" = "V2",
"inverted_index_storage_format" = "V2",
"enable_unique_key_merge_on_write" = "true",
"light_schema_change" = "true",
"disable_auto_compaction" = "false",
"enable_single_replica_compaction" = "false",
"group_commit_interval_ms" = "10000",
"group_commit_data_bytes" = "134217728",
"enable_mow_light_delete" = "false"
);

;

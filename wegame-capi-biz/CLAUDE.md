# wegame-capi-biz — 核心业务模块

承载几乎全部业务逻辑。分析代码 90% 时间在这里。先读根 `../CLAUDE.md` 了解全局，本文件只展开 biz 内部结构。

包根：`com.wegame.capi.biz`

## 一、目录速览（`src/main/java/com/wegame/capi/biz/`）

| 目录 | 内容 |
|------|------|
| `module/base/` | **公共抽象**：`bet/AbstractBetContext`、`odds`、`reward`、`multiple`、`game`、`jackpot`、`intervention/AbstractNodeComponent` + `component/*`（11 个干预组件）、`AmountHelper`/`IdHelper`/`RewardHelper` |
| `module/<game>/` | **每游戏实现**（11 个）：`bet/ game/ odds/ reward/ multiple/` + `<Game>GameHelper`/`SymbolUtil`/`ProbabilisticUtil`。`game/` 下每游戏一般有 `<Game>GameNormalPlay`（实时抽符号）+ `<Game>GameTemplatePlay`（查模板，**线上走这个**）+ `<Game>GameEventPlay` 等多个 Play 类，改玩法来这里 |
| `service/` | `BetService`(路由) `BetRecordService` `BetStatusService` `RandomService`(抽桶) `RewardService` `GameRecordService` `LoginService` `UserService` `SingleWallet*`；子域：`intervention/ operation/ jackpot/ promotion/ bloodpool/ freespin/ inventory/ metric/ template/ backend/` |
| `manager/` | `operation/AbstractOperationEventManager`（**DOS 核心**）、`jackpot/` |
| `listener/` | Pulsar 消费者：`betRecord/<Game>PulsarListener`、`operation/<Game>OperationEventPulsarListener`、`jackpot/`、`handler/`（启动加载）；`PulsarProducer` 回发；`AppLaunchPostProcessor` |
| `config/` | `nacos/`（`NacosConfigRefresherUtil` 配置热加载映射）、`mongo/<Game>MongoConfig`、`pulsar/`（含 `operation/` 4 路）、`es/`、`domain/`（**每游戏 Config + `GameBizConfig` + `common/CommonTemplateConfig`**）、`processor/` |
| `converter/` | DO↔VO 转换（promotion/operation/user/backend/jackpot） |
| `builder/` `domain/` `filter/` `interceptor/` | 构造器、biz 领域对象（`UserMetricInfoDo` 等）、过滤器、拦截器 |

> 注：`io.lettuce.core.support`、`com.alibaba.cloud.nacos.refresh` 下有少量本地覆盖类（框架增强），非业务。

## 二、投注主链路（跟读顺序）

1. `service/BetService.bet(BetDo)` — 按 `GameTypeEnum` 从 `contextNameMap` 取 bean 名，`contextMap` 拿到 `<Game>BetContext`；`buildGamePlayType` 决定模式：**prod 恒 `TEMPLATE_PLAY`**，非 prod 默认 `NORMAL_PLAY`（带 `at` 标记也可 template）。
2. `module/<game>/bet/<Game>BetContext`（继承 `module/base/bet/AbstractBetContext`）— 编排：出盘面 → 算赔率 → 派彩 → 落库 →（运营事件/DOS）明细更新+上报+冷却。
3. **出盘面分两条线**（`GamePlayTypeEnum`，与游戏正交，几乎每游戏都实现两套）：
   - **TEMPLATE_PLAY（线上）**：`module/base/game/AbstractGameTemplatePlay.getTemplate()`（每游戏 `<Game>GameTemplatePlay` 覆写）：
     ```
     选 RTP 档  getRtpWithBloodPoolControl(): 血池档(poolCategory+level) → CommonTemplateConfig.getRtp(); 异常兜底
              或 getDefaultRtp(): betPromotionType → rtpControlSubKey 选默认档
     选倍率桶  getTemplateMultipleRange() = randomKeyByWeight(rtp.multipleRangeWeightMap[betType])   → "1_3" 类区间
     捞盘面    betRecordTemplateService.listIdByMultipleRange(indexName, betType, start, end) → 随机取 id
              → warmUp 命中走本地缓存(getTemplateWithLocalCache)，否则 ES findById
     还原      templateGame().templatePlay(betDo, gamePlay, template)
     ```
     决策收敛点 = 「选 RTP 档 + 选倍率桶」；这就是对接 rtp中台 `getMultiplier` 的替换缝。
   - **NORMAL_PLAY（非 prod）**：`<Game>GameNormalPlay.play()` 现场 `randomService` 掷符号（modeWeights/symbolWeights 或 probabilisticConfig）→ `RewardHandler` 算奖，无"选桶"。
4. 抽桶/抽符号：`service/RandomService.randomKeyByWeight`（权重轮盘，模式/符号/倍率区间通用）。
5. 赔率：`module/base/odds/AbstractOddsCalculateStrategy.calculateOdds`（查 `oddsConfig.commonOddsConfig`）。

## 三、DOS 点控（OperationEvent）关键类

- 下发消费：`listener/operation/AbstractOperationEventPulsarListener` → `OperationEventMessageDo` → Mongo `operation_event_<GAME>`（`eventId` 幂等）。
- 命中执行：`manager/operation/AbstractOperationEventManager`
  - `getEffectedEvent` 查生效事件 → `getInterventionAmount`（needed=eventBonus−finishBonus，受 maxBonus 约束）→ `getWheelId`（`multiple=(intervention+pay)/pay` → `MultipleUtil.findClosestMultiple` → `Pair<wheelId, configMultiple>`）。
  - 结算：`asyncUpdateEventDetail`（`finishBonus+=eventAmount`，≥minBonus→FINISH）→ `asyncReportEventById`/`reportEvent`（分布式锁 + `UpdateEventsStatusReq`）。
- 冷却：`common/enums/DosGapRuleEnum`（按 `configMultiple` 定间隔局数，存 Redis `wegame:dos:gap:remaining:*`）。
- 判定入口：`service/operation/*`（`isCanOperationEventBet` 白名单+间隔期判定，`onBetComplete`/`onDosSuccess` 冷却写入）。

## 四、玩家干预（LiteFlow）

- 规则源：`../wegame-capi-application/src/main/resources/json/common_intervention_rules.xml`（生产环境从 Nacos 读同名 dataId），两条链 `<gameId>_preIntervention` / `<gameId>_postIntervention`。
- 组件：`module/base/intervention/component/*`（newUser/retentionUser/returnUserHelp/newUserHelp/retentionUserHelp/rtpHigh/rtpLow/fg/fgLimit/newUserEmpty/normalEmpty），均继承 `AbstractNodeComponent`。
- 判定四步：`triggerCondition()`（读 `UserMetricInfoDo`）→ `triggerProbability()`（万分位随机）→ `repeatCondition()`（always/today_not/never_repeat）→ `intervention()`（产出 `PlayerInterventionResultDo`：rtpName + backstopRtpName + templateMultipleRange）。
- 执行入口：`service/intervention/PlayerInterventionService`（`FlowExecutor.execute2Resp`）。
- ⚠️ 个人 RTP 只有 `UserMetricInfoDo.getRtp()` = 累计 payout/bet 阈值（rtpHigh/rtpLow 两条规则），**无滚动窗口 / 无 recovery 状态机**。

## 五、配置热加载

`config/nacos/NacosConfigRefresherUtil` 维护三张映射：dataId→Config类、dataId→JSON根名、GameType→dataId。Nacos 变更 → 校验（提取根、反序列化、`config.validate()`）→ 发 `RefreshEvent` 刷新 `@RefreshScope`。兜底：`common/utils/FileUtil.readClasspathFile` 读 `resources/json/*.json`。

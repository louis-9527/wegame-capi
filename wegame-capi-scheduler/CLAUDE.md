# wegame-capi-scheduler — 定时任务模块

先读根 `../CLAUDE.md`。本模块是**每游戏一组定时任务** + 基础设施监控。依赖 `wegame-capi-biz`（任务体只调 biz 的 service/manager，逻辑不在这里重写）。

包根：`com.wegame.capi.scheduler`

## 一、核心模式：Abstract 模板 + 每游戏子类

- `AbstractScheduler`（顶层基类）→ `biz/common/Abstract*Scheduler`（每类任务一个抽象模板，写通用逻辑）→ `biz/<game>/<Game>*Scheduler`（具体子类，绑定游戏类型 + `@Scheduled` cron）。
- **改任务逻辑改 `biz/common/Abstract*`；某游戏要不要跑、跑的频率看 `biz/<game>/` 子类。** 一个游戏没有某抽象对应的子类 = 该游戏不跑那个任务。

## 二、任务类型（`biz/common/` 的抽象模板）

| 抽象模板 | 职责 |
|----------|------|
| `AbstractBetRecordBetStatusScheduler` | 扫描 bet 结算状态（未结算/超时补偿） |
| `AbstractBetRecordBetStatusAlertScheduler` | bet 状态异常告警（Lark 通知） |
| `AbstractBetRecordFailedBetScheduler` | 失败投注处理（仅 mangkukulam 挂了子类） |
| `AbstractOperationEventScheduler` | **DOS 运营事件**兜底：扫未完成/未上报事件，重试上报 |
| `AbstractMaintainCacheUpdateScheduler` | 刷新游戏维护配置缓存 |
| `AbstractStarCardScheduler` | 星卡过期/结算 |
| `AbstractBloodPoolConfigScheduler` | 血池配置刷新（仅 diwatamayari 挂了子类） |
| `AbstractContinuePlayScheduler` | 连续游戏处理（仅 mangkukulam） |
| `AbstractJackpotRewardGrantScheduler` / `...GrantAlertScheduler` | 奖池发奖 + 告警（仅 lamang） |
| `AbstractJackpotRewardPoolAccScheduler` | 奖池累积 |
| `AbstractJackpotRewardPoolInputRecordDeleteScheduler` | 奖池注水记录清理 |

## 三、每游戏子类覆盖情况（`biz/<game>/`）

- 通用四件套几乎每游戏都有：`BetRecordBetStatus` + `BetRecordBetStatusAlert` + `MaintainCacheUpdate` + `OperationEvent`。
- 差异点：多数游戏有 `StarCard`；**lamang** 独有整套 jackpot 任务（发奖/累积/清理）；**mangkukulam** 独有 `FailedBet` + `ContinuePlay`；**diwatamayari** 独有 `BloodPoolConfig`。
- 目录：atlantis / bakunawa / bayanihan / diwatamayari / ibong / kapre / lamang / malakas / mangkukulam / mariamakiling / sirena。

## 四、基础设施监控 — `monitor/`

不分游戏，监控运行时资源（多配合 JMX/Prometheus + Lark 告警）：
- `DataSourceMonitorScheduler` — 数据源连接池
- `RestTemplateConnectionPoolMonitorScheduler` / `RestTemplateMinConnectionPoolKeepScheduler` — 外部 HTTP 连接池监控与最小连接保活
- `ThreadPoolMonitorScheduler` — 线程池

## 五、注意

- 调度开关由启动类 `@EnableScheduling` 打开；分布式部署下任务需靠分布式锁避免多 pod 重复执行（锁与实际业务在 biz 侧）。
- 新增某游戏的定时任务：在 `biz/<game>/` 加一个继承对应 `biz/common/Abstract*Scheduler` 的子类即可，不要在本模块写业务逻辑。

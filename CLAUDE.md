# wegame-capi — 项目代码结构与分析指引

游戏方**服务端**（Client API），承载 11 款老虎机的投注、派彩、运营干预（点控/DOS）、奖池、促销等。Spring Boot + Maven 多模块单体。本文件用于**下次快速定位代码**，不是需求文档。

> 深度调研（架构 / DOS 点控 / 统一赔率映射，对接 rtp中台）见飞书：
> https://casinoplus.sg.larksuite.com/wiki/DKmZw4zpWi8Zs7k3FYAlrzyhgke

---

## 一、这是什么 / 一句话心智模型

- 玩家投注 → `BetController` → `BetService` 按 `GameTypeEnum` 路由到**每游戏的 `<Game>BetContext`** →（线上）**选 RTP 档 + 选倍率桶 → 按桶取预生成模板** → 还原盘面算派彩 → 存 Mongo + 发 Pulsar + 更新 Redis。血池/促销/DOS点控/LiteFlow干预都收敛在「选档+选桶」这一步（详见「五」）。
- **每游戏一套实现**，共享 `module/base/` 抽象（base + override 模式）。改一个游戏的玩法，去 `wegame-capi-biz/module/<game>/`。
- **配置不在 DB**：每游戏赔率/权重是一份 JSON，存 **Nacos**（热加载），classpath JSON 兜底。

## 二、模块地图（依赖自底向上）

| 模块 | 职责 | 分析时看这里找… |
|------|------|----------------|
| `wegame-capi-client` | 对外契约：req/res DTO、枚举（`GameTypeEnum`/`BetTypeEnum`/`GamePlayTypeEnum`）、`UserInfoContext` | 接口入参出参、游戏枚举 |
| `wegame-capi-common` | 常量、工具、通用枚举（`DosGapRuleEnum`、`InterventionRuleIdConstants`） | 全局常量、DOS 冷却规则 |
| `wegame-capi-infrastructure` | Redis 各类 Cache、REST 客户端连接池（Wallet/Promotion/FreeSpin）、AOP、监控、配置属性 | 缓存 key、外部 HTTP 调用 |
| `wegame-capi-dao` | MyBatis Mapper（MySQL: user/login/backend）+ MongoDB PO（`BetRecordPo`、`OperationEventPo`、`Jackpot*`…） | 表结构、Mongo 文档结构 |
| `wegame-capi-api` | API 层领域模型（req/res：promotion/platform/jackpot/operation/singlewallet） | 回调/上报报文（如 `UpdateEventsStatusReq`） |
| `wegame-capi-biz` | **核心业务**：per-game module、LiteFlow 干预、Pulsar 监听、service/manager、Nacos/Mongo/Pulsar/ES 配置 | 99% 业务逻辑（见 `wegame-capi-biz/CLAUDE.md`） |
| `wegame-capi-scheduler` | 定时任务：每游戏一组（bet 状态、缓存刷新、运营事件、血池、星卡）+ 连接池/线程池监控 | 后台批处理、告警 |
| `wegame-capi-application` | 启动类 + Controller + 各环境配置文件 | HTTP 入口、`resources/config/<env>/`、`resources/json/<game>_config.json` |

依赖链：`client → common → infrastructure → api → dao → biz → scheduler → application`。

启动类：`wegame-capi-application/.../Application.java`（`@MapperScan("com.wegame.capi.dao.*")`，`scanBasePackages=com.wegame.*`；排除 Redisson/Session/Mongo 的自动配置，均自定义）。

## 三、技术栈 / 中间件（谁负责什么）

| 中间件 | 用途 | 配置位置 |
|--------|------|----------|
| **Nacos** | 配置中心（每游戏 `*_config.json`、LiteFlow 规则 xml、动态 Pulsar 参数） | `application/resources/config/<env>/bootstrap.yml` |
| **MongoDB** | 游戏主数据：bet_record、模板、`operation_event_*`、`jackpot_*` | `biz/config/mongo/<Game>MongoConfig`（每游戏 primary/replica + 事务） |
| **MySQL/MariaDB** | 用户/登录/后台配置 | dynamic-datasource：`wegame_user_db` + `wegame_backend_db` |
| **Redis (Lettuce)** | 用户/bet/配置热缓存、DOS 间隔局数、干预状态 | `infrastructure/cache/*`、每游戏 `application-redis-*.yml` |
| **Pulsar** | 事件流：bet record、operation event（4 路）、jackpot | `biz/config/pulsar/*`、`biz/listener/*` |
| **Elasticsearch** | TEMPLATE_PLAY 的**预生成盘面模板**库（线上出盘面靠它）+ 分析 | `application-elasticsearch.yml`；索引名见 `rtpMap.indexName` / `templateRangeConfig` |
| **LiteFlow** | 玩家干预规则引擎，规则存 Nacos，热加载 | `resources/json/common_intervention_rules.xml` |

## 四、必须先懂的核心概念

1. **两种执行模式（`GamePlayTypeEnum`，与游戏正交）**：几乎每款游戏都**同时**实现 `<Game>GameNormalPlay` 和 `<Game>GameTemplatePlay`，由 `BetService.buildGamePlayType` 切换（⚠️ 不是"某些游戏是A、某些是B"）：
   - **NORMAL_PLAY 实时抽符号**：现场按权重/概率掷符号 → 套赔率算出倍率（倍率是"抽"出来的）。**仅非 prod / 带 `at` 测试标记时走**。
   - **TEMPLATE_PLAY 查预生成模板**：先定"目标倍率区间桶" → 从 ES 捞一个落在该区间的预生成盘面。**PROD 恒走这个**（详见「五、投注主流程」）。
2. **统一赔率配置（每游戏 JSON）**：`resources/json/<game>_config.json` → `biz/config/domain/<Game>Config`（继承 `GameBizConfig`）。关键块：
   - `oddsConfig.commonOddsConfig`（符号→中奖形态→赔率，渲染/算奖用）；`probabilisticConfig`（每列符号概率，生成盘面用）。
   - **模板桶配置有两种 schema**（都属 TEMPLATE_PLAY）：① 简版 `templateRangeConfig`（A/B 模板→倍率区间权重，8 款）；② 全版 `commonTemplateConfig.rtpMap` + `rtpControlMap` + `multipleRangeWeightMap`（多 RTP 档 96/97/98rtp + 血池/上下文路由，仅 `diwata_mayari`、`lamang`）。见 `config/domain/common/CommonTemplateConfig.java`。
   - 抽桶 / 抽符号统一走 `RandomService.randomKeyByWeight`。
3. **DOS 点控 = OperationEvent（运营事件）**：Pulsar 4 路下发（`dynamic.operation.{external|internal|ap|np}`）→ Mongo `operation_event_*` → `AbstractOperationEventManager`（`getEventBetResult`/`getInterventionAmount`/`getWheelId`）把 bonus 折算成倍率抽桶 → `DosGapRuleEnum` 冷却 → `reportEvent`/`UpdateEventsStatusReq` 上报。
4. **玩家干预 = LiteFlow 链**：`common_intervention_rules.xml` 两条链（pre/post），11 个组件继承 `module/base/intervention/AbstractNodeComponent`，四步判定（触发条件→概率→重复规则→产出 rtp 档）。

## 五、投注主流程（线上 = TEMPLATE_PLAY）

`BetService.bet` → `<Game>BetContext.bet` → play。线上只跑 TEMPLATE_PLAY，核心在 `module/base/game/AbstractGameTemplatePlay`：

```
getTemplate()                                    # 每游戏 <Game>GameTemplatePlay 覆写
 ├─ 选 RTP 档
 │   ├─ getRtpWithBloodPoolControl(): 血池档(poolCategory+level) → commonTemplateConfig.getRtp(); 匹配异常兜底
 │   └─ 或 getDefaultRtp(): 按 betPromotionType → rtpControlSubKey 选默认档
 ├─ 选桶  getTemplateMultipleRange() = randomKeyByWeight(rtp.multipleRangeWeightMap[betType])  # 抽出 "1_3" 类区间
 ├─ 捞盘面 betRecordTemplateService.listIdByMultipleRange(indexName, betType, start, end)      # ES 按区间查模板 id 列表
 │        → 随机取 id → warmup 命中走本地缓存, 否则 ES findById
 └─ templateGame().templatePlay(betDo, gamePlay, template)                                     # 用模板还原这一局→算派彩
```

- **决策收敛点**：血池 / 促销 / DOS点控 / LiteFlow干预最终都作用在「选 RTP 档 + 选倍率桶」这两步（`getRtp*` + `getTemplateMultipleRange`）——这也是对接 rtp中台 `getMultiplier` 的替换缝；后半段 ES 捞盘面（"桶内生成盘面"）保留。
- **DOS / 干预如何插入**：投注前 `isCanOperationEventBet` 命中运营事件时，`getWheelId` 折算出的档位倍率决定用哪个桶；LiteFlow `preIntervention` 产出的 `rtpName` 覆盖默认 RTP 档。
- **NORMAL_PLAY**（`<Game>GameNormalPlay.play`）是另一条线：`randomService` 现场掷符号 + `RewardHandler` 算奖，无"选桶"步骤，非 prod 用。

## 六、11 款游戏

maria_makiling · malakas · ibong_adarna · lam_ang · bakunawa · diwata_mayari · kapre · mangkukulam · sirena · atlantis · bayanihan
（枚举 `GameTypeEnum`；投注路由表 `BetService.contextNameMap`。游戏↔R↔分支对照见 `README.md`。）

## 七、构建 / 运行 / 部署

```bash
# 构建（按环境 profile：local(默认)/qa/sts/pre/prod）
mvn clean package -Pqa -DskipTests
# 本地接口文档
# http://localhost:9000/capi/doc.html
```

- 打 tag 触发发布：`git tag -a qat/v1.0.0 -m "qa version" && git push origin qat/v1.0.0`。
- CI：`.github/workflows/build-image-capi-qa-aio.yml`（JDK17 构建 → ECR → GitOps 到 slots 仓库）。
- 运行时 JDK8（`Dockerfile`，时区 Asia/Kuala_Lumpur）；线上链路：CloudFront → LB → APISIX 网关 → pods。
- 部署/运维脚本：`script/`（deploy、mysql、mongodb、elasticsearch、flink2doris、seatunnel）。

## 八、约定 / 分支

- 对象命名（见 README）：`Vo` 视图 / `Do` 领域·数据 / `Dto` 传输 / `Po` 持久化(=表结构) / `Bo` 业务。
- 基础/保护分支：`bayanihan`（`zhumeng011`）；`main` 用于 PR。当前工作分支多为 `sandbox` / `release/1.0.0`。分支现状见 `分支管理.md` 与 `README.md`。

## 九、加一款新游戏 / 加一条干预规则（改动落点）

- **新游戏**：`biz/module/<game>/`（bet/game/odds/reward/multiple + Helper/SymbolUtil/ProbabilisticUtil）→ 注册 `BetService.contextNameMap` → `biz/config/mongo/<Game>MongoConfig` → `biz/listener/{betRecord,operation}/<Game>*Listener` → `scheduler/biz/<game>/` → Nacos 加 `<game>_config.json`。
- **新干预规则**：`biz/module/base/intervention/component/<X>Intervention`（继承 `AbstractNodeComponent`，`@LiteflowComponent`）→ 在 Nacos `common_intervention_rules.xml` 挂到链上（配 tag 优先级 + data）。

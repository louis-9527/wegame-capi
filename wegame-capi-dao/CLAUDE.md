# wegame-capi-dao — 数据访问层

先读根 `../CLAUDE.md`。本文件说明数据存储的落点：**MySQL 走 `mapper/`，MongoDB 走 `repository/`**。

包根：`com.wegame.capi.dao`

## 一、两套存储的分工

| 存储 | 位置 | 装什么 |
|------|------|--------|
| **MySQL**（MyBatis，dynamic-datasource） | `mapper/` | 只有 **user / login / backend 配置**。游戏运行数据都不在这里 |
| **MongoDB** | `repository/` | **游戏主数据**：bet 记录、运营事件、奖池、星卡、模板 |

> 记住这条：查玩家账号/登录/维护配置 → `mapper/`；查投注/点控/奖池/模板 → `repository/`。

## 二、MySQL — `mapper/`

- 数据源：`wegame_user_db`（user、user_login_info）+ `wegame_backend_db`（game_maintain_config）。`@MapperScan("com.wegame.capi.dao.*")` 在启动类。
- 结构：
  - `mapper/user/` — `UserMapper`、`UserLoginInfoMapper`，及 `*ManualMapper`（手写复杂 SQL / 批量）。
  - `mapper/backend/` — `GameMaintainConfigMapper`（游戏维护窗口）。
  - `mapper/domain/<db>/` — PO（=表结构，如 `UserPo`/`UserLoginInfoPo`/`GameMaintainConfigPo`）+ `*Query`（查询条件对象）。
- 无 `*Mapper.xml`（`src/main/resources` 为空）：SQL 走注解 / `wegame-base` 基础框架封装，不是 XML 映射。

## 三、MongoDB — `repository/domain/`

每个子包一组 `*Po`（文档结构）+ `*Query`（查询条件）。文档集合名一般按游戏区分（如 `operation_event_<GAME>`），MongoTemplate 在 `../wegame-capi-biz/config/mongo/<Game>MongoConfig`。

| 子包 | 关键 PO | 用途 |
|------|---------|------|
| `business/` | `BetRecordPo`（含 jackpot/inventory/extendInfo 内嵌）、`GameRecordQuery` | 投注记录 / 盘面记录 |
| `operation/` | `OperationEventPo`、`OperationEventDetailPo`、`OperationEventConfigPo`、`OperationEventConfigRecordPo` | **DOS 点控**事件、明细、配置及审计 |
| `jackpot/` | `JackpotRewardPoolPo`、`JackpotUserRewardRecordPo`、`JackpotRewardPoolInputRecordPo` | 奖池状态、中奖记录、注水记录 |
| `star/` | `StarCardUseRecordPo` | 星卡使用记录 |
| `template/` | `BetRecordTemplatePo`、`GameRecordTemplatePo` | 形态B 游戏的预生成模板（与 ES 索引配合） |

## 四、约定

- `Po` = 与库结构一致的持久化对象；`Query` = 查询条件封装；命名见根 `README.md`。
- 关键字段语义（`OperationEventPo`）：`bonus/minBonus/maxBonus/finishBonus`（金额，分）、`eventStatus`(NONE/ING/FINISH)、`eventSubStatus`(完成原因)、`reportStatus`(上报结果)、`eventId`（第三方 id，幂等）。DOS 执行/上报逻辑在 `../wegame-capi-biz/manager/operation/AbstractOperationEventManager`。

# 赔率抽桶逐层走读（成都 wegame-capi ↔ RTP 中台四层模型）

> 配套 [`CLAUDE.md`](CLAUDE.md) / [`wegame-capi-biz/CLAUDE.md`](wegame-capi-biz/CLAUDE.md) 的展开：把 TEMPLATE_PLAY 的「选 RTP 档 → 选倍率桶 → 捞盘面」链路拆开，映射到 RTP 中台统一模型（`Game → RtpProfile → GameStage → OddsBucket`）与详设-01 **第七章「加权抽桶算法」**。
>
> 中台详设-01：飞书 docx `QdeCdXRvNo8L2RxpUwIltztZgbo` / wiki `ZrabwcvkAiBjmektAIplgtFugje`；本地索引 `dos-egame-claude-code/docs/rtp-platform/detail-design.md`（成都映射见 §5.2）。
>
> 对照文件（均在 `wegame-capi-biz/src/main/java/com/wegame/capi/biz/`，简写去掉此前缀）：
> - `service/RandomService.java`（抽桶内核）
> - `config/domain/common/CommonTemplateConfig.java`（全版 schema：rtpMap + rtpControlMap）
> - `config/domain/MariaMakilingConfig.java`（简版 schema：templateRangeConfig，样例）
> - `module/base/game/AbstractGameTemplatePlay.java`（选档 + 选桶 + 捞盘面公共骨架）
> - `module/diawatamayari/game/DiwataMayariGameTemplatePlay.java`（全版游戏样例）
> - `module/mariamakiling/game/MariaMakilingGameTemplatePlay.java`（简版游戏样例）
> - `manager/operation/AbstractOperationEventManager.java` + `common/utils/MultipleUtil.java`（DOS steer）
> - `module/base/intervention/AbstractNodeComponent.java`（LiteFlow 干预产出 rtpName）
> - `service/template/BetRecordTemplateService.java`（ES 捞盘面，游戏侧）

---

## 一、四层对应关系 + 与中台的关键差异

| 中台模型 | 本库对应 | 位置 |
|---|---|---|
| **Game** | `GameTypeEnum` + 每游戏 `<Game>Config`（Nacos JSON 热加载） | `client/enums/GameTypeEnum`, `config/domain/*Config.java` |
| **RtpProfile**（选权重集） | **两种 schema**：① 全版 `CommonTemplateConfig.rtpMap<rtpName, Rtp>`（多档，仅 diwata_mayari / lamang）；② 简版 `templateRangeConfig`（单套，其余 8 款） | `CommonTemplateConfig.java:42`；`MariaMakilingConfig.java:187` |
| **GameStage**（玩法阶段） | `BetTypeEnum`（NORMAL_BET / free / …）→ `multipleRangeWeightMap.get(betType)`；简版 = `mainGame` / `freeGame` 二分 | `CommonTemplateConfig.java:129`；`MariaMakilingConfig.java:189-198` |
| **OddsBucket**（赔率桶） | range 键 `"start_end"` = **左开右闭 `(start, end]`** → `TemplateMultipleRange`；value = 权重 | `TemplateMultipleRange.java:30`；ES 查询 `BetRecordTemplateService.java:110-118` |
| **抽桶算法（第七章）** | `RandomService.randomKeyByWeight`（整数权重轮盘） | `RandomService.java:60-82` |
| **档位路由**（血池/默认→选档） | `getRtpWithBloodPoolControl`（血池 poolCategory+level → 档）/ `getDefaultRtp`（betPromotionType → subKey → 档） | `AbstractGameTemplatePlay.java:68-94` |

**四条与深圳/台湾不同的结构差异（重点）**：

1. **桶是「区间字符串」而非枚举/结构体**：桶键就是 `"1_3"` 这样的字符串，`TemplateMultipleRange.build` 拆成 `start=1, end=3`，语义 **左开右闭 `(1, 3]`**（见 ES 查询 `.gt(start).lte(end)`，`BetRecordTemplateService.java:116-117`；`start==end` 时退化为 `termQuery` 单点，:110-112）。→ 中台 `boundType = LEFT_OPEN_RIGHT_CLOSED`，`start==end` → `POINT`。
2. **配置载体 = Nacos JSON（热加载），不是 YAML 文件也不是硬编码**：每游戏一份 `<game>_config.json`，`@RefreshScope + @ConfigurationProperties`（`MariaMakilingConfig.java:31-33`）；改权重即改 Nacos，无需发版。→ 深圳 = YAML 文件，台湾 = 代码里 `SetWeights` 硬编码。
3. **RtpProfile 分「有多档」和「无多档」两族**：只有 diwata_mayari / lamang 用全版 `rtpMap`（96/97/98rtp 多档 + `rtpControlMap` 路由），最像深圳多档 YAML；其余 8 款只有一套 `templateRangeConfig`（无 RTP 档概念，靠 mainGame/freeGame 两个权重表），最像台湾「无预置档」——但成都是**配置驱动**而非硬编码。
4. **DOS 点控走独立的「预生成 wheel 盘面池」，不与普通投注共用 ES 模板**：点控命中时 `getWheelId` 反算出最近的 `configMultiple` + `configId`（wheelId），EVENT_PLAY 用 `configId` 从 Mongo `operation_event_config_record` 捞整局预生成盘面（`DiwataMayariGameEventPlay.java:53`），而非从 ES 抽桶。→ 桶=最近配置倍率（POINT），盘面=另一套池。

---

## 二、热路径编排（TEMPLATE_PLAY，线上恒走）

投注入口链：`BetService.bet` → `<Game>BetContext.bet` → play → `AbstractGameTemplatePlay.play`（`AbstractGameTemplatePlay.java:53`）→ `getTemplate`（每游戏覆写）。核心三步：

```
getTemplate(betDo, gamePlayDo)                                        # 每游戏 <Game>GameTemplatePlay 覆写
 ├─① 选 RTP 档（= 选 RtpProfile）
 │    ├─ getRtpWithBloodPoolControl(): 血池档(poolCategory+currentLevelNo) → commonTemplateConfig.getRtp()  # 匹配异常→兜底
 │    └─ getDefaultRtp(): betPromotionType → defaultRtpControlSubKey → getRtp(DEFAULT, subKey)
 ├─② 选倍率桶（= 抽 OddsBucket，第七章）
 │    getTemplateMultipleRange(rtp.multipleRangeWeightMap.get(betType))
 │      = randomService.randomKeyByWeight(treeMap)   → 抽出 "1_3" → TemplateMultipleRange(start=1,end=3)
 └─③ 捞盘面（游戏侧，不进中台）
      betRecordTemplateService.listIdByMultipleRange(indexName, betType, start, end)  # ES 按区间查模板 id 列表
        → getRandomElement(idList) 随机取一个 id
        → supportWarmUp ? getTemplateWithLocalCache(本地缓存) : findById(ES)          # 取整局预生成盘面
      → templateGame().templatePlay(betDo, gamePlay, template)                        # 用模板还原本局→算派彩
```

**决策收敛点 = ①②**（`getRtp*` + `getTemplateMultipleRange`）——这就是对接中台 `getMultiplier` 的**替换缝**；③ ES 捞盘面（桶内生成盘面）保留在游戏侧。

全版样例 `DiwataMayariGameTemplatePlay.getTemplate`（`DiwataMayariGameTemplatePlay.java:51-61`）：

```java
CommonTemplateConfig.Rtp rtp = getRtpWithBloodPoolControl(betDo, gamePlayDo,
        diwataMayariConfig.getCommonBloodPoolConfig(),
        diwataMayariConfig.getCommonTemplateConfig());        // ① 选档（血池优先）
return RetryUtil.execute(() -> fetchTemplate(betDo, gamePlayDo, rtp), ...);  // ②③ 抽桶+捞盘面(带派彩上限重试)
```

`fetchTemplate`（公共骨架 `AbstractGameTemplatePlay.java:96-115`；diwata 覆写版 `DiwataMayariGameTemplatePlay.java:64-82` 额外加了单笔派彩上限校验 `templateValidate` → 超限 `RetryException` 重抽）：

```java
String templateIndexName = rtp.getIndexName();                                            // 档 → ES 索引名
TemplateMultipleRange multipleRange = getTemplateMultipleRange(rtp.getMultipleRangeWeightMap().get(betType)); // ② 抽桶
List<Long> ids = betRecordTemplateService.listIdByMultipleRange(indexName, betType, start, end);  // ③ 桶内候选盘面 id
Long betRecordId = getRandomElement(ids);                                                 // ③ 随机取一个
return supportWarmUp ? getTemplateWithLocalCache(...) : findById(...);                    // ③ 取盘面
```

> **`indexName` 随档变**：`rtp.getIndexName()`（`CommonTemplateConfig.java:124`）——不同 RTP 档指向**不同 ES 索引**（不同的预生成盘面池）。选档不仅换权重表，还换盘面池。这与深圳「各档共享盘面池、只权重不同」不同（见 §九）。

---

## 三、桶（OddsBucket）与抽桶内核

### 桶键结构：`TemplateMultipleRange`

```java
// TemplateMultipleRange.java:30 —— 桶键字符串 "start_end" 解析
String[] arr = multipleRangeStr.split("_");         // "1_3" → ["1","3"]
BigDecimal left  = new BigDecimal(arr[0]);           // start
BigDecimal right = new BigDecimal(arr[1]);           // end
```

- 桶键 `"1_3"` = 中台 `bucketId`（「字符串统一承载来源桶键」的一个实例）。
- 区间语义 **左开右闭 `(start, end]`**：由 ES 查询 `.gt(start).lte(end)`（`BetRecordTemplateService.java:115-117`）坐实 → 中台 `boundType = LEFT_OPEN_RIGHT_CLOSED`。
- `start == end`（如 `"5_5"`）→ ES `termQuery("multiple", start)` 单点匹配（:110-112）→ 中台 `POINT`（lo=hi）。
- **`start/end` 是真实赔率边界**（不是深圳/台湾那种轮盘累积位）——可直接映射成中台 `lowerBound/upperBound`。

### 抽桶 = 中台第七章：`RandomService.randomKeyByWeight`

```java
// RandomService.java:60-82 —— 标准整数权重轮盘
public <T> T randomKeyByWeight(Map<T, Integer> map, Integer totalWeight) {
    if (mapSize > 1 && totalWeight > 1) {
        int randomNumber = this.randomInt(Constants.CONST_1, totalWeight);   // 随机 [1, total]
        int startVal = 0;
        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return entry.getKey();                                        // 命中累积区间 → 返回桶键
            } else {
                startVal += entry.getValue();                                 // 未命中累加
            }
        }
    } else {
        // 单桶/总权重=1 兜底：返回首个 weight>0 的 key
    }
}
```

逐行对照第七章伪代码：

| RandomService.java | 第七章伪代码 | 说明 |
|---|---|---|
| `randomInt(CONST_1, totalWeight)`（65） | `r = randomInt(1, total)` | 随机数 `[1, total]`（与中台边界惯例完全一致） |
| `for entry : map.entrySet()`（67） | `for (bid,w) in pool` | 遍历桶（`TreeMap` 有序，遍历顺序稳定） |
| `randomNumber > startVal && <= startVal+w`（68） | `r > startVal && r <= startVal+w` | 命中累积区间 |
| `startVal += entry.getValue()`（71） | `startVal += w` | 累加 |
| `return entry.getKey()`（69） | `return {bucketId,...}` | 返回选中桶键 |

**这是全库统一的加权抽取器**：抽倍率桶、NORMAL_PLAY 抽符号 mode、抽 reel 符号（`symbolWeights` / `modeWeights` / `fgNumbersWeights`…）全走它。中台只关心它抽「倍率桶」的那次调用。

> 便捷重载 `randomKeyByWeight(map)`（:84-87）：先 `sum` 权重再调主方法。倍率桶抽取即走这个重载（`AbstractGameTemplatePlay.java:63`、`MariaMakilingGameTemplatePlay.java:192/194`）。

---

## 四、RtpProfile：两种 schema

### 全版：`CommonTemplateConfig`（多 RTP 档，仅 diwata_mayari / lamang）

```java
// CommonTemplateConfig.java
TreeMap<String, Rtp> rtpMap;                              // :42  rtpName(如 "96rtp") → Rtp   = 多个 RtpProfile
TreeMap<String, TreeMap<String, String>> rtpControlMap;  // :47  档位路由：key → (subKey → rtpName)

static class Rtp {                                        // :120  = 一个 RtpProfile
    String indexName;                                    // :124  该档的 ES 盘面池索引
    TreeMap<BetTypeEnum, TreeMap<String, Integer>> multipleRangeWeightMap;  // :129  betType → (桶键 → 权重)
}
```

映射中台：
- `rtpMap<rtpName, Rtp>` = 一组 **RtpProfile**（多档，如 `96rtp` / `97rtp` / `98rtp`）。
- `Rtp.multipleRangeWeightMap<betType, <range, weight>>` = 该 profile 下 **每个 GameStage（betType）的 OddsBucket 权重集**。
- `Rtp.indexName` = 该档专属盘面池（**换档即换盘面池**）。

`getRtp(key, subKey)`（`CommonTemplateConfig.java:54-61`）= 两级查表选档：`rtpControlMap[key][subKey]` → `rtpName` → `rtpMap[rtpName]`。

`validate`（:68-113）在 Nacos 加载时强校验：每个 rtp 的 `indexName` 非空、每个 `BetTypeEnum` 都有 `rangeMap`、每个桶键能被 `TemplateMultipleRange.build` 解析且 `end >= start >= 0`、权重 `>= 0`；`rtpControlMap` 必须含 `DEFAULT` 项且其 `default/freeSpin/starCard` 三个 subKey 非空（:104-108）。配置错在**启动/热加载期**暴露。

### 简版：`templateRangeConfig`（单套，其余 8 款）

```java
// MariaMakilingConfig.java:187-197 —— 无 RTP 档，只有两个 stage 的权重表
static class TemplateRangeConfig {
    Map<String, Integer> mainGame;   // 主游戏：桶键 → 权重
    Map<String, Integer> freeGame;   // 免费游戏：桶键 → 权重
}
```

抽桶（`MariaMakilingGameTemplatePlay.java:189-198`）：

```java
private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betType) {
    String range = Objects.equals(betType, BetTypeEnum.NORMAL_BET)
        ? randomService.randomKeyByWeight(config.getTemplateRangeConfig().getMainGame())   // NORMAL_BET → mainGame
        : randomService.randomKeyByWeight(config.getTemplateRangeConfig().getFreeGame());  // 其余 → freeGame
    return TemplateMultipleRange.build(range);
}
```

映射中台：**简版 = 单一 RtpProfile**（无档），`mainGame`/`freeGame` = 两个 GameStage 各自的 OddsBucket 权重集。校验见 `templateRangeConfigValidate`（:362-382，强制桶键含 `_`、两段是数字、`arr[0] <= arr[1]`）。

> ⚠️ 简版游戏的 ES 盘面池按 `TemplateTypeEnum`（如 `TemplateTypeEnum.MARIA_MAKILING`）定位（`MariaMakilingGameTemplatePlay.java:74`），不像全版走 `rtp.indexName`。盘面呈现细节属游戏侧，不影响中台抽桶语义。

---

## 五、档位路由（血池 / 默认 → 选 RtpProfile）

只有全版游戏走档位路由（简版无档）。入口 `getRtpWithBloodPoolControl`（`AbstractGameTemplatePlay.java:68-87`）：

```java
GetPoolConfigRes.BloodPoolConfig cfg = bloodPoolControlService.matchBloodPoolConfig(betDo, commonBloodPoolConfig);
if (cfg != null) {
    try {
        return commonTemplateConfig.getRtp(String.valueOf(cfg.getPoolCategory()),  // key   = 血池类别
                                           cfg.getCurrentLevelNo());               // subKey = 当前档位号
    } catch (Exception e) {
        larkHelper.sendWarningMsg(...);           // 匹配异常 → 飞书告警
        return getDefaultRtp(betDo, commonTemplateConfig);   // 兜底默认档
    }
} else {
    return getDefaultRtp(betDo, commonTemplateConfig);       // 无血池匹配 → 默认档
}
```

- **血池档直接当 RtpProfile 选择器**：`(poolCategory, currentLevelNo)` → `rtpControlMap` → `rtpName` → 该档权重集 + 盘面池。**这正是中台「血池：余额 → 档位 → 选 RTP 权重档」的语义**——成都是三家里最贴合中台血池模型的（台湾 `game_pool` 是 jackpot 大奖池 ≠ 血池；深圳靠 betCount 换档而非血池余额）。
- 兜底 `getDefaultRtp`（:89-94）：`betPromotionType.getDefaultRtpControlSubKey()`（default / freeSpin / starCard）→ `getRtp(DEFAULT, subKey)`。

> **血池档在中台归属**：档位选择逻辑本身是「决策层对权重集的选择」，属**详设-02 决策层**；档→权重表/盘面池这份静态配置进详设-01。

---

## 六、RTP 动态调制：LiteFlow 玩家干预（rtpName 覆盖）

玩家干预链（`common_intervention_rules.xml` 的 `<gameId>_preIntervention`）产出一个 `PlayerInterventionResultDo`，其 `rtpName` **覆盖默认档**，从而换权重表 + 盘面池。

组件基类 `AbstractNodeComponent`（`AbstractNodeComponent.java`）四步判定（`doProcess`，:66-94）：

```
canContinue()        # 链优先级/重入判定（高优先级结果不被低优先级覆盖，:36-56）
→ triggerCondition() # 读 UserMetricInfoDo 判触发条件（子类实现）
→ triggerProbability()# 万分位随机 canTrigger(:129-139): randomInt(1,10000) <= 概率*10000
→ repeatCondition()  # always / today_not / never_repeat（commonRepeatCondition, :112-123）
→ intervention()     # 产出 PlayerInterventionResultDo
```

产出结果 `buildInterventionResult`（:141-162）：

```java
PlayerInterventionResultDo.builder()
    .rtpName(config.getRtpName())                    // ★ 覆盖档名 → 换 RtpProfile
    .backstopRtpName(config.getBackstopRtpName())    // 兜底档名
    .templateMultipleRange(templateMultipleRange)    // 亦可直接指定桶
    ...
```

`PlayerInterventionResultDo.rtpName`（`PlayerInterventionResultDo.java:37`，注释示例 `98rtp`）随后被 `commonTemplateConfig.getRtpByRtpName(rtpName)`（`CommonTemplateConfig.java:63-66`）解析成具体档，替换 `getRtpWithBloodPoolControl` 的默认选择。

> ⚠️ **个人 RTP 能力很薄**：干预只读 `UserMetricInfoDo.getRtp()`（累计 payout/bet 阈值，rtpHigh/rtpLow 两条规则），**无滚动窗口、无 recovery 状态机**（详见 `wegame-capi-biz/CLAUDE.md` §四）。这正好落在**中台详设-04「个人 RTP / 个人策略」首版暂缓**的范围内 —— 与台湾 `getTWOBonus2`+`checkMax` 撞同一个 gap（见系列跨篇结论）。**代码生成阶段跳过这部分**，首版由点控（DOS 异步策略）承载。

---

## 七、DOS 点控 steer（OperationEvent，详设-05）

点控是**独立于普通抽桶的一条替换链**：命中活跃点控 → 把剩余 bonus 反算成目标倍率 → 找最近的配置档位（configMultiple）→ 用其 `configId` 捞预生成 wheel 盘面（EVENT_PLAY），彻底旁路 ES 抽桶。

### 判定入口（`AbstractCommonBetStrategy.isCanOperationEventBet`，`AbstractCommonBetStrategy.java:76-111`）

```java
if (!operationEnable) return false;
if (!supportDosChannelIdSet().contains(channelId)) return false;   // 渠道白名单
if (betType == NORMAL_BET) {
    if (operationEventControlService.isInDosGap(userInfo)) return false;   // 冷却期内不干预
    GameOperationEventDo e = operationEventManager().getEventBetResult(payAmount, channelId);  // 反算 wheel
    if (e != null && e.isEventResult()) {
        betDo.setGameOperationEvent(e);
        betDo.setBetPromotionType(OPERATION_EVENT);   // 打上点控标记 → 走 EVENT_PLAY
        return true;
    }
}
```

### 反算目标倍率 → 选档位（`AbstractOperationEventManager`）

`getEventBetResult`（:243-267）编排：`getEffectedEvent`（取生效点控事件，:89-150，含未到时/过期/已完成的清理与上报）→ `getInterventionAmount`（:272-298）→ `getWheelId`（:303-315）。

```java
// getInterventionAmount：本笔要摊派多少 bonus
long eventAmount = eventDo.getBonus() - finishBonus;                 // 剩余待摊
if (finishBonus + eventAmount > maxBonus) eventAmount = maxBonus - finishBonus;   // 受 maxBonus 夹
if (eventAmount < minBetMultiple * betAmount) { ...FINISH_AMOUNT_ERROR; return null; }  // 摊不动→结束

// getWheelId：bonus → 目标倍率 → 最近配置档位（= 桶）
long interveneBalance = interventionAmount + payAmount;
double multiple = (double) interveneBalance / payAmount;             // 目标倍率 = (摊派+本金)/本金
Double closest = MultipleUtil.findClosestMultiple(multiple, getConfigMultipleArray());  // 二分找 ≤ 且最近的档
Long wheelId = getPayMultipleConfigId(closest);                     // 该档位下随机取一个 wheel 配置 id
return Pair.of(wheelId, closest);                                   // (configId, configMultiple)
```

`MultipleUtil.findClosestMultiple`（`MultipleUtil.java:9-28`）= 升序数组二分，返回「≤ input 且最接近」的配置倍率；`< MIN_BET_AMOUNT_M` 返回 null。→ **对应中台「DOS bonus 反算 targetOdds → 选桶」**，`configMultiple` = 命中的桶（离散 POINT），`configId` = 该档下的一个预生成 wheel。

`getPayMultipleConfigId`（:505-526）：同一玩家同倍率连续命中时用 Redis 计数器 `increment` 在多组 wheel 间轮转（避免连续命中同一滚轮），组内再 `ThreadLocalRandom` 随机取一个。

### 执行（EVENT_PLAY）与结算

- **执行**：`betForOperationEvent`（`AbstractCommonBetStrategy.java:116-148`）→ `generateBetRecord` 把 `gamePlayType` 置 `EVENT_PLAY`（:273-277）→ `<Game>GameEventPlay.play` 用 `configId` 从 Mongo `operation_event_config_record` 捞整局预生成盘面（`DiwataMayariGameEventPlay.java:53`：`listByBetRecordId(configId, ...)`），逐轮还原派彩。**不经 ES 抽桶。**
- **冷却**：`DosGapRuleEnum.forConfigMultiple(configMultiple)`（`DosGapRuleEnum.java:44-56`）按命中倍率定下次干预前的间隔局数：`≥100 → [20,50]`、`[5,100) → [20,30]`、`<5 → 不冷却`；间隔写 Redis（`isInDosGap` 读）。
- **结算上报**：`asyncUpdateEventDetail`（:317-348，`finishBonus += eventAmount`，≥`minBonus` → FINISH）→ `reportEvent`（:414-424，分布式锁）→ `UpdateEventsStatusReq`（:463-471）回传点控系统，附本次摊派的 `betNos` + `executeBonus`。

> **对应中台详设-05**：点控 steer 摊派语义完全对齐——`getWheelId` ≈ 中台「bonus 反算 targetOdds 选桶」，冷却 `DosGapRuleEnum` ≈ 摊派节流，`UpdateEventsStatusReq` ≈ 结算回传（中台走异步 Pulsar → Consumer 上报，游戏方不碰点控）。成都的特殊点：**桶命中后盘面来自独立 wheel 池**，非普通盘面池。

---

## 八、桶内盘面（ES / wheel 池，游戏侧，不进中台）

普通投注：`BetRecordTemplateService.listIdByMultipleRange`（`BetRecordTemplateService.java:99-129`）按 `(betType, multiple∈(start,end])` 在 ES 索引里 `search_after` 分页拉全部命中 id（每批 5000，`fetchBatch` 只取 `id` doc-value，:258-286），带 `@Cacheable` 本地缓存整份 id 列表 → 游戏侧 `getRandomElement` 随机取一个 → `supportWarmUp`（`CommonTemplateConfig.java:49-52`：桶上界 ≤ 预热阈值）命中走本地缓存 `getTemplateWithLocalCache`（:226-229），否则 `findById` 回 ES（:70-96）。另有 `randomTemplateWithNoCache`（:231-256）用 ES `randomFunction` 直接取一条。

DOS 投注：盘面来自 Mongo `operation_event_config_record`（按 `configId` 取整局，见 §七）。

**两者都是「中台给桶 → 游戏据桶捞预生成盘面」的第②段**，纯游戏侧数据呈现，**不进详设-01**（等同深圳「签名索引 + 盘面池」、台湾「`totalXxxCountMap` + 偏移」）。

---

## 九、与深圳 / 台湾对照

| 维度 | 深圳 predefined | 台湾 rtpManager | **成都 wegame-capi** |
|---|---|---|---|
| RtpProfile（档） | `RtpMode` 多份 YAML，同区间异权重 | 无预置档，单套硬编码 + 运行时调 | **两族**：全版 `rtpMap` 多档（异索引异权重）/ 简版 `templateRangeConfig` 单套 |
| 桶形态 | Range 区间（左开右闭/单点/±∞） | `Multiple` 枚举**单点** | range 字符串 `"start_end"` = 左开右闭 `(s,e]`，`s==e` 退化单点 |
| 桶配置载体 | YAML 文件 | 硬编码在 slotNNN.go | **Nacos JSON（热加载）** |
| 盘面池随档 | **共享**（各档同池，只权重不同） | 单套 | **随档换**（`rtp.indexName` 各档不同 ES 索引） |
| stage 选择 | 调用方 `Rz(ResultType)` 显式 | 服务内 `isFree/isEvent` 概率 | 调用方传 `BetTypeEnum` **显式**（近深圳） |
| 盘面类型(kind) | 独立加权 `KindRandomizer` | `TapeEvent` 分支副产物 | **无独立 kind 随机**（盘面=整局模板，mode/reel 在生成期已定） |
| 桶内盘面 | `randomResultIdInRange` → result id | `rand(totalXxxCountMap)+偏移` → `N` | ES `listIdByMultipleRange` → 随机 id |
| 抽桶内核 | `saferand.RandomMapKey` | `GetWeights`/`GetWeightsBonus` | `RandomService.randomKeyByWeight` |
| RTP 调制 | betCount → RtpMode（选档） | bonus 注入 + checkMax 降档 | 选档：血池档 / LiteFlow `rtpName` 覆盖 |
| 血池语义 | 无（betCount 换档） | `game_pool` = jackpot 大奖池（≠血池） | **血池档 = RTP 档选择器**（最贴合中台） |
| DOS steer | `RandomResultByOdds`(targetOdds) | `dosEvent`→`getMu`(targetOdds) | `getWheelId`→`findClosestMultiple`(targetOdds) + 独立 wheel 池 |

**三家共同点**：抽桶都是**整数权重轮盘**（中台第七章，`RandomMapKey`/`GetWeights`/`randomKeyByWeight` 同构）；桶内盘面池 + 盘面索引都在**游戏侧**，不进详设-01；DOS 都是「剩余 bonus 反算 targetOdds → 选桶 → 摊派 + 冷却 + 回传」。

**成都独有**：① 配置走 Nacos 热加载；② 血池档语义与中台完全一致；③ 换档同时换盘面池（ES 索引）；④ DOS 用独立 wheel 盘面池而非共用普通盘面。

---

## 十、映射回中台四层（一句话）

```
GameTypeEnum + <Game>Config(Nacos)                            →  Game
  └ 全版 rtpMap<rtpName,Rtp> / 简版 templateRangeConfig        →  RtpProfile
     （档位路由：血池 poolCategory+level / betPromotionType subKey / 干预 rtpName 覆盖 = 详设-02；个人RTP暂缓=详设-04）
      └ betType → multipleRangeWeightMap.get(betType) / mainGame·freeGame   →  GameStage
          └ randomService.randomKeyByWeight(桶键→权重)  →  抽 OddsBucket（"start_end" 左开右闭桶）= 第七章
              └ ES listIdByMultipleRange(indexName, betType, start, end) → 随机 id → 盘面（游戏侧，非中台）

  点控命中：getEventBetResult → getWheelId(findClosestMultiple 反算 targetOdds) → configId 捞 wheel 盘面（EVENT_PLAY）
           冷却 DosGapRuleEnum + 回传 UpdateEventsStatusReq  =  详设-05 DOS 接入
```

**一句话总纲**：成都在「选 RTP 档（血池/默认/干预）+ 加权抽倍率桶」这两步做完全部 RTP 决策，桶键是左开右闭的倍率区间字符串，抽桶内核是标准整数权重轮盘；桶选定后从 ES（普通）或 wheel 池（点控）捞一个预生成盘面还原本局——**前半段 = 中台 `getMultiplier` 的替换缝，后半段盘面生成留游戏侧**。
</content>
</invoke>

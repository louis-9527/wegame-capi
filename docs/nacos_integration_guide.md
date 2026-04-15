# 新游戏接入 Nacos 配置功能变更指南

基于 `malakas` 分支与 `malakas_nacos` 分支的差异总结。

---

## 概述

本文档说明在现有项目中为新游戏接入 Nacos 配置中心功能所需的全部变更。参考已有游戏（MariaMakiling、Malakas）的实现模式。

---

## 变更概览

| 序号 | 变更项 | 变更类型 |
|------|--------|----------|
| 1 | bootstrap.yml 配置 | 修改 |
| 2 | NacosConfigRefresherUtil.java | 修改 |
| 3 | 新建游戏配置类 | 新增 |
| 4 | Nacos 控制台配置 | 新增 |

---

## 详细变更说明

### 1. 配置文件变更

#### 1.1 bootstrap.yml (各环境)

路径：`wegame-capi-application/src/main/resources/config/{env}/bootstrap.yml`

在 `extension-configs` 中添加新游戏的配置：

```yaml
spring:
  cloud:
    nacos:
      config:
        extension-configs:
          - data-id: wegame-capi-local-maria_makiling_config.json
            group: wegame-capi-local
            refresh: true
          - data-id: wegame-capi-local-malakas_config.json
            group: wegame-capi-local
            refresh: true
          # 新游戏配置
          - data-id: wegame-capi-local-newgame_config.json
            group: wegame-capi-local
            refresh: true
```

#### 1.2 游戏开关配置

在同一文件中添加游戏开关：

```yaml
game:
  enable:
    MariaMakiling: true
    Malakas: true
    NewGame: true  # 新游戏开关
```

#### 1.3 Nacos 控制台配置

需要在 Nacos 控制台创建对应的配置文件：

- **Data ID**: `wegame-capi-{env}-{game}_config.json`
- **Group**: `wegame-capi-{env}`
- **格式**: JSON

**配置内容格式要求**：

```json
{
    "newgame": {
        "oddsConfig": {...},
        "probabilisticConfig": {...},
        "scatterFreeWheelTimesConfig": {...},
        "rewardMaxMultiple": 5000,
        "templateRangeConfig": {...}
    }
}
```

> ⚠️ **重要**：JSON 根节点名称必须与配置类中的 `@ConfigurationProperties(prefix = "xxx")` 保持一致（小写）。

---

### 2. 代码变更

#### 2.1 新建游戏配置类

路径：`wegame-capi-biz/src/main/java/com/wegame/capi/biz/config/domain/{Game}Config.java`

参考 `MalakasConfig.java` 创建新游戏的配置类：

```java
package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wegame.capi.biz.config.nacos.NacosConfigRefresherUtil.NEWGAME_JSON_ROOT_NAME;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

/**
 * 新游戏配置
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@RefreshScope
@ConfigurationProperties(prefix = NEWGAME_JSON_ROOT_NAME) // 匹配JSON根节点，推荐以游戏命名（不可带任何分隔符，所有字母小写）
public class NewGameConfig extends GameBizConfig implements Serializable, ConfigValidate {

    private final static int REEL_NUMBERS = 7;

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;

    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;

    /**
     * Scatter个数对应的免费轮次数
     */
    private TreeMap<Integer, Integer> scatterFreeWheelTimesConfig;

    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;

    /**
     * 模板配置
     */
    private TemplateRangeConfig templateRangeConfig;

    // ========== 内部类定义 ==========

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OddsConfig implements Serializable {
        /**
         * TreeMap<元素ID，<赔率类型，具体赔率>>
         */
        private TreeMap<String, TreeMap<String, BigDecimal>> commonOddsConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProbabilisticConfig implements Serializable {
        /**
         * 正常轮概率
         */
        private PlayProbabilisticConfig commonPlayProbabilisticConfig;
        /**
         * 免费轮概率
         */
        private PlayProbabilisticConfig freePlayProbabilisticConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayProbabilisticConfig implements Serializable {
        /**
         * 滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> wheelProbabilisticConfig;
        /**
         * 元素大小概率配置 S M L XL
         */
        private TreeMap<String, List<BigDecimal>> symbolSizeProbabilisticConfig;
        /**
         * 每个reel出现金边概率配置: eg：5%
         */
        private List<BigDecimal> goldBoarderProbabilisticConfig;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class TemplateRangeConfig implements Serializable {
        private static final long serialVersionUID = -734841249803450751L;
        /**
         * 主游戏模板配置
         */
        private Map<String, Integer> mainGame;
        /**
         * 免费游戏模板配置
         */
        private Map<String, Integer> freeGame;
    }

    // ========== 校验方法 ==========

    @Override
    public void validate() {
        oddsConfigValidate();
        probabilisticConfigValidate();
        scatterFreeWheelTimesConfigValidate();
        rewardMaxMultipleValidate();
        templateRangeConfigValidate();
    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }

    private void oddsConfigValidate() {
        Assert.notNull(oddsConfig, "oddsConfig is null");
        Assert.notNull(oddsConfig.commonOddsConfig, "oddsConfig.commonOddsConfig is null");
        // 校验-赔率配置
        oddsConfig.commonOddsConfig.keySet().forEach(symbolId -> {
            TreeMap<String, BigDecimal> map = oddsConfig.commonOddsConfig.get(symbolId);
            AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER, "Odds Config Err");
        });
    }

    private void probabilisticConfigValidate() {
        Assert.notNull(probabilisticConfig, "probabilisticConfig is null");
        Assert.notNull(probabilisticConfig.commonPlayProbabilisticConfig, "probabilisticConfig.commonPlayProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freePlayProbabilisticConfig, "probabilisticConfig.freePlayPlayProbabilisticConfig is null");

        playProbabilisticConfigValid(probabilisticConfig.commonPlayProbabilisticConfig);
        playProbabilisticConfigValid(probabilisticConfig.freePlayProbabilisticConfig);
    }

    private void scatterFreeWheelTimesConfigValidate() {
        Assert.notNull(scatterFreeWheelTimesConfig, "scatterFreeWheelTimesConfig is null");
    }

    private void playProbabilisticConfigValid(PlayProbabilisticConfig playProbabilisticConfig) {
        wheelProbabilisticConfigValid(REEL_NUMBERS, playProbabilisticConfig.getWheelProbabilisticConfig());
        symbolSizeProbabilisticConfigValid(REEL_NUMBERS, playProbabilisticConfig.getSymbolSizeProbabilisticConfig());
        goldBoarderProbabilisticConfigValid(playProbabilisticConfig.getGoldBoarderProbabilisticConfig());
    }

    private void wheelProbabilisticConfigValid(int column, TreeMap<String, List<BigDecimal>> wheelProbabilisticConfig) {
        wheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            TreeMap<String, Integer> map = new TreeMap<>();
            wheelProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig ERR, columnNo:" + columnNo);
        }
    }

    private void symbolSizeProbabilisticConfigValid(int column, TreeMap<String, List<BigDecimal>> symbolSizeProbabilisticConfig) {
        symbolSizeProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            TreeMap<String, Integer> map = new TreeMap<>();
            symbolSizeProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid ERR, columnNo:" + columnNo);
        }
    }

    private void goldBoarderProbabilisticConfigValid(List<BigDecimal> list) {
        AssertUtil.assertTrue(Objects.equals(list.size(), 7), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelGoldBoarderProbabilisticConfig size error");
    }

    private void templateRangeConfigValidate() {
        Assert.notNull(templateRangeConfig, "templateRangeConfig is null");
        Assert.notNull(templateRangeConfig.getMainGame(), "templateRangeConfig.mainGame is null");
        Assert.notNull(templateRangeConfig.getFreeGame(), "templateRangeConfig.freeGame is null");
        templateRangeConfig.getMainGame().keySet().forEach(templateMultipleRange -> {
            Assert.isTrue(templateMultipleRange.contains("_"), "templateRangeConfig.mainGame.key not contain '_'");
            String[] multipleRangeArr = templateMultipleRange.split("_");
            Assert.isTrue(multipleRangeArr.length == 2, "templateRangeConfig.mainGame.key length is not eqauls 2");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[0]), "templateRangeConfig.mainGame.key first number must be number");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[1]), "templateRangeConfig.mainGame.key first number must be number");
            Assert.isTrue(new BigDecimal(multipleRangeArr[0]).compareTo(new BigDecimal(multipleRangeArr[1])) <= 0, "templateRangeConfig.mainGame.key first number must be less than or equal to the second number");
        });
        templateRangeConfig.getFreeGame().keySet().forEach(templateMultipleRange -> {
            Assert.isTrue(templateMultipleRange.contains("_"), "templateRangeConfig.freeGame.key not contain '_'");
            String[] multipleRangeArr = templateMultipleRange.split("_");
            Assert.isTrue(multipleRangeArr.length == 2, "templateRangeConfig.freeGame.key length is not eqauls 2");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[0]), "templateRangeConfig.freeGame.key first number must be number");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[1]), "templateRangeConfig.freeGame.key first number must be number");
            Assert.isTrue(new BigDecimal(multipleRangeArr[0]).compareTo(new BigDecimal(multipleRangeArr[1])) <= 0, "templateRangeConfig.freeGame.key first number must be less than or equal to the second number");
        });
    }
}
```

**关键注解说明**：

| 注解 | 说明 |
|------|------|
| `@Component` | 将类注册为 Spring Bean |
| `@RefreshScope` | 支持 Nacos 配置变更后自动刷新，无需重启应用 |
| `@ConfigurationProperties(prefix = "xxx")` | 绑定 JSON 配置中的根节点，必须小写 |
| `extends GameBizConfig` | 继承基础配置类 |
| `implements ConfigValidate` | 实现配置校验接口 |

#### 2.2 在 NacosConfigRefresherUtil.java 中注册

路径：`wegame-capi-biz/src/main/java/com/wegame/capi/biz/config/nacos/NacosConfigRefresherUtil.java`

添加常量定义：

```java
// 在文件顶部添加常量
public static final String NEWGAME_JSON_ROOT_NAME = "newgame";
```

添加映射关系：

```java
private static final Map<String, Class<? extends ConfigValidate>> TO_CLASSTYPE_MAP = 
    new ImmutableMap.Builder<String, Class<? extends ConfigValidate>>()
        .put("maria_makiling_config.json", MariaMakilingConfig.class)
        .put("malakas_config.json", MalakasConfig.class)
        .put("newgame_config.json", NewGameConfig.class)  // 新增
        .build();

private static final Map<String, String> TO_JSON_ROOT_MAP = 
    new ImmutableMap.Builder<String, String>()
        .put("maria_makiling_config.json", MARIAMAKILING_JSON_ROOT_NAME)
        .put("malakas_config.json", MALAKAS_JSON_ROOT_NAME)
        .put("newgame_config.json", NEWGAME_JSON_ROOT_NAME)  // 新增
        .build();
```

---

## 变更文件清单

| 文件路径 | 变更类型 | 说明 |
|----------|----------|------|
| `wegame-capi-application/src/main/resources/config/{env}/bootstrap.yml` | 修改 | 添加 extension-configs 和游戏开关 |
| `wegame-capi-biz/src/main/java/com/wegame/capi/biz/config/nacos/NacosConfigRefresherUtil.java` | 修改 | 添加新游戏映射 |
| `wegame-capi-biz/src/main/java/com/wegame/capi/biz/config/domain/NewGameConfig.java` | 新增 | 游戏配置类 |
| Nacos 控制台 | 新增 | `{game}_config.json` 配置文件 |

---

## 重要注意事项

### 1. 命名规范

- **游戏名小写**：配置前缀必须小写，如 `malakas` → `@ConfigurationProperties(prefix = "malakas")`
- **JSON 根节点**：必须与 prefix 一致
- **Data ID 格式**：`wegame-capi-{env}-{game}_config.json`

### 2. 配置校验

- 必须实现 `ConfigValidate` 接口的 `validate()` 方法
- 校验失败时 Nacos 配置不会更新，并发送告警消息
- **校验逻辑设计说明**：每个游戏的配置结构可能不同，校验逻辑应根据游戏原有设计来实现，而非机械照搬模板。标准模板（MalakasConfig）仅供参考，实际开发中需根据具体游戏配置字段进行适配。

### 3. 配置更新

- Nacos 修改配置后，应用会自动刷新（`@RefreshScope` 生效）
- 无需重启应用

### 4. 已有游戏参考

| 游戏 | 分支 | 配置类 | JSON 根节点 |
|------|------|--------|-------------|
| MariaMakiling | mariamakiling_nacos | MariaMakilingConfig | mariamakiling |
| Malakas | malakas_nacos | MalakasConfig | malakas |
| IbongAdarna | - | IbongAdarnaconfig | ibongadarna |
| Bakunawa | - | BakunawaConfig | bakunawa |
---

## 环境列表

需要修改的环境配置文件：

- `src/main/resources/config/local/bootstrap.yml`
- `src/main/resources/config/dev/bootstrap.yml`
- `src/main/resources/config/qa/bootstrap.yml`
- `src/main/resources/config/sts/bootstrap.yml`
- `src/main/resources/config/pre/bootstrap.yml`
- `src/main/resources/config/prod/bootstrap.yml`

## 总结

新游戏接入 Nacos 配置功能的核心步骤：

1. **创建配置类** → 实现 `ConfigValidate`，添加 `@Component`、`@RefreshScope`、`@ConfigurationProperties`
2. **注册到工具类** → 在 `NacosConfigRefresherUtil` 中添加映射
3. **配置 bootstrap.yml** → 添加 extension-configs 和游戏开关
4. **创建 Nacos 配置** → 在控制台创建对应的 JSON 配置文件

---

## 环境列表

需要修改的环境配置文件：

- `src/main/resources/config/local/bootstrap.yml`
- `src/main/resources/config/dev/bootstrap.yml`
- `src/main/resources/config/qa/bootstrap.yml`
- `src/main/resources/config/sts/bootstrap.yml`
- `src/main/resources/config/pre/bootstrap.yml`
- `src/main/resources/config/prod/bootstrap.yml`

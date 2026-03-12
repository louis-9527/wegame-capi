# AGENTS.md - WeGame CAPI Development Guide

## Project Overview

**Spring Boot 2.x Maven multi-module Java project** (Java 1.8) - RESTful APIs for gaming platforms.

### Module Structure

| Module | Purpose |
|--------|---------|
| `wegame-capi-api` | API interfaces, request/response DTOs |
| `wegame-capi-application` | Controllers, main application entry |
| `wegame-capi-biz` | Business logic, services, managers |
| `wegame-capi-client` | Client domain objects, contexts |
| `wegame-capi-common` | Common utilities, exceptions |
| `wegame-capi-dao` | Database access layer |
| `wegame-capi-infrastructure` | Infrastructure configs, aspects |
| `wegame-capi-scheduler` | Scheduled tasks |

---

## Build & Test Commands

### Build
```bash
mvn clean install -DskipTests
mvn clean install -pl wegame-capi-application -am -DskipTests
```

### Run
```bash
mvn spring-boot:run -pl wegame-capi-application
mvn spring-boot:run -pl wegame-capi-application -Dspring-boot.run.profiles=dev
```

### Test
```bash
mvn test
mvn test -Dtest=BaseTest
mvn test -Dtest=MariaMakilingBetRecordManagerTest#testBet
mvn test -pl wegame-capi-application
```

---

## Code Style Guidelines

### 1. Object Naming (DDD)

| Type | Suffix | Example |
|------|--------|---------|
| View Object | `Vo` | `BetResultVo` |
| Domain Object | `Do` | `BetDo` |
| Request | `Req` | `BetRequest` |
| Response | `Res` | `BetRes` |
| Persistent Object | `Po` | `BetPo` |

### 2. Package Structure

```
com.wegame.capi
├── api/           # API interfaces, base classes
├── application/   # Controllers
├── biz/           # Service, manager, converter, domain, module
├── client/        # Context, domain, enums
├── common/        # Exception, annotation
├── dao/           # Database access
├── infrastructure/# Config, aspect
└── scheduler/     # Scheduled tasks
```

### 3. Import Order

**Static imports first, then blank line, then regular imports:**
```java
import static com.wegame.base.config.HttpClientConfig.COMMON_REST_TEMPLATE_BEAN;
import static com.wegame.capi.infrastructure.config.rest.WalletRestTemplateConfig.WALLET_REST_TEMPLATE_BEAN;

import com.wegame.capi.common.annotation.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
```

### 4. Lombok Annotations

**Data classes:**
```java
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseRes implements Serializable { }
```

**Services/Controllers:**
```java
@Slf4j
@Service
public class BetService { }

@Slf4j
@RestController
@RequestMapping("bet")
@Api(value = "投注模块", tags = {"投注模块"})
public class BetController { }
```

### 5. Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `BetService` |
| Methods | camelCase | `bet()` |
| Constants | UPPER_SNAKE_CASE | `SUC_CODE = 200` |
| Variables | camelCase | `betDo` |
| Packages | lowercase | `com.wegame.capi.biz.service` |

### 6. REST API Pattern

```java
@Slf4j
@RestController
@RequestMapping("bet")
@Api(value = "投注模块", tags = {"投注模块"})
public class BetController {
    @Autowired
    BetService betService;

    @LoginRequired
    @ApiOperation(value = "投注", httpMethod = "POST")
    @PostMapping("/bet")
    public CommonResponse<BetResultVo> bet(@RequestBody BetRequest request) {
        request.validate();
        return build(doToVo(betService.bet(requestToDo(request))));
    }
}
```

### 7. Error Handling

```java
AssertUtil.assertNotNull(betDo, ErrorCode.ERR_PARAM);
AssertUtil.assertStrNotEmpty(betContextName, ErrorCode.ERR_PARAM, "Not Support Game");
```

### 8. Service Injection

```java
@Service
public class BetService {
    @Autowired
    Map<String, AbstractBetContext> contextMap;
}
```

---

## Test Conventions

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(
    type = FilterType.REGEX, 
    pattern = "com.wegame.capi.scheduler.*"
))
public class BaseTest {
    public static final String TEST_PLAYER_ID = "IgoTest10020";
}
```

---

## Environment Profiles

`local`, `dev`, `qa`, `sts`, `pre`, `prod`

```bash
mvn clean install -Pqa -DskipTests
```

---

## API Docs

- **Local**: http://localhost:9000/capi/doc.html
- **QA**: http://client.sportslive.com.ph:9000/capi/doc.html

---

## Key Dependencies

Spring Boot 2.x, MySQL/MariaDB, MongoDB, Apache Pulsar, Elasticsearch, Nacos, Lombok, Swagger

---

## Notes

1. **Always use Maven** - Do not use Gradle
2. **Java 1.8** - Ensure Java 8 compatibility
3. **Parent POM** - Dependencies from `wegame-parent`
4. **Static converters** - Use `Converter` classes with static import
5. **Game modules** - Each game (MariaMakiling, Malakas, IbongAdarna) under `biz/module/`

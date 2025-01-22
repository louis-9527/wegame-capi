



todo：


游戏：模版方法设计模式
游戏结果：工厂/建造者模式


**包结构说明**：
com.wegame.capi
    advice: 增强（统一异常处理）
    cache:  缓存
    client:  客户端
    common:  通用
        annotation：注解
        builder：模型构建
        converter：模型转换
        domain：模型
            context：上下文模型
            request：req模型
            response：res模型
            vo：vo模型
        enums：枚举
        utils：工具
        Constants.java 常量
    config: 自动化配置类（后面会考虑挪动到wegamebase包中）
    controller: 控制层
    filter: 过滤器（eg：UserInfoContextFilter.java构建用户上下文）
    interceptor: 拦截器(eg: 权限控制)
    mapper: db操作mapper
        auto：自动生产的mapper
        domain：持久层domain
        manual：手动编写的mapper
    scheduler: 定时任务
    service: 服务层
    Application.java 启动类


对象命名规则：

Vo（View Object）：视图对象，用于展示层，主要用于页面展示以及将页面的数据传送给控制器。
Do（Domain Object/Data Object）：领域对象，是对现实生活中的抽象对象
Dto（Data Transfer Object）：数据传输对象
Po（Persistent Object）：持久化对象，它和数据库表结构一致。
Bo（Business Object）：业务对象，可以由Service层输出的封装业务逻辑的对象。

本地环境接口文档地址：
http://localhost:9000/capi/doc.html

测试环境接口文档地址：
http://client.sportslive.com.ph:9000/capi/doc.html



线上请求链路：
客户端 -> cloudfront - > load balancer -> apisix (网关) -> 应用程序pods
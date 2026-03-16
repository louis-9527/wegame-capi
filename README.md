
基于分支打rtag：
1、git tag -a qat/v1.0.0 -m "qa version"
2、git push origin qat/v1.0.0



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

备注：

1、 定时任务：schedule模块（alert、backend、biz、operation、startcard）
2、 mongodb配置：infrastructure模块
3、 listener：biz模块listener（betRecord、operation）
4、 pulsar配置：biz模块config（xxxPulsarConfig、OperationEventPulsarConfig）



线上请求链路：
压测机 -> cloudfront - > load balancer -> apisix (网关) -> 应用程序pods


游戏主R：
    MariaMakiling: zhumeng001 maidong
    Malakas: zhumeng002 maidong
    IbongAdarna: zhumeng003 jayni
    Lamang: zhumeng004 lyric
    Bakunawa: zhumeng005 maidong
    DiwataMayari: zhumeng006 jayni
    Kapre: zhumeng007 jayni
    Mangkukulam: zhumeng008 lyric
    Sirena: zhumeng009 lyric
    Atlantis: zhumeng010 lyric
    Bayanihan: zhumeng011 jayni
    Idol: zhumeng012 maidong
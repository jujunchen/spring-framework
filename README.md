## Spring 框架源码解读
目标是解读并注释Spring框架的每一行代码。  
### 如果你感兴趣请加入进来，让我们来品读Spring的每一行代码
## 构建环境
https://itsaysay.blog.csdn.net/article/details/135758134
## 进度
|模块|进度|描述|
|-|-|-|
|spring-aop|0%|要使用AOP相关的功能(事务、切面)需要包含该模块|
|spring-aspects|0%|包含AspectJ AOP库进行高级集成的所有类|
|spring-beans|0%|包含对Spring bean进行操作的类|
|spring-beans-groovy|0%|对Spring bean进行操作的Groovy类|
|spring-context|0%|包含Spring Core提供的许多扩展类，如ApplicationContext|
|spring-context-indexer|0%|包含一个索引器实现，<br>它提供对META-INF/spring.components 中定义的候选项的访问功能，但核心类CandidateComponentsIndex 并不能再外部使用|
|spring-context-support|0%|该模块是对spring-context模块的进一步扩展，在用户界面方面，有一些用于支持邮件并与模块引擎集成的类，还包括与各种任务执行和调度库（CommonJ和Quartz）的集成|
|spring-core|0%|主要模块，其他Spring模块都会依赖该模块|
|spring-expression|0%|包含SpEL表达式的支持类|
|spring-instrument|0%|包含用于JVM启动的Spring工具代理，如果在Spring应用程序中使用AspectJ实现加载织入，那么该模块是必需的|
|spring-jdbc|0%|包含所有的JDBC支持类|
|spring-jms|0%|所有JMS支持类|
|spring-messaging|0%|提供消息传递的基础结构和协议|
|spring-orm|0%|扩展了Spring的标准JDBC功能集，支持流行的ORM工具，包含Hibernate、JDO、JPA和数据映射器IBATIS。该JAR文件中的许多类都依赖于spring-jdbc JAR文件中所包含的类，因此也需要把它包含在程序中|
|spring-oxm|0%|为Object/XML映射OXM提供支持，用于抽象XML编组和解组以及支持Castor、JAXB、XMLBeans和XStream等常用工具的类都包含在此模块中|
|spring-test|0%|Spring提供的帮助测试程序的包|
|spring-tx|80%|提供支持Spring事务的所有类|
|spring-web|0%|包含Web程序中使用的所需核心类|
|spring-web-reactive|0%|响应式模型的核心接口和类|
|spring-webmvc|0%|Spring自己的MVC框架|
|spring-websocket|0%|Spring对WebSocket的支持类|
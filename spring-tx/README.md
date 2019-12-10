## 事务

### 1. 事务中的几个主要对象

1. TransactionDefinition

    定义了与Spring兼容的事务属性，有事务传播方式、隔离级别、超时时间、是否只读事务等控制事务具体行为的事务属性

2. TransactionStatus

    表示一个事务的具体运行状态。事务管理器可以通过该接口获取事务运行期的状态信息，也可以 通过该接口以编程的方式回滚事务，该接口继承了SavepointManager接口，以提供事务保存点功能，SavepointManager接口提供了编程方式管理事务保存点的通用方法

3. PlatformTransactionManager

    事务管理器，根据TransactionDefinition提供的事务属性配置信息创建事务，并用TransactionStatus描述这个激活事务的状态(提交、回滚)。

4. TransactionSynchronizationManager 
 
    事务同步管理器，使用ThreadLocal为不同的事务线程提供了独立的资源副本，使Spring的事务变成线程安全，同时提供事务配置的属性和运行状态信息
    
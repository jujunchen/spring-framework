J2EE提供JCA（Java Connector Architecture）规范来标准化对EIS（Enterprise Information System）的访问。这个规范被分为几个不同的部分： 

　　**SPI（Service provider interfaces）**是连接器提供者（connector provider）必须实现的接口。 这些接口组成了一个能被部署在J2EE应用服务器上的资源适配器（resource adapter）。 在这种情况下，由服务器来管理连接池（connection pooling）、事务和安全（托管模式（managed mode））。 应用服务器还负责管理客户端应用程序之外所拥有的配置。连接器（connector）同样能在脱离应用服务器的情况下使用；在这种情况下，应用程序必须直接对它进行配置（非托管模式（non-managed mode））。 

　　**CCI （Common Client Interface）**是应用程序用来与连接器交互并与EIS通信的接口。同样还为本地事务划界提供了API。 

　　Spring对CCI的支持，目的是为了提供以典型的Spring方式来访问CCI连接器的类，并有效地使用Spring的通用资源和事务管理工具。 

`注意：`   
连接器的客户端不必总是使用CCI。 某些连接器暴露它们自己的API，只提供JCA资源适配器（resource adapter） 以使用J2EE容器的某些系统契约（system contracts）（连接池（connection pooling），全局事务（global transactions），安全（security））。 Spring并没有为这类连接器特有（connector-specific）的API提供特殊的支持。
/**
 * The classes in this package represent RDBMS queries, updates,
 * and stored procedures as threadsafe, reusable objects. This approach
 * is modelled by JDO, although of course objects returned by queries
 * are "disconnected" from the database.
 * <br>
 *	此软件包中的类将RDBMS查询，更新和存储过程表示为线程安全的可重用对象。
 *	这种方法是由JDO建模的，尽管查询返回的对象当然是与数据库“断开连接”的。
 *
 * <p>This higher level of JDBC abstraction depends on the lower-level
 * abstraction in the {@code org.springframework.jdbc.core} package.
 * Exceptions thrown are as in the {@code org.springframework.dao} package,
 * meaning that code using this package does not need to implement JDBC or
 * RDBMS-specific error handling.
 * <br>
 *     较高级别的JDBC抽象取决于{@code org.springframework.jdbc.core}软件包中的较低级别的抽象。
 *     引发的异常与{@code org.springframework.dao}包中的一样，这意味着使用此包的代码不需要实现JDBC或RDBMS特定的错误处理。
 *
 * <p>This package and related packages are discussed in Chapter 9 of
 * <a href="https://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 */
@NonNullApi
@NonNullFields
package org.springframework.jdbc.object;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;

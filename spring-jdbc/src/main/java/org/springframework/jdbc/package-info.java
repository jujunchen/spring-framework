/**
 * The classes in this package make JDBC easier to use and
 * reduce the likelihood of common errors. In particular, they:
 * <br>
 *     该软件包中的类使JDBC易于使用，并减少了常见错误的可能性。特别是，他们：
 * <ul>
 * <li>Simplify error handling, avoiding the need for try/catch/finally
 * blocks in application code.
 * <br>
 *     简化错误处理，避免在应用程序代码中需要try / catch / finally块。
 * <li>Present exceptions to application code in a generic hierarchy of
 * unchecked exceptions, enabling applications to catch data access
 * exceptions without being dependent on JDBC, and to ignore fatal
 * exceptions there is no value in catching.
 * <br>
 *     在未经检查的异常的通用层次结构中呈现应用程序代码的异常，
 *     使应用程序能够在不依赖JDBC的情况下捕获数据访问异常，并忽略致命异常，因此捕获毫无价值。
 * <li>Allow the implementation of error handling to be modified
 * to target different RDBMSes without introducing proprietary
 * dependencies into application code.
 * <br>
 *     允许修改错误处理的实现以针对不同的RDBMS，而无需在应用程序代码中引入专有的依赖关系
 * </ul>
 *
 * <p>This package and related packages are discussed in Chapter 9 of
 * <a href="https://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).x
 */
@NonNullApi
@NonNullFields
package org.springframework.jdbc;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;

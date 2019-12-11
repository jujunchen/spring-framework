/**
 * Exception hierarchy enabling sophisticated error handling independent
 * of the data access approach in use. For example, when DAOs and data
 * access frameworks use the exceptions in this package (and custom
 * subclasses), calling code can detect and handle common problems such
 * as deadlocks without being tied to a particular data access strategy,
 * such as JDBC.
 *
 * <p>All these exceptions are unchecked, meaning that calling code can
 * leave them uncaught and treat all data access exceptions as fatal.
 *
 * <p>The classes in this package are discussed in Chapter 9 of
 * <a href="https://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 *
 * <p>
 *     异常层次结构使复杂的错误处理独立于所使用的数据访问方法。例如，当DAO和数据访问框架使用此程序包（和自定义子类）中的异常时，调用代码可以检测和处理常见问题（例如死锁），而无需绑定到诸如JDBC之类的特定数据访问策略。
 * 所有这些异常都是未经检查的，这意味着调用代码可以使其未被捕获，并将所有数据访问异常视为致命的。
 * Rod Johnson（Wrox，2002）在“Expert One-On-One J2EE Design and Development”的第9章中讨论了此软件包中的类。
 * </p>
 */
@NonNullApi
@NonNullFields
package org.springframework.dao;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;

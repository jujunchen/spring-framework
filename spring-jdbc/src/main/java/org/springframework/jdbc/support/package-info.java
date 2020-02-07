/**
 * Support classes for the JDBC framework, used by the classes in the
 * jdbc.core and jdbc.object packages. Provides a translator from
 * SQLExceptions Spring's generic DataAccessExceptions.
 *	<br>
 *	   JDBC框架的支持类，由jdbc.core和jdbc.object包中的类使用。
 *	   提供SQLExceptions Spring的通用DataAccessExceptions的转换器。
 *
 * <p>Can be used independently, for example in custom JDBC access code,
 * or in JDBC-based O/R mapping layers.
 * <br>
 *     可以独立使用，例如在自定义JDBC访问代码中或在基于JDBC的O / R映射层中使用。
 */
@NonNullApi
@NonNullFields
package org.springframework.jdbc.support;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;

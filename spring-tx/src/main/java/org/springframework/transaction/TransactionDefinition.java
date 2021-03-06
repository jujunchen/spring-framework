/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.transaction;

import org.springframework.lang.Nullable;

/**
 * Interface that defines Spring-compliant transaction properties.
 * Based on the propagation behavior definitions analogous to EJB CMT attributes.
 *
 * <p>Note that isolation level and timeout settings will not get applied unless
 * an actual new transaction gets started. As only {@link #PROPAGATION_REQUIRED},
 * {@link #PROPAGATION_REQUIRES_NEW} and {@link #PROPAGATION_NESTED} can cause
 * that, it usually doesn't make sense to specify those settings in other cases.
 * Furthermore, be aware that not all transaction managers will support those
 * advanced features and thus might throw corresponding exceptions when given
 * non-default values.
 *
 * <p>The {@link #isReadOnly() read-only flag} applies to any transaction context,
 * whether backed by an actual resource transaction or operating non-transactionally
 * at the resource level. In the latter case, the flag will only apply to managed
 * resources within the application, such as a Hibernate {@code Session}.
 *
 * <p>
 *     与Spring兼容的事务属性接口。类似EJB CMT属性
 * <p>
 *     注意：除非开启新事务，否则不会应用隔离级别和超时设置。只有 {@link #PROPAGATION_REQUIRED},
 *   {@link #PROPAGATION_REQUIRES_NEW} 和 {@link #PROPAGATION_NESTED} 能开启新事务，因此在其他情况下设置是没有意义的。
 *   此外，并非所有的事务管理器都支持这种高级功能，在没有默认值时会引发异常
 * <p>
 *     {@link #isReadOnly()} 只读标志适用于任何事务上下文，不管实际事务，还是非事务方式。非事务方式，仅适用于由应用托管的资源，
 *     比如Hibernate {@code Session}
 *
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 * @see PlatformTransactionManager#getTransaction(TransactionDefinition)
 * @see org.springframework.transaction.support.DefaultTransactionDefinition
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 */
public interface TransactionDefinition {

	/**
	 * Support a current transaction; create a new one if none exists.
	 * Analogous to the EJB transaction attribute of the same name.
	 * <p>This is typically the default setting of a transaction definition,
	 * and typically defines a transaction synchronization scope.
	 * <p>
	 *     如果当前没有事务，则新建一个事务；如果已经存在一个事务，则加入到这个事务中
	 * </p>
	 */
	int PROPAGATION_REQUIRED = 0;

	/**
	 * Support a current transaction; execute non-transactionally if none exists.
	 * Analogous to the EJB transaction attribute of the same name.
	 * <p><b>NOTE:</b> For transaction managers with transaction synchronization,
	 * {@code PROPAGATION_SUPPORTS} is slightly different from no transaction
	 * at all, as it defines a transaction scope that synchronization might apply to.
	 * As a consequence, the same resources (a JDBC {@code Connection}, a
	 * Hibernate {@code Session}, etc) will be shared for the entire specified
	 * scope. Note that the exact behavior depends on the actual synchronization
	 * configuration of the transaction manager!
	 * <p>In general, use {@code PROPAGATION_SUPPORTS} with care! In particular, do
	 * not rely on {@code PROPAGATION_REQUIRED} or {@code PROPAGATION_REQUIRES_NEW}
	 * <i>within</i> a {@code PROPAGATION_SUPPORTS} scope (which may lead to
	 * synchronization conflicts at runtime). If such nesting is unavoidable, make sure
	 * to configure your transaction manager appropriately (typically switching to
	 * "synchronization on actual transaction").
	 * <p>
	 *     使用当前事务，如果没有事务，则以非事务方式执行
	 * </p>
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#SYNCHRONIZATION_ON_ACTUAL_TRANSACTION
	 */
	int PROPAGATION_SUPPORTS = 1;

	/**
	 * Support a current transaction; throw an exception if no current transaction
	 * exists. Analogous to the EJB transaction attribute of the same name.
	 * <p>Note that transaction synchronization within a {@code PROPAGATION_MANDATORY}
	 * scope will always be driven by the surrounding transaction.
	 * <p>
	 *     如果当前有事物存在，就使用当前事务。当前没有事务，就抛错
	 * </p>
	 */
	int PROPAGATION_MANDATORY = 2;

	/**
	 * Create a new transaction, suspending the current transaction if one exists.
	 * Analogous to the EJB transaction attribute of the same name.
	 * <p><b>NOTE:</b> Actual transaction suspension will not work out-of-the-box
	 * on all transaction managers. This in particular applies to
	 * {@link org.springframework.transaction.jta.JtaTransactionManager},
	 * which requires the {@code javax.transaction.TransactionManager} to be
	 * made available it to it (which is server-specific in standard Java EE).
	 * <p>A {@code PROPAGATION_REQUIRES_NEW} scope always defines its own
	 * transaction synchronizations. Existing synchronizations will be suspended
	 * and resumed appropriately.
	 * <p>
	 *     开启新事务，如果当前存在事务，则挂起当前事务
	 * </p>
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	int PROPAGATION_REQUIRES_NEW = 3;

	/**
	 * Do not support a current transaction; rather always execute non-transactionally.
	 * Analogous to the EJB transaction attribute of the same name.
	 * <p><b>NOTE:</b> Actual transaction suspension will not work out-of-the-box
	 * on all transaction managers. This in particular applies to
	 * {@link org.springframework.transaction.jta.JtaTransactionManager},
	 * which requires the {@code javax.transaction.TransactionManager} to be
	 * made available it to it (which is server-specific in standard Java EE).
	 * <p>Note that transaction synchronization is <i>not</i> available within a
	 * {@code PROPAGATION_NOT_SUPPORTED} scope. Existing synchronizations
	 * will be suspended and resumed appropriately.
	 * <p>
	 *     以非事务方式执行，如果当前存在事务，则把当前事务挂起
	 * </p>
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	int PROPAGATION_NOT_SUPPORTED = 4;

	/**
	 * Do not support a current transaction; throw an exception if a current transaction
	 * exists. Analogous to the EJB transaction attribute of the same name.
	 * <p>Note that transaction synchronization is <i>not</i> available within a
	 * {@code PROPAGATION_NEVER} scope.
	 * <p>
	 *     以非事务方式执行，如果当前存在事务则抛错
	 * </p>
	 */
	int PROPAGATION_NEVER = 5;

	/**
	 * Execute within a nested transaction if a current transaction exists,
	 * behave like {@link #PROPAGATION_REQUIRED} otherwise. There is no
	 * analogous feature in EJB.
	 * <p><b>NOTE:</b> Actual creation of a nested transaction will only work on
	 * specific transaction managers. Out of the box, this only applies to the JDBC
	 * {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
	 * when working on a JDBC 3.0 driver. Some JTA providers might support
	 * nested transactions as well.
	 * <p>
	 *     如果当前存在事务，则在嵌套事务中执行。如果没有事务，就执行与{@link #PROPAGATION_REQUIRED}类似的操作
	 *     <br>
	 *         内部事务的回滚不会对外部事务造成影响。只对{@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
	 *         事务管理器有效
	 * </p>
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
	 */
	int PROPAGATION_NESTED = 6;


	/**
	 * Use the default isolation level of the underlying datastore.
	 * All other levels correspond to the JDBC isolation levels.
	 * <p>默认隔离级别。隔离级别对应JDBC的隔离级别</p>
	 * @see java.sql.Connection
	 */
	int ISOLATION_DEFAULT = -1;

	/**
	 * Indicates that dirty reads, non-repeatable reads and phantom reads
	 * can occur.
	 * <br>
	 *     可能发生脏读，不可重复读和幻读
	 * <p>This level allows a row changed by one transaction to be read by another
	 * transaction before any changes in that row have been committed (a "dirty read").
	 * If any of the changes are rolled back, the second transaction will have
	 * retrieved an invalid row.
	 * <br>
	 *     此级别允许在提交该行中的任何更改之前，由一个事务更改的行被另一事务读取（“脏读”）。
	 *     如果任何更改被回滚，则第二个事务将检索到无效的行。
	 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
	 */
	// same as java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
	int ISOLATION_READ_UNCOMMITTED = 1;

	/**
	 * Indicates that dirty reads are prevented; non-repeatable reads and
	 * phantom reads can occur.
	 * <br>
	 *     避免脏读；允许不可重复读和幻读
	 * <p>This level only prohibits a transaction from reading a row
	 * with uncommitted changes in it.
	 * <br>
	 *     该级别禁止事务读取未提交的更改
	 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
	 */
	// same as java.sql.Connection.TRANSACTION_READ_COMMITTED;
	int ISOLATION_READ_COMMITTED = 2;

	/**
	 * Indicates that dirty reads and non-repeatable reads are prevented;
	 * phantom reads can occur.
	 * <br>
	 *     不允许脏读，不可重复读，允许幻读
	 * <p>This level prohibits a transaction from reading a row with uncommitted changes
	 * in it, and it also prohibits the situation where one transaction reads a row,
	 * a second transaction alters the row, and the first transaction re-reads the row,
	 * getting different values the second time (a "non-repeatable read").
	 * <br>
	 *     此级别禁止事务读取其中未提交更改的行，
	 *     并且还禁止以下情况：一个事务读取一行，第二个事务更改该行，第一个事务重新读取该行，第二次获得不同的值（“不可重复读取”）。
	 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
	 */
	// same as java.sql.Connection.TRANSACTION_REPEATABLE_READ;
	int ISOLATION_REPEATABLE_READ = 4;

	/**
	 * Indicates that dirty reads, non-repeatable reads and phantom reads
	 * are prevented.
	 * <br>
	 *     防止脏读，不可重复读和幻读
	 * <p>This level includes the prohibitions in {@link #ISOLATION_REPEATABLE_READ}
	 * and further prohibits the situation where one transaction reads all rows that
	 * satisfy a {@code WHERE} condition, a second transaction inserts a row
	 * that satisfies that {@code WHERE} condition, and the first transaction
	 * re-reads for the same condition, retrieving the additional "phantom" row
	 * in the second read.
	 * <br>
	 *     事务只能一个个执行，执行速度慢
	 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
	 */
	// same as java.sql.Connection.TRANSACTION_SERIALIZABLE;
	int ISOLATION_SERIALIZABLE = 8;


	/**
	 * Use the default timeout of the underlying transaction system,
	 * or none if timeouts are not supported.
	 * <p>
	 *     事务默认超时时间，如果不支持就不使用默认超时
	 * </p>
	 */
	int TIMEOUT_DEFAULT = -1;


	/**
	 * Return the propagation behavior.
	 * <p>Must return one of the {@code PROPAGATION_XXX} constants
	 * defined on {@link TransactionDefinition this interface}.
	 * <p>
	 *     返回事务传播方式
	 * <p>
	 *     必须返回{@link TransactionDefinition}接口中定义的{@code PROPAGATION_XXX}中的其中一个
	 * @return the propagation behavior <br>返回事务传播方式
	 * @see #PROPAGATION_REQUIRED
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isActualTransactionActive()
	 */
	default int getPropagationBehavior() {
		return PROPAGATION_REQUIRED;
	}

	/**
	 * Return the isolation level.
	 * <p>Must return one of the {@code ISOLATION_XXX} constants defined on
	 * {@link TransactionDefinition this interface}. Those constants are designed
	 * to match the values of the same constants on {@link java.sql.Connection}.
	 * <p>Exclusively designed for use with {@link #PROPAGATION_REQUIRED} or
	 * {@link #PROPAGATION_REQUIRES_NEW} since it only applies to newly started
	 * transactions. Consider switching the "validateExistingTransactions" flag to
	 * "true" on your transaction manager if you'd like isolation level declarations
	 * to get rejected when participating in an existing transaction with a different
	 * isolation level.
	 * <p>The default is {@link #ISOLATION_DEFAULT}. Note that a transaction manager
	 * that does not support custom isolation levels will throw an exception when
	 * given any other level than {@link #ISOLATION_DEFAULT}.
	 * <p>
	 *     返回事务隔离级别。
	 * <p>
	 *     必须返回在{@link TransactionDefinition}接口中定义的{@code ISOLATION_XXX}中的一个。
	 * <p>
	 *     只有与{@link #PROPAGATION_REQUIRED}或者{@link #PROPAGATION_REQUIRES_NEW}结合才有意义。
	 * <p>
	 *     注意：如果事务管理器不支持隔离级别，那么只有设置{@link #ISOLATION_DEFAULT}，其他隔离级别都将抛出异常。
	 * @return the isolation level
	 * @see #ISOLATION_DEFAULT
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
	 */
	default int getIsolationLevel() {
		return ISOLATION_DEFAULT;
	}

	/**
	 * Return the transaction timeout.
	 * <p>Must return a number of seconds, or {@link #TIMEOUT_DEFAULT}.
	 * <p>Exclusively designed for use with {@link #PROPAGATION_REQUIRED} or
	 * {@link #PROPAGATION_REQUIRES_NEW} since it only applies to newly started
	 * transactions.
	 * <p>Note that a transaction manager that does not support timeouts will throw
	 * an exception when given any other timeout than {@link #TIMEOUT_DEFAULT}.
	 * <p>The default is {@link #TIMEOUT_DEFAULT}.
	 * <p>
	 *     返回事务超时时间。
	 * <p>
	 *     必须返回几秒钟，或者使用{@link #TIMEOUT_DEFAULT}
	 * <p>
	 *     仅仅与{@link #PROPAGATION_REQUIRED} 或者 {@link #PROPAGATION_REQUIRES_NEW} 结合才有意义。
	 * <p>
	 *     注意：如果事务管理器不支持超时，在设置为除{@link #TIMEOUT_DEFAULT}以外的时间时，将抛出异常
	 * @return the transaction timeout
	 */
	default int getTimeout() {
		return TIMEOUT_DEFAULT;
	}

	/**
	 * Return whether to optimize as a read-only transaction.
	 * <p>The read-only flag applies to any transaction context, whether backed
	 * by an actual resource transaction ({@link #PROPAGATION_REQUIRED}/
	 * {@link #PROPAGATION_REQUIRES_NEW}) or operating non-transactionally at
	 * the resource level ({@link #PROPAGATION_SUPPORTS}). In the latter case,
	 * the flag will only apply to managed resources within the application,
	 * such as a Hibernate {@code Session}.
	 * <p>This just serves as a hint for the actual transaction subsystem;
	 * it will <i>not necessarily</i> cause failure of write access attempts.
	 * A transaction manager which cannot interpret the read-only hint will
	 * <i>not</i> throw an exception when asked for a read-only transaction.
	 * <p>
	 *     返回事务是否为只读。
	 * <p>
	 *     只读标志适用于任何事务上下文。
	 * @return {@code true} if the transaction is to be optimized as read-only
	 * ({@code false} by default)<br>如果为只读，返回true，默认为false
	 * @see org.springframework.transaction.support.TransactionSynchronization#beforeCommit(boolean)
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
	 */
	default boolean isReadOnly() {
		return false;
	}

	/**
	 * Return the name of this transaction. Can be {@code null}.
	 * <p>This will be used as the transaction name to be shown in a
	 * transaction monitor, if applicable (for example, WebLogic's).
	 * <p>In case of Spring's declarative transactions, the exposed name will be
	 * the {@code fully-qualified class name + "." + method name} (by default).
	 * <p>
	 *     返回这个事务的名称，可以为null，用做事务监视器的名称。
	 * <p>
	 *     默认情况下，Spring声明式事务，公开的名称为全限定名
	 * @return the name of this transaction ({@code null} by default}
	 * @see org.springframework.transaction.interceptor.TransactionAspectSupport
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#getCurrentTransactionName()
	 */
	@Nullable
	default String getName() {
		return null;
	}


	// Static builder methods

	/**
	 * Return an unmodifiable {@code TransactionDefinition} with defaults.
	 * <p>For customization purposes, use the modifiable
	 * {@link org.springframework.transaction.support.DefaultTransactionDefinition}
	 * instead.
	 * @since 5.2
	 */
	static TransactionDefinition withDefaults() {
		return StaticTransactionDefinition.INSTANCE;
	}

}

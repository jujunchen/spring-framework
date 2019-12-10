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
 * This is the central interface in Spring's transaction infrastructure.
 * Applications can use this directly, but it is not primarily meant as API:
 * Typically, applications will work with either TransactionTemplate or
 * declarative transaction demarcation through AOP.
 *
 * <p>For implementors, it is recommended to derive from the provided
 * {@link org.springframework.transaction.support.AbstractPlatformTransactionManager}
 * class, which pre-implements the defined propagation behavior and takes care
 * of transaction synchronization handling. Subclasses have to implement
 * template methods for specific states of the underlying transaction,
 * for example: begin, suspend, resume, commit.
 *
 * <p>The default implementations of this strategy interface are
 * {@link org.springframework.transaction.jta.JtaTransactionManager} and
 * {@link org.springframework.jdbc.datasource.DataSourceTransactionManager},
 * which can serve as an implementation guide for other transaction strategies.
 *
 * <p>
 *     这是Spring事务基础架构中的中央接口。应用可以直接使用，但它并不是主要用于API：
 *     通常，应用将通过AOP与TransactionTemplate或声明式事物一起使用
 * <p>
 *     对于实现，建议继承 {@link org.springframework.transaction.support.AbstractPlatformTransactionManager}类，
 *     该类可预先实现定义的传播行为并负责事物同步处理。子类必须为基础事务的特定状态实现模版方法，例如：begin,suspend,resume,commit
 * <p>
 *     这个接口的默认实现，{@link org.springframework.transaction.jta.JtaTransactionManager} 和
 *  * {@link org.springframework.jdbc.datasource.DataSourceTransactionManager},可以做为其他事务策略的实现指导
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16.05.2003
 * @see org.springframework.transaction.support.TransactionTemplate
 * @see org.springframework.transaction.interceptor.TransactionInterceptor
 */
public interface PlatformTransactionManager extends TransactionManager {

	/**
	 * Return a currently active transaction or create a new one, according to
	 * the specified propagation behavior.
	 * <p>Note that parameters like isolation level or timeout will only be applied
	 * to new transactions, and thus be ignored when participating in active ones.
	 * <p>Furthermore, not all transaction definition settings will be supported
	 * by every transaction manager: A proper transaction manager implementation
	 * should throw an exception when unsupported settings are encountered.
	 * <p>An exception to the above rule is the read-only flag, which should be
	 * ignored if no explicit read-only mode is supported. Essentially, the
	 * read-only flag is just a hint for potential optimization.
	 * <p>
	 *     根据指定的传播行为，返回已存在的事务或创建新的事务。
	 * 请注意，诸如隔离级别或超时之类的参数将仅应用于新事务，因此在参与活动事务时将被忽略。
	 * 此外，并非每个事务管理器都支持所有事务定义设置：当遇到不受支持的设置时，正确的事务管理器实现应引发异常。
	 * 上述规则的一个例外是只读标志，如果不支持显式只读模式，则应忽略该标志。本质上，只读标志只是潜在优化的提示。
	 * </p>
	 * @param definition the TransactionDefinition instance (can be {@code null} for defaults),
	 * describing propagation behavior, isolation level, timeout etc.<br>TransactionDefinition实例
	 * @return transaction status object representing the new or current transaction<br>返回当前事务或者新事务的事务状态对象
	 * @throws TransactionException in case of lookup, creation, or system errors <br>
	 *     如果查找、创建、系统错误抛出TransactionException
	 * @throws IllegalTransactionStateException if the given transaction definition
	 * cannot be executed (for example, if a currently active transaction is in
	 * conflict with the specified propagation behavior)<br>
	 *     如果不能执行给定的事务定义（比如，当前活动的事务与指定的传播行为冲突）
	 * @see TransactionDefinition#getPropagationBehavior
	 * @see TransactionDefinition#getIsolationLevel
	 * @see TransactionDefinition#getTimeout
	 * @see TransactionDefinition#isReadOnly
	 */
	TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
			throws TransactionException;

	/**
	 * Commit the given transaction, with regard to its status. If the transaction
	 * has been marked rollback-only programmatically, perform a rollback.
	 * <p>If the transaction wasn't a new one, omit the commit for proper
	 * participation in the surrounding transaction. If a previous transaction
	 * has been suspended to be able to create a new one, resume the previous
	 * transaction after committing the new one.
	 * <p>Note that when the commit call completes, no matter if normally or
	 * throwing an exception, the transaction must be fully completed and
	 * cleaned up. No rollback call should be expected in such a case.
	 * <p>If this method throws an exception other than a TransactionException,
	 * then some before-commit error caused the commit attempt to fail. For
	 * example, an O/R Mapping tool might have tried to flush changes to the
	 * database right before commit, with the resulting DataAccessException
	 * causing the transaction to fail. The original exception will be
	 * propagated to the caller of this commit method in such a case.
	 * <p>
	 *     根据事务状态提交事务。如果事务状态已经被标记为rollback-only,则该方法将执行一个回滚事务的操作。
	 * <p>
	 *     如果该事务不是新事务，加入到其他事务中会忽略提交。
	 *     如果为了创建新事务之前的事务被暂停，那么在提交新事务后将恢复上一个事务。
	 * <p>
	 *     注意：当提交调用完成时，无论是正常还是发生异常，事务都必须完成并清理。在提交完成时，不应该再调用回滚操作。
	 * <p>
	 *     如果此方法发生TransactionException以外的异常，在某些提交前发生的错误将导致此次提交失败。
	 * <p>
	 *    例如：一个O/R 映射工具可能已经尝试在提交之前立即刷新对数据库的更改，结果DataAccessException导致事务失败，
	 *    在这种情况下，原始异常将传播到此commit方法的调用方
	 * @param status object returned by the {@code getTransaction} method <br> getTransaction方法返回的事务状态
	 * @throws TransactionException
	 * @throws UnexpectedRollbackException in case of an unexpected rollback
	 * that the transaction coordinator initiated <br>如果事务管理器发生了意外回滚，抛出UnexpectedRollbackException
	 * @throws HeuristicCompletionException in case of a transaction failure
	 * caused by a heuristic decision on the side of the transaction coordinator<br>
	 *     如果由事务管理器启发式决策导致的事务失败，抛出HeuristicCompletionException
	 * @throws TransactionSystemException in case of commit or system errors
	 * (typically caused by fundamental resource failures)<br>由基础资源失败引发的提交或者系统错误，抛出TransactionSystemException
	 * @throws IllegalTransactionStateException if the given transaction
	 * is already completed (that is, committed or rolled back)<br>
	 *     事务已经提交或者回滚，抛出IllegalTransactionStateException
	 * @see TransactionStatus#setRollbackOnly
	 */
	void commit(TransactionStatus status) throws TransactionException;

	/**
	 * Perform a rollback of the given transaction.
	 * <p>If the transaction wasn't a new one, just set it rollback-only for proper
	 * participation in the surrounding transaction. If a previous transaction
	 * has been suspended to be able to create a new one, resume the previous
	 * transaction after rolling back the new one.
	 * <p><b>Do not call rollback on a transaction if commit threw an exception.</b>
	 * The transaction will already have been completed and cleaned up when commit
	 * returns, even in case of a commit exception. Consequently, a rollback call
	 * after commit failure will lead to an IllegalTransactionStateException.
	 * <P>
	 *     将指定的事务回滚。
	 * <P>
	 *     如果事务不是新事务，则也需要设置 rollback-only，以能够正确加入其他事务中。如果先前有事物因为创建事务而被暂停，
	 *     则在回滚事务后恢复先前的事务。
	 * <p>
	 *     在提交事务发生异常时，不要在事务上调用回滚。该事务会在调用返回时已经完成并清理，因此将导致IllegalTransactionStateException
	 * </p>
	 * @param status object returned by the {@code getTransaction} method
	 * @throws TransactionSystemException in case of rollback or system errors
	 * (typically caused by fundamental resource failures)
	 * @throws IllegalTransactionStateException if the given transaction
	 * is already completed (that is, committed or rolled back)
	 * @throws TransactionException
	 */
	void rollback(TransactionStatus status) throws TransactionException;

}

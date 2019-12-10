/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.transaction.support;

import java.io.Flushable;

/**
 * Interface for transaction synchronization callbacks.
 * Supported by AbstractPlatformTransactionManager.
 *
 * <p>TransactionSynchronization implementations can implement the Ordered interface
 * to influence their execution order. A synchronization that does not implement the
 * Ordered interface is appended to the end of the synchronization chain.
 *
 * <p>System synchronizations performed by Spring itself use specific order values,
 * allowing for fine-grained interaction with their execution order (if necessary).
 *
 * <p>
 *     事务同步回调的接口。AbstractPlatformTransactionManager支持。
 *     Spring本身执行的系统同步使用指定的顺序值，从而允许与其执行顺序进行细粒度的交互（如有必要）。
 * <p>
 *     TransactionSynchronization的实现可以实现Ordered接口以影响其执行顺序。未实现Ordered接口的同步将附加到同步链的末尾。
 *
 * @author Juergen Hoeller
 * @since 02.06.2003
 * @see TransactionSynchronizationManager
 * @see AbstractPlatformTransactionManager
 * @see org.springframework.jdbc.datasource.DataSourceUtils#CONNECTION_SYNCHRONIZATION_ORDER
 */
public interface TransactionSynchronization extends Flushable {

	/** Completion status in case of proper commit */
	/**
	 * 正确提交的完成状态
	 */
	int STATUS_COMMITTED = 0;

	/** Completion status in case of proper rollback */
	/**
	 * 回滚时的完成状态
	 */
	int STATUS_ROLLED_BACK = 1;

	/** Completion status in case of heuristic mixed completion or system errors */
	/**
	 * 未知情况下的完成状态
	 */
	int STATUS_UNKNOWN = 2;


	/**
	 * Suspend this synchronization.
	 * Supposed to unbind resources from TransactionSynchronizationManager if managing any.
	 * <p>
	 *     暂停同步。需要先从TransactionSynchronizationManager解除绑定
	 * </p>
	 * @see TransactionSynchronizationManager#unbindResource
	 */
	default void suspend() {
	}

	/**
	 * Resume this synchronization.
	 * Supposed to rebind resources to TransactionSynchronizationManager if managing any.
	 * <p>
	 *     恢复同步。需要绑定到TransactionSynchronizationManager
	 * </p>
	 * @see TransactionSynchronizationManager#bindResource
	 */
	default void resume() {
	}

	/**
	 * Flush the underlying session to the datastore, if applicable:
	 * for example, a Hibernate/JPA session.
	 * <p>
	 *     如果适用，将基础session刷新到数据存储区，比如 Hibernate/JPA session
	 * </p>
	 * @see org.springframework.transaction.TransactionStatus#flush()
	 */
	@Override
	default void flush() {
	}

	/**
	 * Invoked before transaction commit (before "beforeCompletion").
	 * Can e.g. flush transactional O/R Mapping sessions to the database.
	 * <p>This callback does <i>not</i> mean that the transaction will actually be committed.
	 * A rollback decision can still occur after this method has been called. This callback
	 * is rather meant to perform work that's only relevant if a commit still has a chance
	 * to happen, such as flushing SQL statements to the database.
	 * <p>Note that exceptions will get propagated to the commit caller and cause a
	 * rollback of the transaction.
	 * <p>
	 *     在事务提交之前调用（在 beforeCompletion前）,例如将事务性O/R映射会话刷新到数据库。
	 * <p>
	 *     此回调并不是真正提交事务，在调用此方法后，事务仍然可以回滚。
	 * <p>
	 *     如果有异常，将传播给调用者
	 *
	 * @param readOnly whether the transaction is defined as read-only transaction <br>事务是否为只读
	 * @throws RuntimeException in case of errors; will be <b>propagated to the caller</b>
	 * (note: do not throw TransactionException subclasses here!)<br>如果发生异常，将传播给调用者
	 * @see #beforeCompletion
	 */
	default void beforeCommit(boolean readOnly) {
	}

	/**
	 * Invoked before transaction commit/rollback.
	 * Can perform resource cleanup <i>before</i> transaction completion.
	 * <p>This method will be invoked after {@code beforeCommit}, even when
	 * {@code beforeCommit} threw an exception. This callback allows for
	 * closing resources before transaction completion, for any outcome.
	 * <p>
	 *     在事务提交/回滚之前调用，可以在事务完成之前执行资源清理。
	 * <p>
	 *     即使在{@code beforeCommit}引发异常，也会在{@code beforeCommit}之后调用此方法。
	 *     对于任何结果，此回调都允许在事务完成之前关闭资源。
	 * @throws RuntimeException in case of errors; will be <b>logged but not propagated</b>
	 * (note: do not throw TransactionException subclasses here!) 发生异常将被记录，而不抛出
	 * @see #beforeCommit
	 * @see #afterCompletion
	 */
	default void beforeCompletion() {
	}

	/**
	 * Invoked after transaction commit. Can perform further operations right
	 * <i>after</i> the main transaction has <i>successfully</i> committed.
	 * <p>Can e.g. commit further operations that are supposed to follow on a successful
	 * commit of the main transaction, like confirmation messages or emails.
	 * <p><b>NOTE:</b> The transaction will have been committed already, but the
	 * transactional resources might still be active and accessible. As a consequence,
	 * any data access code triggered at this point will still "participate" in the
	 * original transaction, allowing to perform some cleanup (with no commit following
	 * anymore!), unless it explicitly declares that it needs to run in a separate
	 * transaction. Hence: <b>Use {@code PROPAGATION_REQUIRES_NEW} for any
	 * transactional operation that is called from here.</b>
	 * <p>
	 *     事务提交后调用。在成功提交事务后，可以执行进一步操作。
	 * <p>
	 *     <b>注意：</b>事务已经被提交后，事务资源可能仍处于活动状态并且可以访问。因此，如果此时触发的任务数据访问仍将
	 *     参与原始事务，允许进行一些清除操作（不再进行提交操作），除非明确声明需要在单独的事务中运行。<b>因此：对于此处调用的任何事务操作，请使用{@code PROPAGATION_REQUIRES_NEW}</b>
	 *
	 * @throws RuntimeException in case of errors; will be <b>propagated to the caller</b>
	 * (note: do not throw TransactionException subclasses here!) 发送异常，将会传播给调用者
	 */
	default void afterCommit() {
	}

	/**
	 * Invoked after transaction commit/rollback.
	 * Can perform resource cleanup <i>after</i> transaction completion.
	 * <p><b>NOTE:</b> The transaction will have been committed or rolled back already,
	 * but the transactional resources might still be active and accessible. As a
	 * consequence, any data access code triggered at this point will still "participate"
	 * in the original transaction, allowing to perform some cleanup (with no commit
	 * following anymore!), unless it explicitly declares that it needs to run in a
	 * separate transaction. Hence: <b>Use {@code PROPAGATION_REQUIRES_NEW}
	 * for any transactional operation that is called from here.</b>
	 * <p>
	 *     在事务提交/回滚后调用。事务完成后可以执行清理操作。
	 * <p>
	 *     <b>注意：</b>事务已经被提交后，事务资源可能仍处于活动状态并且可以访问。因此，如果此时触发的任务数据访问仍将
	 * 	   参与原始事务，允许进行一些清除操作（不再进行提交操作），除非明确声明需要在单独的事务中运行。<b>因此：对于此处调用的任何事务操作，请使用{@code PROPAGATION_REQUIRES_NEW}
	 * 	 </b>
	 * @param status completion status according to the {@code STATUS_*} constants
	 * @throws RuntimeException in case of errors; will be <b>logged but not propagated</b>
	 * (note: do not throw TransactionException subclasses here!)
	 * @see #STATUS_COMMITTED
	 * @see #STATUS_ROLLED_BACK
	 * @see #STATUS_UNKNOWN
	 * @see #beforeCompletion
	 */
	default void afterCompletion(int status) {
	}

}

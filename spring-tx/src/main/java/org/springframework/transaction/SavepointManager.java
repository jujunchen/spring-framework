/*
 * Copyright 2002-2014 the original author or authors.
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

/**
 * Interface that specifies an API to programmatically manage transaction
 * savepoints in a generic fashion. Extended by TransactionStatus to
 * expose savepoint management functionality for a specific transaction.
 *
 * <p>Note that savepoints can only work within an active transaction.
 * Just use this programmatic savepoint handling for advanced needs;
 * else, a subtransaction with PROPAGATION_NESTED is preferable.
 *
 * <p>This interface is inspired by JDBC 3.0's Savepoint mechanism
 * but is independent from any specific persistence technology.
 *
 * <p>
 *     该接口提供编程的方式管理事务保存点的通用方法。TransactionStatus扩展该接口，以提供事务保存点的管理功能。
 * <p>
 *     注意：保存点只能在活动的事务中工作。只需要使用编程式的保存点管理方法就可以处理高级需求，否则最好使用PROPAGATION_NESTED的子事务。
 * <p>
 *     这个接口受JDBC 3.0保存点启发，但独立于任何指定的持久性技术。
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see TransactionStatus
 * @see TransactionDefinition#PROPAGATION_NESTED
 * @see java.sql.Savepoint
 */
public interface SavepointManager {

	/**
	 * Create a new savepoint. You can roll back to a specific savepoint
	 * via {@code rollbackToSavepoint}, and explicitly release a savepoint
	 * that you don't need anymore via {@code releaseSavepoint}.
	 * <p>Note that most transaction managers will automatically release
	 * savepoints at transaction completion.
	 * <p>
	 *     创建一个新的保存点，能够通过{@code rollbackToSavepoint}回滚事务到指定的保存点，
	 *     也可以通过{@code releaseSavepoint}释放已经不再需要的保存点
	 * <p>
	 *     通常大多数事务管理器将在事务完成后自动释放保存点
	 * @return a savepoint object, to be passed into <br>一个保存点对象
	 * {@link #rollbackToSavepoint} or {@link #releaseSavepoint}
	 * @throws NestedTransactionNotSupportedException if the underlying
	 * transaction does not support savepoints <br>如果事务不支持保存点，抛错
	 * @throws TransactionException if the savepoint could not be created,
	 * for example because the transaction is not in an appropriate state <br>
	 *     如果不能创建事务，比如事务处于一个不正确的状态，抛错
	 * @see java.sql.Connection#setSavepoint
	 */
	Object createSavepoint() throws TransactionException;

	/**
	 * Roll back to the given savepoint.
	 * <p>The savepoint will <i>not</i> be automatically released afterwards.
	 * You may explicitly call {@link #releaseSavepoint(Object)} or rely on
	 * automatic release on transaction completion.
	 * <p>
	 *     回滚事务到指定的保存点。
	 * <p>
	 *     之后，这个保存点将不会自动释放，需要显式调用 {@link #releaseSavepoint(Object)}
	 *     或者 等待事务完成后自动释放
	 * @param savepoint the savepoint to roll back to <br>要回退到的保存点
	 * @throws NestedTransactionNotSupportedException if the underlying
	 * transaction does not support savepoints<br>如果事务不支持保存点，抛错
	 * @throws TransactionException if the rollback failed <br>如果回滚失败
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	void rollbackToSavepoint(Object savepoint) throws TransactionException;

	/**
	 * Explicitly release the given savepoint.
	 * <p>Note that most transaction managers will automatically release
	 * savepoints on transaction completion.
	 * <p>Implementations should fail as silently as possible if proper
	 * resource cleanup will eventually happen at transaction completion.
	 * <p>
	 *     释放指定的保存点。
	 * <p>
	 *     大多数事务管理器将在事务完成后自动释放保存点。
	 * @param savepoint the savepoint to release <br>需要释放的保存点
	 * @throws NestedTransactionNotSupportedException if the underlying
	 * transaction does not support savepoints<br>如果事务不支持保存点，抛错
	 * @throws TransactionException if the release failed<br>如果释放失败，抛错
	 * @see java.sql.Connection#releaseSavepoint
	 */
	void releaseSavepoint(Object savepoint) throws TransactionException;

}

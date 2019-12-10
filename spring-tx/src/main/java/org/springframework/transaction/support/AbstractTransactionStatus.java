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

package org.springframework.transaction.support;

import org.springframework.lang.Nullable;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionUsageException;

/**
 * Abstract base implementation of the
 * {@link org.springframework.transaction.TransactionStatus} interface.
 *
 * <p>Pre-implements the handling of local rollback-only and completed flags, and
 * delegation to an underlying {@link org.springframework.transaction.SavepointManager}.
 * Also offers the option of a holding a savepoint within the transaction.
 *
 * <p>Does not assume any specific internal transaction handling, such as an
 * underlying transaction object, and no transaction synchronization mechanism.
 *
 * <p>
 *     {@link org.springframework.transaction.TransactionStatus}接口的抽象实现。
 * <p>
 *     预先实现仅本地回滚和已完成标志的处理，并委派给基础的{@link org.springframework.transaction.SavepointManager}。
 *     还提供在事务中保留保存点的选项。
 * <p>
 *     不承担任何特定的内部事务处理，例如基础事务对象，并且没有事务同步机制
 *
 * @author Juergen Hoeller
 * @since 1.2.3
 * @see #setRollbackOnly()
 * @see #isRollbackOnly()
 * @see #setCompleted()
 * @see #isCompleted()
 * @see #getSavepointManager()
 * @see SimpleTransactionStatus
 * @see DefaultTransactionStatus
 */
public abstract class AbstractTransactionStatus implements TransactionStatus {

	private boolean rollbackOnly = false;

	private boolean completed = false;

	@Nullable
	private Object savepoint;


	//---------------------------------------------------------------------
	// Implementation of TransactionExecution
	//事务执行实现
	//---------------------------------------------------------------------

	@Override
	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	/**
	 * Determine the rollback-only flag via checking both the local rollback-only flag
	 * of this TransactionStatus and the global rollback-only flag of the underlying
	 * transaction, if any.
	 * <p>
	 *     通过检查TransactionStatus的本地回滚标志和全局回滚标志来确定回滚标志
	 * </p>
	 * @see #isLocalRollbackOnly()
	 * @see #isGlobalRollbackOnly()
	 */
	@Override
	public boolean isRollbackOnly() {
		return (isLocalRollbackOnly() || isGlobalRollbackOnly());
	}

	/**
	 * Determine the rollback-only flag via checking this TransactionStatus.
	 * <p>Will only return "true" if the application called {@code setRollbackOnly}
	 * on this TransactionStatus object.
	 *
	 * <p>
	 *     通过检查TransactionStatus来确定事务回滚标志。
	 * <p>
	 *     如果应用程序通过这个TransactionStatus对象调用 setRollbackOnly，只会返回"true"
	 */
	public boolean isLocalRollbackOnly() {
		return this.rollbackOnly;
	}

	/**
	 * Template method for determining the global rollback-only flag of the
	 * underlying transaction, if any.
	 * <p>This implementation always returns {@code false}.
	 * <p>
	 *     用于确定事务全局回滚标志的模版方法，子类可以重写，默认返回 false
	 * </p>
	 */
	public boolean isGlobalRollbackOnly() {
		return false;
	}

	/**
	 * Mark this transaction as completed, that is, committed or rolled back.
	 * <p>
	 *     设置标志事务已完成，即已提交或者回滚
	 * </p>
	 */
	public void setCompleted() {
		this.completed = true;
	}

	@Override
	public boolean isCompleted() {
		return this.completed;
	}


	//---------------------------------------------------------------------
	// Handling of current savepoint state
	//---------------------------------------------------------------------

	@Override
	public boolean hasSavepoint() {
		return (this.savepoint != null);
	}

	/**
	 * Set a savepoint for this transaction. Useful for PROPAGATION_NESTED.
	 * <p>为这个事务设置保存点，对PROPAGATION_NESTED有效</p>
	 * @see org.springframework.transaction.TransactionDefinition#PROPAGATION_NESTED
	 */
	protected void setSavepoint(@Nullable Object savepoint) {
		this.savepoint = savepoint;
	}

	/**
	 * Get the savepoint for this transaction, if any.
	 * <p>
	 *     获取事务保存点
	 * </p>
	 */
	@Nullable
	protected Object getSavepoint() {
		return this.savepoint;
	}

	/**
	 * Create a savepoint and hold it for the transaction.
	 * <p>创建一个保存点并保存在事务中</p>
	 * @throws org.springframework.transaction.NestedTransactionNotSupportedException
	 * if the underlying transaction does not support savepoints <br>如果事务不支持保存点抛错
	 */
	public void createAndHoldSavepoint() throws TransactionException {
		setSavepoint(getSavepointManager().createSavepoint());
	}

	/**
	 * Roll back to the savepoint that is held for the transaction
	 * and release the savepoint right afterwards.
	 * <p>
	 *     回滚到事务的保存点，然后释放保存点
	 * </p>
	 */
	public void rollbackToHeldSavepoint() throws TransactionException {
		Object savepoint = getSavepoint();
		if (savepoint == null) {
			throw new TransactionUsageException(
					"Cannot roll back to savepoint - no savepoint associated with current transaction");
		}
		getSavepointManager().rollbackToSavepoint(savepoint);
		getSavepointManager().releaseSavepoint(savepoint);
		setSavepoint(null);
	}

	/**
	 * Release the savepoint that is held for the transaction.
	 * <p>释放事务保存点</p>
	 */
	public void releaseHeldSavepoint() throws TransactionException {
		Object savepoint = getSavepoint();
		if (savepoint == null) {
			throw new TransactionUsageException(
					"Cannot release savepoint - no savepoint associated with current transaction");
		}
		getSavepointManager().releaseSavepoint(savepoint);
		setSavepoint(null);
	}


	//---------------------------------------------------------------------
	// Implementation of SavepointManager
	// SavepointManager实现
	//---------------------------------------------------------------------

	/**
	 * This implementation delegates to a SavepointManager for the
	 * underlying transaction, if possible.
	 * <p>
	 *     委派给SavepointManager创建事务保存点
	 * </p>
	 * @see #getSavepointManager()
	 * @see SavepointManager#createSavepoint()
	 */
	@Override
	public Object createSavepoint() throws TransactionException {
		return getSavepointManager().createSavepoint();
	}

	/**
	 * This implementation delegates to a SavepointManager for the
	 * underlying transaction, if possible.
	 * <p>
	 *     委派给SavepointManager，回滚到事务指定保存点
	 * @throws org.springframework.transaction.NestedTransactionNotSupportedException
	 * @see #getSavepointManager()
	 * @see SavepointManager#rollbackToSavepoint(Object)
	 */
	@Override
	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		getSavepointManager().rollbackToSavepoint(savepoint);
	}

	/**
	 * This implementation delegates to a SavepointManager for the
	 * underlying transaction, if possible.
	 * <p>
	 *     委派给SavepointManager，是否事务指定保存点
	 * </p>
	 * @see #getSavepointManager()
	 * @see SavepointManager#releaseSavepoint(Object)
	 */
	@Override
	public void releaseSavepoint(Object savepoint) throws TransactionException {
		getSavepointManager().releaseSavepoint(savepoint);
	}

	/**
	 * Return a SavepointManager for the underlying transaction, if possible.
	 * <p>Default implementation always throws a NestedTransactionNotSupportedException.
	 * @throws org.springframework.transaction.NestedTransactionNotSupportedException
	 * if the underlying transaction does not support savepoints
	 * <p>
	 *     返回一个SavepointManager。
	 *     默认抛出NestedTransactionNotSupportedException，不支持事务保存点
	 * </p>
	 */
	protected SavepointManager getSavepointManager() {
		throw new NestedTransactionNotSupportedException("This transaction does not support savepoints");
	}


	//---------------------------------------------------------------------
	// Flushing support
	// 刷新支持
	//---------------------------------------------------------------------

	/**
	 * This implementations is empty, considering flush as a no-op.
	 <p> 空的模版实现方法
	 */
	@Override
	public void flush() {
	}

}

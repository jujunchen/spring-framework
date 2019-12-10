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

import java.util.Date;

import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionTimedOutException;

/**
 * Convenient base class for resource holders.
 *
 * <p>Features rollback-only support for participating transactions.
 * Can expire after a certain number of seconds or milliseconds
 * in order to determine a transactional timeout.
 *
 * <p>
 *     资源持有者的便捷基础类。可以设置只回滚嵌套事务。可以设置事务超时时间
 * </p>
 *
 * @author Juergen Hoeller
 * @since 02.02.2004
 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#doBegin
 * @see org.springframework.jdbc.datasource.DataSourceUtils#applyTransactionTimeout
 */
public abstract class ResourceHolderSupport implements ResourceHolder {

	private boolean synchronizedWithTransaction = false;

	private boolean rollbackOnly = false;

	@Nullable
	private Date deadline;

	private int referenceCount = 0;

	private boolean isVoid = false;


	/**
	 * Mark the resource as synchronized with a transaction.
	 * <P>
	 *     设置资源是否与事务同步
	 * </P>
	 */
	public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
		this.synchronizedWithTransaction = synchronizedWithTransaction;
	}

	/**
	 * Return whether the resource is synchronized with a transaction.
	 * <p>返回资源是否与事务同步</p>
	 */
	public boolean isSynchronizedWithTransaction() {
		return this.synchronizedWithTransaction;
	}

	/**
	 * Mark the resource transaction as rollback-only.
	 * <p>
	 *     标记资源事务回滚
	 * </p>
	 */
	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

	/**
	 * Reset the rollback-only status for this resource transaction.
	 * <p>Only really intended to be called after custom rollback steps which
	 * keep the original resource in action, e.g. in case of a savepoint.
	 * @since 5.0
	 * @see org.springframework.transaction.SavepointManager#rollbackToSavepoint
	 */
	public void resetRollbackOnly() {
		this.rollbackOnly = false;
	}

	/**
	 * Return whether the resource transaction is marked as rollback-only.
	 * <p>
	 *     返回资源事务是否标记回滚
	 * </p>
	 */
	public boolean isRollbackOnly() {
		return this.rollbackOnly;
	}

	/**
	 * Set the timeout for this object in seconds.
	 * <p>
	 *     设置超时时间(秒)
	 * </p>
	 * @param seconds number of seconds until expiration 超时时间，秒
	 */
	public void setTimeoutInSeconds(int seconds) {
		setTimeoutInMillis(seconds * 1000L);
	}

	/**
	 * Set the timeout for this object in milliseconds.
	 * <p>
	 *     设置超时时间，毫秒
	 * </p>
	 * @param millis number of milliseconds until expiration 超时时间，毫秒
	 */
	public void setTimeoutInMillis(long millis) {
		this.deadline = new Date(System.currentTimeMillis() + millis);
	}

	/**
	 * Return whether this object has an associated timeout.
	 * <p>
	 *     返回是否设置了超时时间
	 * </p>
	 */
	public boolean hasTimeout() {
		return (this.deadline != null);
	}

	/**
	 * Return the expiration deadline of this object.
	 * <p>
	 *     返回超时时间
	 * </p>
	 * @return the deadline as Date object 返回超时时间，Date对象
	 */
	@Nullable
	public Date getDeadline() {
		return this.deadline;
	}

	/**
	 * Return the time to live for this object in seconds.
	 * Rounds up eagerly, e.g. 9.00001 still to 10.
	 * <p>
	 *     返回对象的剩余生存时间，秒。
	 *     始终入整，比如9.00001 返回 10
	 * </p>
	 * @return number of seconds until expiration <br>到期前的秒数
	 * @throws TransactionTimedOutException if the deadline has already been reached <br>如果已经超时，返回TransactionTimedOutException
	 */
	public int getTimeToLiveInSeconds() {
		//除以1000,编程秒数，会有小数
		double diff = ((double) getTimeToLiveInMillis()) / 1000;
		//获取大于或等于的整数
		int secs = (int) Math.ceil(diff);
		checkTransactionTimeout(secs <= 0);
		return secs;
	}

	/**
	 * Return the time to live for this object in milliseconds.
	 * <p>
	 *     返回剩余生存时间，毫秒
	 * </p>
	 * @return number of millseconds until expiration <br>到期前的毫秒数
	 * @throws TransactionTimedOutException if the deadline has already been reached <br>如果已经超时，返回TransactionTimedOutException
	 */
	public long getTimeToLiveInMillis() throws TransactionTimedOutException{
		if (this.deadline == null) {
			throw new IllegalStateException("No timeout specified for this resource holder");
		}
		long timeToLive = this.deadline.getTime() - System.currentTimeMillis();
		checkTransactionTimeout(timeToLive <= 0);
		return timeToLive;
	}

	/**
	 * Set the transaction rollback-only if the deadline has been reached,
	 * and throw a TransactionTimedOutException.
	 * <p>
	 *     如果已经超时，则只能设置事务回滚，并抛出TransactionTimedOutException
	 * </p>
	 */
	private void checkTransactionTimeout(boolean deadlineReached) throws TransactionTimedOutException {
		if (deadlineReached) {
			setRollbackOnly();
			throw new TransactionTimedOutException("Transaction timed out: deadline was " + this.deadline);
		}
	}

	/**
	 * Increase the reference count by one because the holder has been requested
	 * (i.e. someone requested the resource held by it).
	 * <p>
	 *     资源被引用就加1
	 * </p>
	 */
	public void requested() {
		this.referenceCount++;
	}

	/**
	 * Decrease the reference count by one because the holder has been released
	 * (i.e. someone released the resource held by it).
	 * <p>引用被释放，就减1</p>
	 */
	public void released() {
		this.referenceCount--;
	}

	/**
	 * Return whether there are still open references to this holder.
	 * <p>
	 *     返回资源持有者是否还被引用
	 * </p>
	 */
	public boolean isOpen() {
		return (this.referenceCount > 0);
	}

	/**
	 * Clear the transactional state of this resource holder.
	 * <p>
	 *     清除资源持有者的事务状态
	 * </p>
	 */
	public void clear() {
		this.synchronizedWithTransaction = false;
		this.rollbackOnly = false;
		this.deadline = null;
	}

	/**
	 * Reset this resource holder - transactional state as well as reference count.
	 * <p>
	 *     重置资源持有者，包括事务状态和引用计数
	 * </p>
	 */
	@Override
	public void reset() {
		clear();
		this.referenceCount = 0;
	}

	@Override
	public void unbound() {
		this.isVoid = true;
	}

	@Override
	public boolean isVoid() {
		return this.isVoid;
	}

}

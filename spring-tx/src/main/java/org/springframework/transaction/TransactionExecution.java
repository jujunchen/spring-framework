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

/**
 * Common representation of the current state of a transaction.
 * Serves as base interface for {@link TransactionStatus} as well as
 * {@link ReactiveTransaction}.
 *
 * @author Juergen Hoeller
 * @since 5.2
 */
public interface TransactionExecution {

	/**
	 * Return whether the present transaction is new; otherwise participating
	 * in an existing transaction, or potentially not running in an actual
	 * transaction in the first place.
	 * <p>判断当前事务是否是新事务（否则当前事务是一个已有事务，或者当前事务未运行在事务环境中）</p>
	 */
	boolean isNewTransaction();

	/**
	 * Set the transaction rollback-only. This instructs the transaction manager
	 * that the only possible outcome of the transaction may be a rollback, as
	 * alternative to throwing an exception which would in turn trigger a rollback.
	 * <p>
	 *     设置事务可回滚。通过该标识通知事务管理器只能将事务回滚，事务管理通过显示调用回滚命令或者抛出异常的方式回滚事务
	 * <p>
	 *     主要用于{@link org.springframework.transaction.support.TransactionTemplate} 或者
	 *     {@link org.springframework.transaction.interceptor.TransactionInterceptor}管理的事务，
	 *     由容器决定是否提交/回滚
	 */
	void setRollbackOnly();

	/**
	 * Return whether the transaction has been marked as rollback-only
	 * (either by the application or by the transaction infrastructure).
	 * <p>
	 *     返回事务是否已标记为可回滚
	 * </p>
	 */
	boolean isRollbackOnly();

	/**
	 * Return whether this transaction is completed, that is,
	 * whether it has already been committed or rolled back.
	 * <p>
	 *     返回此事务是否已完成，即是否已经提交或者回滚
	 * </p>
	 */
	boolean isCompleted();

}

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

import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionStatus;

/**
 * Callback interface for transactional code. Used with {@link TransactionTemplate}'s
 * {@code execute} method, often as anonymous class within a method implementation.
 *
 * <p>Typically used to assemble various calls to transaction-unaware data access
 * services into a higher-level service method with transaction demarcation. As an
 * alternative, consider the use of declarative transaction demarcation (e.g. through
 * Spring's {@link org.springframework.transaction.annotation.Transactional} annotation).
 *
 * <p>
 *     事务代码的回调接口。用于{@link TransactionTemplate} execute方法中的匿名类
 * </p>
 *
 * @author Juergen Hoeller
 * @since 17.03.2003
 * @see TransactionTemplate
 * @see CallbackPreferringPlatformTransactionManager
 * @param <T> the result type
 */
@FunctionalInterface
public interface TransactionCallback<T> {

	/**
	 * Gets called by {@link TransactionTemplate#execute} within a transactional context.
	 * Does not need to care about transactions itself, although it can retrieve and
	 * influence the status of the current transaction via the given status object,
	 * e.g. setting rollback-only.
	 * <p>Allows for returning a result object created within the transaction, i.e. a
	 * domain object or a collection of domain objects. A RuntimeException thrown by the
	 * callback is treated as application exception that enforces a rollback. Any such
	 * exception will be propagated to the caller of the template, unless there is a
	 * problem rolling back, in which case a TransactionException will be thrown.
	 *
	 * <p>
	 *     在事务上下文中由{@link TransactionTemplate#execute(TransactionCallback)}调用。
	 *		不需要关心事务本身，可以通过给定的事务状态影响当前事务状态，比如设置 只能回滚
	 * <p>
	 *     允许返回事务内创建的结果。即对象域或者对象域集合，回调引发的RunTimeException被当着强制执行回滚的应用程序异常。
	 *     除非存在回滚问题，否则任何异常都将传播到调用者，抛出TransactionException
	 * @param status associated transaction status <br>指定的事务状态
	 * @return a result object, or {@code null} <br>返回的结果，或者 null
	 * @see TransactionTemplate#execute
	 * @see CallbackPreferringPlatformTransactionManager#execute
	 */
	@Nullable
	T doInTransaction(TransactionStatus status);

}

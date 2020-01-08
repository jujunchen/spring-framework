/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.transaction.PlatformTransactionManager;

/**
 * Extension of the {@link org.springframework.transaction.PlatformTransactionManager}
 * interface, indicating a native resource transaction manager, operating on a single
 * target resource. Such transaction managers differ from JTA transaction managers in
 * that they do not use XA transaction enlistment for an open number of resources but
 * rather focus on leveraging the native power and simplicity of a single target resource.
 *
 * <br>
 *     {@link org.springframework.transaction.PlatformTransactionManager}接口的扩展，
 *     指示在单个目标资源上运行的本机资源事务管理器。
 *     此类事务管理器与JTA事务管理器的不同之处在于，它们不使用XA事务登记来获取大量资源，
 *     而是专注于利用单个目标资源的本机功能和简单性。
 *
 * <p>This interface is mainly used for abstract introspection of a transaction manager,
 * giving clients a hint on what kind of transaction manager they have been given
 * and on what concrete resource the transaction manager is operating on.
 * <p>
 *     此接口主要用于事务管理器的抽象，为客户提供有关已授予他们哪种事务管理器以及事务管理器正在使用哪种具体资源的提示。
 * </p>
 *
 *
 * @author Juergen Hoeller
 * @since 2.0.4
 * @see TransactionSynchronizationManager
 */
public interface ResourceTransactionManager extends PlatformTransactionManager {

	/**
	 * Return the resource factory that this transaction manager operates on,
	 * e.g. a JDBC DataSource or a JMS ConnectionFactory.
	 * <br>
	 *     返回此事务管理器操作的资源工厂，例如JDBC数据源或JMS ConnectionFactory。
	 *
	 * <p>This target resource factory is usually used as resource key for
	 * <p>
	 *     此目标资源工厂通常用作每个线程TransactionSynchronizationManager的资源绑定的资源键。
	 * </p>
	 * {@link TransactionSynchronizationManager}'s resource bindings per thread.
	 * @return the target resource factory (never {@code null})
	 * @see TransactionSynchronizationManager#bindResource
	 * @see TransactionSynchronizationManager#getResource
	 */
	Object getResourceFactory();

}

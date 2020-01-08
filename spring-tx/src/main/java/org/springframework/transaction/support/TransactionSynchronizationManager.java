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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.NamedThreadLocal;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Central delegate that manages resources and transaction synchronizations per thread.
 * To be used by resource management code but not by typical application code.
 *
 * <p>Supports one resource per key without overwriting, that is, a resource needs
 * to be removed before a new one can be set for the same key.
 * Supports a list of transaction synchronizations if synchronization is active.
 *
 * <p>Resource management code should check for thread-bound resources, e.g. JDBC
 * Connections or Hibernate Sessions, via {@code getResource}. Such code is
 * normally not supposed to bind resources to threads, as this is the responsibility
 * of transaction managers. A further option is to lazily bind on first use if
 * transaction synchronization is active, for performing transactions that span
 * an arbitrary number of resources.
 *
 * <p>Transaction synchronization must be activated and deactivated by a transaction
 * manager via {@link #initSynchronization()} and {@link #clearSynchronization()}.
 * This is automatically supported by {@link AbstractPlatformTransactionManager},
 * and thus by all standard Spring transaction managers, such as
 * {@link org.springframework.transaction.jta.JtaTransactionManager} and
 * {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}.
 *
 * <p>Resource management code should only register synchronizations when this
 * manager is active, which can be checked via {@link #isSynchronizationActive};
 * it should perform immediate resource cleanup else. If transaction synchronization
 * isn't active, there is either no current transaction, or the transaction manager
 * doesn't support transaction synchronization.
 *
 * <p>Synchronization is for example used to always return the same resources
 * within a JTA transaction, e.g. a JDBC Connection or a Hibernate Session for
 * any given DataSource or SessionFactory, respectively.
 *
 * <p>
 *     中央委托器，用于管理每个线程的资源和事务同步。提供给资源管理器调用，而不是应用程序代码。
 * <p>
 *     每个线程拥有独立的key，而不会被覆盖(使用了ThreadLocal)，设置前，先删除原来key的资源，再设置该key的新资源。
 *     如果同步处于活动状态，则支持事务同步列表。
 * <p>
 *     资源管理器应该检查线程绑定的资源，比如通过getResource获取JDBC连接或者Hibernate Sessions资源。
 *     此类代码通常不应该绑定到线程，因为这是事务管理器的责任。进一步的选择是如果事务同步处于活动状态，那么在首次使用时使用延迟绑定，
 *     以便执行任意数量资源的事务。
 * <p>
 *     事务同步必须由事务管理器通过 {@link #initSynchronization()} 和 {@link #clearSynchronization()} 激活和停用。
 *     {@link AbstractPlatformTransactionManager} 已经支持此功能，所以其他标准事务管理都支持该功能,比如{@link org.springframework.transaction.jta.JtaTransactionManager}
 *     比如{@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
 * <p>
 * 	   资源管理器应在此管理器处于活动状态时注册同步，可以通过{@link #isSynchronizationActive}来检查。否则应该及时清除资源。
 * 	   如果事务同步未处于活动状态，则说明当前没有事务，或者事务管理器不支持事务同步。
 * <p>
 *     例如，同步用于始终在JTA事务中返回相同的资源，比如任意的DataSource 或者 SessionFactory返回JDBC连接或者Hibernate会话。
 *  //todo
 * @author Juergen Hoeller
 * @since 02.06.2003
 * @see #isSynchronizationActive
 * @see #registerSynchronization
 * @see TransactionSynchronization
 * @see AbstractPlatformTransactionManager#setTransactionSynchronization
 * @see org.springframework.transaction.jta.JtaTransactionManager
 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
 * @see org.springframework.jdbc.datasource.DataSourceUtils#getConnection
 */
public abstract class TransactionSynchronizationManager {

	private static final Log logger = LogFactory.getLog(TransactionSynchronizationManager.class);

	/**
	 * 保存每个事务线程绑定的资源,比如connection和session资源
	 */
	private static final ThreadLocal<Map<Object, Object>> resources =
			new NamedThreadLocal<>("Transactional resources");

	/**
	 * 保存每个事务对应的事务同步回调接口
	 */
	private static final ThreadLocal<Set<TransactionSynchronization>> synchronizations =
			new NamedThreadLocal<>("Transaction synchronizations");

	/**
	 * 保存每个事务线程对应的事务名称
	 */
	private static final ThreadLocal<String> currentTransactionName =
			new NamedThreadLocal<>("Current transaction name");

	/**
	 * 保存每个事务线程对应的事务只读状态
	 */
	private static final ThreadLocal<Boolean> currentTransactionReadOnly =
			new NamedThreadLocal<>("Current transaction read-only status");

	/**
	 * 保存每个事务线程对应的事务隔离级别
	 */
	private static final ThreadLocal<Integer> currentTransactionIsolationLevel =
			new NamedThreadLocal<>("Current transaction isolation level");

	/**
	 * 保存每个事务线程对应的事务激活状态
	 */
	private static final ThreadLocal<Boolean> actualTransactionActive =
			new NamedThreadLocal<>("Actual transaction active");


	//-------------------------------------------------------------------------
	// Management of transaction-associated resource handles
	// 与处理事务相关的资源的管理
	//-------------------------------------------------------------------------

	/**
	 * Return all resources that are bound to the current thread.
	 * <p>Mainly for debugging purposes. Resource managers should always invoke
	 * {@code hasResource} for a specific resource key that they are interested in.
	 * @return a Map with resource keys (usually the resource factory) and resource
	 * values (usually the active resource object), or an empty Map if there are
	 * currently no resources bound
	 * @see #hasResource
	 */
	public static Map<Object, Object> getResourceMap() {
		Map<Object, Object> map = resources.get();
		return (map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap());
	}

	/**
	 * Check if there is a resource for the given key bound to the current thread.
	 * @param key the key to check (usually the resource factory)
	 * @return if there is a value bound to the current thread
	 * @see ResourceTransactionManager#getResourceFactory()
	 */
	public static boolean hasResource(Object key) {
		Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
		Object value = doGetResource(actualKey);
		return (value != null);
	}

	/**
	 * Retrieve a resource for the given key that is bound to the current thread.
	 * @param key the key to check (usually the resource factory)
	 * @return a value bound to the current thread (usually the active
	 * resource object), or {@code null} if none
	 * @see ResourceTransactionManager#getResourceFactory()
	 */
	@Nullable
	public static Object getResource(Object key) {
		Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
		Object value = doGetResource(actualKey);
		if (value != null && logger.isTraceEnabled()) {
			logger.trace("Retrieved value [" + value + "] for key [" + actualKey + "] bound to thread [" +
					Thread.currentThread().getName() + "]");
		}
		return value;
	}

	/**
	 * Actually check the value of the resource that is bound for the given key.
	 */
	@Nullable
	private static Object doGetResource(Object actualKey) {
		Map<Object, Object> map = resources.get();
		if (map == null) {
			return null;
		}
		Object value = map.get(actualKey);
		// Transparently remove ResourceHolder that was marked as void...
		if (value instanceof ResourceHolder && ((ResourceHolder) value).isVoid()) {
			map.remove(actualKey);
			// Remove entire ThreadLocal if empty...
			if (map.isEmpty()) {
				resources.remove();
			}
			value = null;
		}
		return value;
	}

	/**
	 * Bind the given resource for the given key to the current thread.
	 * <br>
	 *     通过指定键将给定资源绑定到当前线程
	 * @param key the key to bind the value to (usually the resource factory) 资源的key，通常是资源工厂
	 * @param value the value to bind (usually the active resource object) 要绑定的资源，通常是实际资源对象
	 * @throws IllegalStateException if there is already a value bound to the thread
	 * @see ResourceTransactionManager#getResourceFactory()
	 */
	public static void bindResource(Object key, Object value) throws IllegalStateException {
		Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
		Assert.notNull(value, "Value must not be null");
		Map<Object, Object> map = resources.get();
		// set ThreadLocal Map if none found
		if (map == null) {
			map = new HashMap<>();
			resources.set(map);
		}
		Object oldValue = map.put(actualKey, value);
		// Transparently suppress a ResourceHolder that was marked as void...
		if (oldValue instanceof ResourceHolder && ((ResourceHolder) oldValue).isVoid()) {
			oldValue = null;
		}
		if (oldValue != null) {
			throw new IllegalStateException("Already value [" + oldValue + "] for key [" +
					actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Bound value [" + value + "] for key [" + actualKey + "] to thread [" +
					Thread.currentThread().getName() + "]");
		}
	}

	/**
	 * Unbind a resource for the given key from the current thread.
	 * @param key the key to unbind (usually the resource factory)
	 * @return the previously bound value (usually the active resource object)
	 * @throws IllegalStateException if there is no value bound to the thread
	 * @see ResourceTransactionManager#getResourceFactory()
	 */
	public static Object unbindResource(Object key) throws IllegalStateException {
		Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
		Object value = doUnbindResource(actualKey);
		if (value == null) {
			throw new IllegalStateException(
					"No value for key [" + actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
		}
		return value;
	}

	/**
	 * Unbind a resource for the given key from the current thread.
	 * <p>
	 *     从当前线程中根据给定的key删除绑定的资源
	 * </p>
	 * @param key the key to unbind (usually the resource factory) <br>需要删除的key（通常是资源工厂）
	 * @return the previously bound value, or {@code null} if none bound <br>返回原来绑定的值
	 */
	@Nullable
	public static Object unbindResourceIfPossible(Object key) {
		Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
		return doUnbindResource(actualKey);
	}

	/**
	 * Actually remove the value of the resource that is bound for the given key.
	 * <p>
	 *     通过key删除绑定资源的执行方法
	 * </p>
	 */
	@Nullable
	private static Object doUnbindResource(Object actualKey) {
		Map<Object, Object> map = resources.get();
		if (map == null) {
			return null;
		}
		Object value = map.remove(actualKey);
		// Remove entire ThreadLocal if empty...
		//如果删除后，map变为空map，则从线程绑定中删除
		if (map.isEmpty()) {
			resources.remove();
		}
		// Transparently suppress a ResourceHolder that was marked as void...
		//如果ResourceHolder已经无效，则返回null
		if (value instanceof ResourceHolder && ((ResourceHolder) value).isVoid()) {
			value = null;
		}
		if (value != null && logger.isTraceEnabled()) {
			logger.trace("Removed value [" + value + "] for key [" + actualKey + "] from thread [" +
					Thread.currentThread().getName() + "]");
		}
		return value;
	}


	//-------------------------------------------------------------------------
	// Management of transaction synchronizations
	//-------------------------------------------------------------------------

	/**
	 * Return if transaction synchronization is active for the current thread.
	 * Can be called before register to avoid unnecessary instance creation.
	 * <p>
	 *     如果当前线程的事务同步处于活动状态，则返回true。可以在注册前调用，以避免不必要的实例创建。
	 * </p>
	 * @see #registerSynchronization
	 */
	public static boolean isSynchronizationActive() {
		return (synchronizations.get() != null);
	}

	/**
	 * Activate transaction synchronization for the current thread.
	 * Called by a transaction manager on transaction begin.
	 * <p>
	 *     激活当前线程的事务同步。由事务管理器在事务开始时调用。
	 * </p>
	 * @throws IllegalStateException if synchronization is already active 如果事务同步已经激活，则抛错
	 */
	public static void initSynchronization() throws IllegalStateException {
		if (isSynchronizationActive()) {
			throw new IllegalStateException("Cannot activate transaction synchronization - already active");
		}
		logger.trace("Initializing transaction synchronization");
		//在 ThreadLocal放了一个空的 LinkedHashSet
		synchronizations.set(new LinkedHashSet<>());
	}

	/**
	 * Register a new transaction synchronization for the current thread.
	 * Typically called by resource management code.
	 * <p>Note that synchronizations can implement the
	 * {@link org.springframework.core.Ordered} interface.
	 * They will be executed in an order according to their order value (if any).
	 * <p>
	 *     为当前线程注册新的事务同步。通常由资源管理器调用。
	 * 请注意，同步可以实现{@link org.springframework.core.Ordered}接口。可以按order值执行。
	 * </p>
	 * @param synchronization the synchronization object to register 要注册的同步对象
	 * @throws IllegalStateException if transaction synchronization is not active 如果事务同步没有激活，则抛错
	 * @see org.springframework.core.Ordered
	 */
	public static void registerSynchronization(TransactionSynchronization synchronization)
			throws IllegalStateException {

		Assert.notNull(synchronization, "TransactionSynchronization must not be null");
		//当前线程的事务同步没有激活
		Set<TransactionSynchronization> synchs = synchronizations.get();
		if (synchs == null) {
			throw new IllegalStateException("Transaction synchronization is not active");
		}
		synchs.add(synchronization);
	}

	/**
	 * Return an unmodifiable snapshot list of all registered synchronizations
	 * for the current thread.
	 * <p>
	 *     返回当前线程所有已同步，不可更改的TransactionSynchronization快照列表
	 * </p>
	 * @return unmodifiable List of TransactionSynchronization instances <br>不可修改的TransactionSynchronization实例列表
	 * @throws IllegalStateException if synchronization is not active <br>如果同步未激活
	 * @see TransactionSynchronization
	 */
	public static List<TransactionSynchronization> getSynchronizations() throws IllegalStateException {
		//获取当前线程所拥有的TransactionSynchronization
		Set<TransactionSynchronization> synchs = synchronizations.get();
		if (synchs == null) {
			throw new IllegalStateException("Transaction synchronization is not active");
		}
		// Return unmodifiable snapshot, to avoid ConcurrentModificationExceptions
		// while iterating and invoking synchronization callbacks that in turn
		// might register further synchronizations.
		if (synchs.isEmpty()) {
			return Collections.emptyList();
		}
		else {
			// Sort lazily here, not in registerSynchronization.
			//排序
			List<TransactionSynchronization> sortedSynchs = new ArrayList<>(synchs);
			AnnotationAwareOrderComparator.sort(sortedSynchs);
			return Collections.unmodifiableList(sortedSynchs);
		}
	}

	/**
	 * Deactivate transaction synchronization for the current thread.
	 * Called by the transaction manager on transaction cleanup.
	 * <p>
	 *     停用当前线程的事务同步。由事务管理器在事务清理中调用
	 * </p>
	 * @throws IllegalStateException if synchronization is not active <br>如果同步未激活
	 */
	public static void clearSynchronization() throws IllegalStateException {
		//事务同步未激活
		if (!isSynchronizationActive()) {
			throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
		}
		logger.trace("Clearing transaction synchronization");
		synchronizations.remove();
	}


	//-------------------------------------------------------------------------
	// Exposure of transaction characteristics
	// 暴露事务特征
	//-------------------------------------------------------------------------

	/**
	 * Expose the name of the current transaction, if any.
	 * Called by the transaction manager on transaction begin and on cleanup.
	 * <p>
	 *     当前事务设置事务名称方法。事务管理器在事务开始和清理的时候调用。
	 * </p>
	 * @param name the name of the transaction, or {@code null} to reset it
	 *                <br>事务名称，{@code null}重置
	 * @see org.springframework.transaction.TransactionDefinition#getName()
	 */
	public static void setCurrentTransactionName(@Nullable String name) {
		currentTransactionName.set(name);
	}

	/**
	 * Return the name of the current transaction, or {@code null} if none set.
	 * To be called by resource management code for optimizations per use case,
	 * for example to optimize fetch strategies for specific named transactions.
	 * <p>
	 *     返回当前事务的名称，如果没有设置返回{@code null}。由资源管理代码调用以针对每个用例进行优化，
	 *     比如针对特定的事务命名获取进行优化
	 * </p>
	 * @see org.springframework.transaction.TransactionDefinition#getName()
	 */
	@Nullable
	public static String getCurrentTransactionName() {
		return currentTransactionName.get();
	}

	/**
	 * Expose a read-only flag for the current transaction.
	 * Called by the transaction manager on transaction begin and on cleanup.
	 * <p>
	 *     暴露设置事务只读的方法，事务管理在开始和清理时调用。
	 * </p>
	 * @param readOnly {@code true} to mark the current transaction
	 * as read-only; {@code false} to reset such a read-only marker
	 *                                <br> true表示当前事务为只读，false重置事务只读标志
	 * @see org.springframework.transaction.TransactionDefinition#isReadOnly()
	 */
	public static void setCurrentTransactionReadOnly(boolean readOnly) {
		//使用包装类型的布尔，可以放null
		currentTransactionReadOnly.set(readOnly ? Boolean.TRUE : null);
	}

	/**
	 * Return whether the current transaction is marked as read-only.
	 * To be called by resource management code when preparing a newly
	 * created resource (for example, a Hibernate Session).
	 * <p>Note that transaction synchronizations receive the read-only flag
	 * as argument for the {@code beforeCommit} callback, to be able
	 * to suppress change detection on commit. The present method is meant
	 * to be used for earlier read-only checks, for example to set the
	 * flush mode of a Hibernate Session to "FlushMode.NEVER" upfront.
	 * <p>
	 *     返回当前事务是否为只读。在准备创建新的资源时由资源管理代码调用。
	 * <p>
	 *     注意：事务将只读标志作为{@code beforeCommit}的入参，以便防止在提交时发生改变。
	 *     该方法用于较早的只读检查，比如将Hibernate会话的刷新模式预先设置为"FlushMode.NEVER"
	 * @see org.springframework.transaction.TransactionDefinition#isReadOnly()
	 * @see TransactionSynchronization#beforeCommit(boolean)
	 */
	public static boolean isCurrentTransactionReadOnly() {
		return (currentTransactionReadOnly.get() != null);
	}

	/**
	 * Expose an isolation level for the current transaction.
	 * Called by the transaction manager on transaction begin and on cleanup.
	 * <p>公开设置当前事务隔离级别的方法。在事务开始和清理的时候调用</p>
	 * @param isolationLevel the isolation level to expose, according to the
	 * JDBC Connection constants (equivalent to the corresponding Spring
	 * TransactionDefinition constants), or {@code null} to reset it <br>
	 * JDBC连接的常量，等效于Spring TransactionDefinition常量公开的隔离级别，null表示重置
	 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
	 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
	 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
	 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_UNCOMMITTED
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_COMMITTED
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_REPEATABLE_READ
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_SERIALIZABLE
	 * @see org.springframework.transaction.TransactionDefinition#getIsolationLevel()
	 */
	public static void setCurrentTransactionIsolationLevel(@Nullable Integer isolationLevel) {
		currentTransactionIsolationLevel.set(isolationLevel);
	}

	/**
	 * Return the isolation level for the current transaction, if any.
	 * To be called by resource management code when preparing a newly
	 * created resource (for example, a JDBC Connection).
	 * <p>
	 *     返回当前事务的隔离级别，在准备创建新的资源时由资源管理器调用(比如,JDBC连接)
	 * </p>
	 * @return the currently exposed isolation level, according to the
	 * JDBC Connection constants (equivalent to the corresponding Spring
	 * TransactionDefinition constants), or {@code null} if none <br>
	 *     当前公开的隔离级别，根据JDBC连接的常量(相当于Spring TransactionDefinition的常量)，如果没有返回设置null
	 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
	 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
	 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
	 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_UNCOMMITTED
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_READ_COMMITTED
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_REPEATABLE_READ
	 * @see org.springframework.transaction.TransactionDefinition#ISOLATION_SERIALIZABLE
	 * @see org.springframework.transaction.TransactionDefinition#getIsolationLevel()
	 */
	@Nullable
	public static Integer getCurrentTransactionIsolationLevel() {
		return currentTransactionIsolationLevel.get();
	}

	/**
	 * Expose whether there currently is an actual transaction active.
	 * Called by the transaction manager on transaction begin and on cleanup.
	 * <p>
	 *     公开设置当前是否有实际事务激活的方法。由事务管理器在事务开始和清除时调用
	 * </p>
	 * @param active {@code true} to mark the current thread as being associated
	 * with an actual transaction; {@code false} to reset that marker <br>
	 *                           如果将当前线程标记为与实际事务相关联，则为true;否则为false，重置事务标记
	 */
	public static void setActualTransactionActive(boolean active) {
		actualTransactionActive.set(active ? Boolean.TRUE : null);
	}

	/**
	 * Return whether there currently is an actual transaction active.
	 * This indicates whether the current thread is associated with an actual
	 * transaction rather than just with active transaction synchronization.
	 * <p>To be called by resource management code that wants to discriminate
	 * between active transaction synchronization (with or without backing
	 * resource transaction; also on PROPAGATION_SUPPORTS) and an actual
	 * transaction being active (with backing resource transaction;
	 * on PROPAGATION_REQUIRES, PROPAGATION_REQUIRES_NEW, etc).
	 * <p>
	 *     返回当前是否有实际的事务处于活动状态。这表明当前线程是否与实际的事务相关联，而不仅与活动事务同步相关联。
	 * <p>
	 *		由资源管理器调用，该资源管理器要区分活动事务同步（有或者没有事务，PROPAGATION_SUPPORTS）与处于活动状态的实际事务（有后台事务，PROPAGATION_REQUIRES, PROPAGATION_REQUIRES_NEW）
	 * @see #isSynchronizationActive()
	 */
	public static boolean isActualTransactionActive() {
		return (actualTransactionActive.get() != null);
	}


	/**
	 * Clear the entire transaction synchronization state for the current thread:
	 * registered synchronizations as well as the various transaction characteristics.
	 * <p>
	 *     清除当前线程所有的事务同步状态
	 * </p>
	 * @see #clearSynchronization()
	 * @see #setCurrentTransactionName
	 * @see #setCurrentTransactionReadOnly
	 * @see #setCurrentTransactionIsolationLevel
	 * @see #setActualTransactionActive
	 */
	public static void clear() {
		//停用当前线程的事务同步
		synchronizations.remove();
		//重置当前线程的的事务名称
		currentTransactionName.remove();
		//重置当前线程的事务只读标志
		currentTransactionReadOnly.remove();
		//重置当前线程的事务隔离级别
		currentTransactionIsolationLevel.remove();
		//重置当前线程的激活状态
		actualTransactionActive.remove();
	}

}

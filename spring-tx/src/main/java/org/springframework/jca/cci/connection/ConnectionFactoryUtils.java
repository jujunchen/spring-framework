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

package org.springframework.jca.cci.connection;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.jca.cci.CannotGetCciConnectionException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * Helper class that provides static methods for obtaining CCI Connections
 * from a {@link javax.resource.cci.ConnectionFactory}. Includes special
 * support for Spring-managed transactional Connections, e.g. managed
 * by {@link CciLocalTransactionManager} or
 * {@link org.springframework.transaction.jta.JtaTransactionManager}.
 *
 * <br>
 *     工具类，用于提供从{@link javax.resource.cci.ConnectionFactory}获取CCI连接的静态方法。
 *     包括对Spring管理的事务性连接的特殊支持，例如由{@link CciLocalTransactionManager} 或者
 *   	{@link org.springframework.transaction.jta.JtaTransactionManager} 管理
 *
 *
 * <p>Used internally by {@link org.springframework.jca.cci.core.CciTemplate},
 * Spring's CCI operation objects and the {@link CciLocalTransactionManager}.
 * Can also be used directly in application code.
 * <p>
 *     由{@link org.springframework.jca.cci.core.CciTemplate}，
 *     Spring的CCI操作对象和{@link CciLocalTransactionManager}内部使用。
 *     也可以直接在应用程序代码中使用。
 * </p>
 *
 * @author Thierry Templier
 * @author Juergen Hoeller
 * @since 1.2
 * @see #getConnection
 * @see #releaseConnection
 * @see CciLocalTransactionManager
 * @see org.springframework.transaction.jta.JtaTransactionManager
 * @see org.springframework.transaction.support.TransactionSynchronizationManager
 */
public abstract class ConnectionFactoryUtils {

	private static final Log logger = LogFactory.getLog(ConnectionFactoryUtils.class);


	/**
	 * Obtain a Connection from the given ConnectionFactory. Translates ResourceExceptions
	 * into the Spring hierarchy of unchecked generic data access exceptions, simplifying
	 * calling code and making any exception that is thrown more meaningful.
	 * <br>
	 *     从给定的ConnectionFactory中获取一个Connection。
	 *     将ResourceExceptions转换为未经检查的通用数据访问异常，从而简化了调用代码并使所有抛出的异常更有意义。
	 *
	 * <p>Is aware of a corresponding Connection bound to the current thread, for example
	 * when using {@link CciLocalTransactionManager}. Will bind a Connection to the thread
	 * if transaction synchronization is active (e.g. if in a JTA transaction).
	 * <p>
	 *     要绑定到当前线程的相应Connection，例如，在使用CciLocalTransactionManager时。
	 *     如果事务同步处于活动状态（例如，在JTA事务中），则将Connection绑定到线程。
	 * </p>
	 * @param cf the ConnectionFactory to obtain Connection from 获取Connection的ConnectionFactory
	 * @return a CCI Connection from the given ConnectionFactory 从给定的ConnectionFactory获取的CCI连接
	 * @throws org.springframework.jca.cci.CannotGetCciConnectionException
	 * if the attempt to get a Connection failed
	 * @see #releaseConnection
	 */
	public static Connection getConnection(ConnectionFactory cf) throws CannotGetCciConnectionException {
		return getConnection(cf, null);
	}

	/**
	 * Obtain a Connection from the given ConnectionFactory. Translates ResourceExceptions
	 * into the Spring hierarchy of unchecked generic data access exceptions, simplifying
	 * calling code and making any exception that is thrown more meaningful.
	 * <p>Is aware of a corresponding Connection bound to the current thread, for example
	 * when using {@link CciLocalTransactionManager}. Will bind a Connection to the thread
	 * if transaction synchronization is active (e.g. if in a JTA transaction).
	 * @param cf the ConnectionFactory to obtain Connection from 获取Connection的ConnectionFactory
	 * @param spec the ConnectionSpec for the desired Connection (may be {@code null}). Connection需要的ConnectionSpec（可能是null）<br>
	 * Note: If this is specified, a new Connection will be obtained for every call,
	 * without participating in a shared transactional Connection.
	 *             <br>
	 *             如果指定了此选项，则将为每个调用返回一个新的Connection，而无需参与共享的事务性Connection。
	 * @return a CCI Connection from the given ConnectionFactory
	 * @throws org.springframework.jca.cci.CannotGetCciConnectionException
	 * if the attempt to get a Connection failed
	 * @see #releaseConnection
	 */
	public static Connection getConnection(ConnectionFactory cf, @Nullable ConnectionSpec spec)
			throws CannotGetCciConnectionException {
		try {
			if (spec != null) {
				Assert.notNull(cf, "No ConnectionFactory specified");
				return cf.getConnection(spec);
			}
			else {
				return doGetConnection(cf);
			}
		}
		catch (ResourceException ex) {
			throw new CannotGetCciConnectionException("Could not get CCI Connection", ex);
		}
	}

	/**
	 * Actually obtain a CCI Connection from the given ConnectionFactory.
	 * Same as {@link #getConnection}, but throwing the original ResourceException.
	 * <br>
	 *     实际上是从给定的ConnectionFactory获得一个CCI连接。
	 *     与{@link #getConnection}相同，但是抛出原始的ResourceException。
	 *
	 * <p>Is aware of a corresponding Connection bound to the current thread, for example
	 * when using {@link CciLocalTransactionManager}. Will bind a Connection to the thread
	 * if transaction synchronization is active (e.g. if in a JTA transaction).
	 * <p>Directly accessed by {@link TransactionAwareConnectionFactoryProxy}.
	 * @param cf the ConnectionFactory to obtain Connection from
	 * @return a CCI Connection from the given ConnectionFactory
	 * @throws ResourceException if thrown by CCI API methods
	 * @see #doReleaseConnection
	 */
	public static Connection doGetConnection(ConnectionFactory cf) throws ResourceException {
		Assert.notNull(cf, "No ConnectionFactory specified");

		ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(cf);
		if (conHolder != null) {
			return conHolder.getConnection();
		}

		logger.debug("Opening CCI Connection");
		Connection con = cf.getConnection();

		//判断当前事务是否激活
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			conHolder = new ConnectionHolder(con);
			conHolder.setSynchronizedWithTransaction(true);
			//注册事务同步
			TransactionSynchronizationManager.registerSynchronization(new ConnectionSynchronization(conHolder, cf));
			//通过指定键将给定资源绑定到当前线程
			TransactionSynchronizationManager.bindResource(cf, conHolder);
		}

		return con;
	}

	/**
	 * Determine whether the given JCA CCI Connection is transactional, that is,
	 * bound to the current thread by Spring's transaction facilities.
	 * <br>
	 *     确定给定的JCA CCI连接是否是事务性的，即是否由Spring的事务功能绑定到当前线程。
	 * @param con the Connection to check
	 * @param cf the ConnectionFactory that the Connection was obtained from
	 * (may be {@code null})
	 * @return whether the Connection is transactional
	 */
	public static boolean isConnectionTransactional(Connection con, @Nullable ConnectionFactory cf) {
		if (cf == null) {
			return false;
		}
		ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(cf);
		return (conHolder != null && conHolder.getConnection() == con);
	}

	/**
	 * Close the given Connection, obtained from the given ConnectionFactory,
	 * if it is not managed externally (that is, not bound to the thread).
	 * <br>
	 *     如果未从外部管理（即未绑定到线程），则关闭从给定ConnectionFactory获得的给定Connection。
	 * @param con the Connection to close if necessary
	 * (if this is {@code null}, the call will be ignored)
	 * @param cf the ConnectionFactory that the Connection was obtained from
	 * (can be {@code null})
	 * @see #getConnection
	 */
	public static void releaseConnection(@Nullable Connection con, @Nullable ConnectionFactory cf) {
		try {
			doReleaseConnection(con, cf);
		}
		catch (ResourceException ex) {
			logger.debug("Could not close CCI Connection", ex);
		}
		catch (Throwable ex) {
			// We don't trust the CCI driver: It might throw RuntimeException or Error.
			logger.debug("Unexpected exception on closing CCI Connection", ex);
		}
	}

	/**
	 * Actually close the given Connection, obtained from the given ConnectionFactory.
	 * Same as {@link #releaseConnection}, but throwing the original ResourceException.
	 * <p>Directly accessed by {@link TransactionAwareConnectionFactoryProxy}.
	 * <p>
	 *     实际上关闭从给定ConnectionFactory获得的给定Connection。
	 *     与{@link #releaseConnection}相同，但是抛出原始的ResourceException。
	 * 		由TransactionAwareConnectionFactoryProxy直接访问。
	 * </p>
	 * @param con the Connection to close if necessary
	 * (if this is {@code null}, the call will be ignored)
	 * @param cf the ConnectionFactory that the Connection was obtained from
	 * (can be {@code null})
	 * @throws ResourceException if thrown by JCA CCI methods
	 * @see #doGetConnection
	 */
	public static void doReleaseConnection(@Nullable Connection con, @Nullable ConnectionFactory cf)
			throws ResourceException {

		if (con == null || isConnectionTransactional(con, cf)) {
			return;
		}
		con.close();
	}


	/**
	 * Callback for resource cleanup at the end of a non-native CCI transaction
	 * (e.g. when participating in a JTA transaction).
	 * <br>
	 *     在非本地CCI事务结束时（例如，在参与JTA事务时）回调资源清理
	 */
	private static class ConnectionSynchronization
			extends ResourceHolderSynchronization<ConnectionHolder, ConnectionFactory> {

		public ConnectionSynchronization(ConnectionHolder connectionHolder, ConnectionFactory connectionFactory) {
			super(connectionHolder, connectionFactory);
		}

		@Override
		protected void releaseResource(ConnectionHolder resourceHolder, ConnectionFactory resourceKey) {
			releaseConnection(resourceHolder.getConnection(), resourceKey);
		}
	}

}

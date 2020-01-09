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

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.LocalTransactionException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.transaction.PlatformTransactionManager} implementation
 * that manages local transactions for a single CCI ConnectionFactory.
 * Binds a CCI Connection from the specified ConnectionFactory to the thread,
 * potentially allowing for one thread-bound Connection per ConnectionFactory.
 *
 * <br>
 *     {@link org.springframework.transaction.PlatformTransactionManager}实现，
 *     用于管理单个CCI ConnectionFactory的本地事务。
 *     将CCI连接从指定的ConnectionFactory绑定到线程，可能允许每个ConnectionFactory一个线程绑定的Connection。
 *
 * <p>Application code is required to retrieve the CCI Connection via
 * {@link ConnectionFactoryUtils#getConnection(ConnectionFactory)} instead of a standard
 * Java EE-style {@link ConnectionFactory#getConnection()} call. Spring classes such as
 * {@link org.springframework.jca.cci.core.CciTemplate} use this strategy implicitly.
 * If not used in combination with this transaction manager, the
 * {@link ConnectionFactoryUtils} lookup strategy behaves exactly like the native
 * DataSource lookup; it can thus be used in a portable fashion.
 *
 * <p>
 *     需要程序通过{@link ConnectionFactoryUtils#getConnection(ConnectionFactory)} 而不是标准的Java EE风格
 *     {@link ConnectionFactory#getConnection()}来调用CCI连接。
 *     Spring 比如{@link org.springframework.jca.cci.core.CciTemplate}隐含的使用了此策略。
 *     如果未跟该事务管理器组合使用，则{@link ConnectionFactoryUtils}的表现方式跟使用DataSource一样；
 *     因此可以很方便的使用。
 * </p>
 *
 * <p>Alternatively, you can allow application code to work with the standard
 * Java EE lookup pattern {@link ConnectionFactory#getConnection()}, for example
 * for legacy code that is not aware of Spring at all. In that case, define a
 * {@link TransactionAwareConnectionFactoryProxy} for your target ConnectionFactory,
 * which will automatically participate in Spring-managed transactions.
 *
 * <p>
 *     另外，您可以允许应用程序代码与标准Java EE查找模式{@link ConnectionFactory#getConnection()}配合使用，
 *     例如，针对根本不了解Spring的旧代码。
 *     在这种情况下，请为目标ConnectionFactory定义一个{@link TransactionAwareConnectionFactoryProxy}，它将自动参与Spring管理的事务。
 * </p>
 *
 * @author Thierry Templier
 * @author Juergen Hoeller
 * @since 1.2
 * @see ConnectionFactoryUtils#getConnection(javax.resource.cci.ConnectionFactory)
 * @see ConnectionFactoryUtils#releaseConnection
 * @see TransactionAwareConnectionFactoryProxy
 * @see org.springframework.jca.cci.core.CciTemplate
 */
@SuppressWarnings("serial")
public class CciLocalTransactionManager extends AbstractPlatformTransactionManager
		implements ResourceTransactionManager, InitializingBean {

	@Nullable
	private ConnectionFactory connectionFactory;


	/**
	 * Create a new CciLocalTransactionManager instance.
	 * A ConnectionFactory has to be set to be able to use it.
	 * <br>
	 *     创建一个新的CciLocalTransactionManager实例。必须将ConnectionFactory设置为可以使用它
	 * @see #setConnectionFactory
	 */
	public CciLocalTransactionManager() {
	}

	/**
	 * Create a new CciLocalTransactionManager instance.
	 * <br>
	 *     创建一个CciLocalTransactionManager实例
	 * @param connectionFactory the CCI ConnectionFactory to manage local transactions for <br>CCI ConnectionFactory用于管理本地事务
	 */
	public CciLocalTransactionManager(ConnectionFactory connectionFactory) {
		setConnectionFactory(connectionFactory);
		afterPropertiesSet();
	}


	/**
	 * Set the CCI ConnectionFactory that this instance should manage local
	 * transactions for.
	 * <br>
	 *     设置CCI ConnectionFactory实例用来管理本地事务
	 */
	public void setConnectionFactory(@Nullable ConnectionFactory cf) {
		//如果是 CCI ConnectionFactory代理
		if (cf instanceof TransactionAwareConnectionFactoryProxy) {
			// If we got a TransactionAwareConnectionFactoryProxy, we need to perform transactions
			// for its underlying target ConnectionFactory, else JMS access code won't see
			// properly exposed transactions (i.e. transactions for the target ConnectionFactory).
			//如果获得TransactionAwareConnectionFactoryProxy，
			// 则需要为其基础目标ConnectionFactory执行事务，
			// 否则JMS访问代码将看不到正确公开的事务（即目标ConnectionFactory的事务）。
			this.connectionFactory = ((TransactionAwareConnectionFactoryProxy) cf).getTargetConnectionFactory();
		}
		else {
			this.connectionFactory = cf;
		}
	}

	/**
	 * Return the CCI ConnectionFactory that this instance manages local
	 * transactions for.
	 * <br>
	 *     返回CCI ConnectionFactory实例
	 */
	@Nullable
	public ConnectionFactory getConnectionFactory() {
		return this.connectionFactory;
	}

	private ConnectionFactory obtainConnectionFactory() {
		ConnectionFactory connectionFactory = getConnectionFactory();
		Assert.state(connectionFactory != null, "No ConnectionFactory set");
		return connectionFactory;
	}

	@Override
	public void afterPropertiesSet() {
		if (getConnectionFactory() == null) {
			throw new IllegalArgumentException("Property 'connectionFactory' is required");
		}
	}


	@Override
	public Object getResourceFactory() {
		return obtainConnectionFactory();
	}

	@Override
	protected Object doGetTransaction() {
		//CCI本地事务对象
		CciLocalTransactionObject txObject = new CciLocalTransactionObject();
		//通过指定ConnectionFactory实例获取资源
		ConnectionHolder conHolder =
				(ConnectionHolder) TransactionSynchronizationManager.getResource(obtainConnectionFactory());
		txObject.setConnectionHolder(conHolder);
		return txObject;
	}

	@Override
	protected boolean isExistingTransaction(Object transaction) {
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) transaction;
		// Consider a pre-bound connection as transaction.
		//将预绑定的连接视为事务，实际就是判断connectionHolder是否为null
		return txObject.hasConnectionHolder();
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		//开始事务
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) transaction;
		//获取ConnectionFactory实例
		ConnectionFactory connectionFactory = obtainConnectionFactory();
		Connection con = null;

		try {
			con = connectionFactory.getConnection();
			if (logger.isDebugEnabled()) {
				logger.debug("Acquired Connection [" + con + "] for local CCI transaction");
			}

			//将连接封装到ConnectionHolder
			ConnectionHolder connectionHolder = new ConnectionHolder(con);
			//设置同步
			connectionHolder.setSynchronizedWithTransaction(true);

			//在EIS实例上开始本地事务。
			con.getLocalTransaction().begin();
			//获取超时时间
			int timeout = determineTimeout(definition);
			//不希望设置默认超时时间
			if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
				connectionHolder.setTimeoutInSeconds(timeout);
			}

			txObject.setConnectionHolder(connectionHolder);
			TransactionSynchronizationManager.bindResource(connectionFactory, connectionHolder);
		}
		catch (NotSupportedException ex) {
			ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
			throw new CannotCreateTransactionException("CCI Connection does not support local transactions", ex);
		}
		catch (LocalTransactionException ex) {
			ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
			throw new CannotCreateTransactionException("Could not begin local CCI transaction", ex);
		}
		catch (Throwable ex) {
			ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
			throw new TransactionSystemException("Unexpected failure on begin of CCI local transaction", ex);
		}
	}

	@Override
	protected Object doSuspend(Object transaction) {
		//挂起事务
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) transaction;
		txObject.setConnectionHolder(null);
		return TransactionSynchronizationManager.unbindResource(obtainConnectionFactory());
	}

	@Override
	protected void doResume(@Nullable Object transaction, Object suspendedResources) {
		//恢复事务
		ConnectionHolder conHolder = (ConnectionHolder) suspendedResources;
		TransactionSynchronizationManager.bindResource(obtainConnectionFactory(), conHolder);
	}

	/**
	 * 事务是否允许回滚
	 * @param transaction
	 * @return
	 * @throws TransactionException
	 */
	protected boolean isRollbackOnly(Object transaction) throws TransactionException {
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) transaction;
		return txObject.getConnectionHolder().isRollbackOnly();
	}


	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		//提交事务
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) status.getTransaction();
		Connection con = txObject.getConnectionHolder().getConnection();
		if (status.isDebug()) {
			logger.debug("Committing CCI local transaction on Connection [" + con + "]");
		}
		try {
			con.getLocalTransaction().commit();
		}
		catch (LocalTransactionException ex) {
			throw new TransactionSystemException("Could not commit CCI local transaction", ex);
		}
		catch (ResourceException ex) {
			throw new TransactionSystemException("Unexpected failure on commit of CCI local transaction", ex);
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		//回滚事务
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) status.getTransaction();
		Connection con = txObject.getConnectionHolder().getConnection();
		if (status.isDebug()) {
			logger.debug("Rolling back CCI local transaction on Connection [" + con + "]");
		}
		try {
			con.getLocalTransaction().rollback();
		}
		catch (LocalTransactionException ex) {
			throw new TransactionSystemException("Could not roll back CCI local transaction", ex);
		}
		catch (ResourceException ex) {
			throw new TransactionSystemException("Unexpected failure on rollback of CCI local transaction", ex);
		}
	}

	@Override
	protected void doSetRollbackOnly(DefaultTransactionStatus status) {
		//设置事务回滚状态
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Setting CCI local transaction [" + txObject.getConnectionHolder().getConnection() +
					"] rollback-only");
		}
		txObject.getConnectionHolder().setRollbackOnly();
	}

	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		//事务完成后清理资源
		CciLocalTransactionObject txObject = (CciLocalTransactionObject) transaction;
		ConnectionFactory connectionFactory = obtainConnectionFactory();

		// Remove the connection holder from the thread.
		TransactionSynchronizationManager.unbindResource(connectionFactory);
		txObject.getConnectionHolder().clear();

		Connection con = txObject.getConnectionHolder().getConnection();
		if (logger.isDebugEnabled()) {
			logger.debug("Releasing CCI Connection [" + con + "] after transaction");
		}
		ConnectionFactoryUtils.releaseConnection(con, connectionFactory);
	}


	/**
	 * CCI local transaction object, representing a ConnectionHolder.
	 * Used as transaction object by CciLocalTransactionManager.
	 * <br>
	 *     CCI本地事务对象，代表ConnectionHolder。由CciLocalTransactionManager用作事务对象。
	 * @see ConnectionHolder
	 */
	private static class CciLocalTransactionObject {

		@Nullable
		private ConnectionHolder connectionHolder;

		public void setConnectionHolder(@Nullable ConnectionHolder connectionHolder) {
			this.connectionHolder = connectionHolder;
		}

		public ConnectionHolder getConnectionHolder() {
			Assert.state(this.connectionHolder != null, "No ConnectionHolder available");
			return this.connectionHolder;
		}

		public boolean hasConnectionHolder() {
			return (this.connectionHolder != null);
		}
	}

}

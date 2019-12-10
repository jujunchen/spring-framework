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

package org.springframework.dao.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;

/**
 * Generic base class for DAOs, defining template methods for DAO initialization.
 *
 * <p>Extended by Spring's specific DAO support classes, such as:
 * JdbcDaoSupport, JdoDaoSupport, etc.
 *
 * <p>
 *     DAO的基础类，定义DAO的初始化模版方法
 * </p>
 *
 * @author Juergen Hoeller
 * @since 1.2.2
 * @see org.springframework.jdbc.core.support.JdbcDaoSupport
 */
public abstract class DaoSupport implements InitializingBean {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());


	@Override
	public final void afterPropertiesSet() throws IllegalArgumentException, BeanInitializationException {
		// Let abstract subclasses check their configuration.
		//由子类去实现，检查配置
		checkDaoConfig();

		// Let concrete implementations initialize themselves.
		//让具体的子类去实现初始化
		try {
			initDao();
		}
		catch (Exception ex) {
			throw new BeanInitializationException("Initialization of DAO failed", ex);
		}
	}

	/**
	 * Abstract subclasses must override this to check their configuration.
	 * <p>Implementors should be marked as {@code final} if concrete subclasses
	 * are not supposed to override this template method themselves.
	 * <p>
	 *     抽象子类必须重写此属性以检查其配置。如果子类本身不需要再覆盖此模板方法，则应将实现标记为final。
	 * </p>
	 * @throws IllegalArgumentException in case of illegal configuration
	 */
	protected abstract void checkDaoConfig() throws IllegalArgumentException;

	/**
	 * Concrete subclasses can override this for custom initialization behavior.
	 * Gets called after population of this instance's bean properties.
	 * <p>
	 *     具体的子类可以为自定义初始化行为覆盖此子类。在填充该实例的bean属性之后调用。
	 * </p>
	 * @throws Exception if DAO initialization fails
	 * (will be rethrown as a BeanInitializationException) <br>如果DAO初始化失败，抛出BeanInitializationException
	 * @see org.springframework.beans.factory.BeanInitializationException
	 */
	protected void initDao() throws Exception {
	}

}

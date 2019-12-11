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

package org.springframework.dao;

/**
 * Data access exception thrown when a previously failed operation might be able
 * to succeed if the application performs some recovery steps and retries the entire
 * transaction or in the case of a distributed transaction, the transaction branch.
 * At a minimum, the recovery operation must include closing the current connection
 * and getting a new connection.
 *
 * <P>
 *     如果应用程序执行一些恢复步骤并重试整个事务，或者在分布式事务的情况下，事务分支，则先前失败的操作可能能够成功时引发的数据访问异常。
 *     恢复操作至少必须包括关闭当前连接并获得新连接。
 * </P>
 *
 * @author Thomas Risberg
 * @since 2.5
 * @see java.sql.SQLRecoverableException
 */
@SuppressWarnings("serial")
public class RecoverableDataAccessException extends DataAccessException {

	/**
	 * Constructor for RecoverableDataAccessException.
	 * @param msg the detail message
	 */
	public RecoverableDataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for RecoverableDataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public RecoverableDataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}

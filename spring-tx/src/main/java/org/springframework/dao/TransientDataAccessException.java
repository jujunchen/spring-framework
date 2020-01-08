/*
 * Copyright 2002-2017 the original author or authors.
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

import org.springframework.lang.Nullable;

/**
 * Root of the hierarchy of data access exceptions that are considered transient -
 * where a previously failed operation might be able to succeed when the operation
 * is retried without any intervention by application-level functionality.
 *
 * <p>
 *	数据访问异常的根，这些数据被认为是瞬态的。在重试该操作之前，先前失败的操作可能能够成功，而无需应用程序级功能的任何干预
 * </p>
 *
 * @author Thomas Risberg
 * @since 2.5
 * @see java.sql.SQLTransientException
 */
@SuppressWarnings("serial")
public abstract class TransientDataAccessException extends DataAccessException {

	/**
	 * Constructor for TransientDataAccessException.
	 * @param msg the detail message
	 */
	public TransientDataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for TransientDataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public TransientDataAccessException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}

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

package org.springframework.jca.cci;

import java.sql.SQLException;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * Exception thrown when a ResultSet has been accessed in an invalid fashion.
 * Such exceptions always have a {@code java.sql.SQLException} root cause.
 *
 * 当指定了无效的ResultSet列索引或名称时，通常会发生这种情况。
 *
 * <p>This typically happens when an invalid ResultSet column index or name
 * has been specified.
 *
 * <p>
 *     以无效方式访问ResultSet时引发的异常。此类异常始终是java.sql.SQLException的根本原因。
 *
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see javax.resource.cci.ResultSet
 */
@SuppressWarnings("serial")
public class InvalidResultSetAccessException extends InvalidDataAccessResourceUsageException {

	/**
	 * Constructor for InvalidResultSetAccessException.
	 * @param msg message
	 * @param ex the root cause
	 */
	public InvalidResultSetAccessException(String msg, SQLException ex) {
		super(ex.getMessage(), ex);
	}

}

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
 * Exception thrown when we couldn't cleanup after a data access operation,
 * but the actual operation went OK.
 *
 * <p>For example, this exception or a subclass might be thrown if a JDBC
 * Connection couldn't be closed after it had been used successfully.
 *
 * <p>Note that data access code might perform resources cleanup in a
 * finally block and therefore log cleanup failure rather than rethrow it,
 * to keep the original data access exception, if any.
 *
 * <p>
 *     当我们在数据访问操作后无法清理时抛出异常，但实际操作正常。
 * <p>
 *     例如，在JDBC连接成功后无法关闭，则其或者子类可能会抛出该异常
 * <p>
 *     请注意，数据访问代码可能会在finally块中执行资源清理，因此会记录清理失败而不是抛出错误，以保留原始的数据访问异常（如果有）。
 * </p>
 *
 * @author Rod Johnson
 */
@SuppressWarnings("serial")
public class CleanupFailureDataAccessException extends NonTransientDataAccessException {

	/**
	 * Constructor for CleanupFailureDataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause from the underlying data access API,
	 * such as JDBC
	 */
	public CleanupFailureDataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}

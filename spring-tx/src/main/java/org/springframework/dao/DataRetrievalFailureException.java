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
 * Exception thrown if certain expected data could not be retrieved, e.g.
 * when looking up specific data via a known identifier. This exception
 * will be thrown either by O/R mapping tools or by DAO implementations.
 *
 * <p>
 *     如果无法检索到某些预期数据，则抛出异常，通过已知标识符查找特定数据时。 O/R映射工具或DAO实现将引发此异常。
 * </p>
 *
 * @author Juergen Hoeller
 * @since 13.10.2003
 */
@SuppressWarnings("serial")
public class DataRetrievalFailureException extends NonTransientDataAccessException {

	/**
	 * Constructor for DataRetrievalFailureException.
	 * @param msg the detail message
	 */
	public DataRetrievalFailureException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataRetrievalFailureException.
	 * @param msg the detail message
	 * @param cause the root cause from the data access API in use
	 */
	public DataRetrievalFailureException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}

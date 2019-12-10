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

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;

/**
 * Miscellaneous utility methods for DAO implementations.
 * Useful with any data access technology.
 * <p>
 *     DAO实现的其他实用程序方法
 * </p>
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 */
public abstract class DataAccessUtils {

	/**
	 * Return a single result object from the given Collection.
	 * <p>Returns {@code null} if 0 result objects found;
	 * throws an exception if more than 1 element found.
	 * <p>
	 *     从collection列表中返回单个结果。
	 *     如果collection为null 或者 为空列表，则返回null
	 *     如果collection长度大于1，则抛错
	 * </p>
	 * @param results the result Collection (can be {@code null}) <br>Collection对象，可以为null
	 * @return the single result object, or {@code null} if none <br>单个结果对象，如果没有返回null
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * element has been found in the given Collection <br>有超过1个以上的对象，则抛错
	 */
	@Nullable
	public static <T> T singleResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		return results.iterator().next();
	}

	/**
	 * Return a single result object from the given Collection.
	 * <p>Throws an exception if 0 or more than 1 element found.
	 * <p>
	 *     相比{@link #singleResult}严格，返回单个结果，如果Collection列表为空 或者 超过1个元素，则抛错
	 * </p>
	 * @param results the result Collection (can be {@code null}
	 * but is not expected to contain {@code null} elements)
	 * @return the single result object
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * element has been found in the given Collection
	 * @throws EmptyResultDataAccessException if no element at all
	 * has been found in the given Collection
	 */
	public static <T> T requiredSingleResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
		if (CollectionUtils.isEmpty(results)) {
			throw new EmptyResultDataAccessException(1);
		}
		if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		return results.iterator().next();
	}

	/**
	 * Return a single result object from the given Collection.
	 * <p>Throws an exception if 0 or more than 1 element found.
	 * @param results the result Collection (can be {@code null}
	 * and is also expected to contain {@code null} elements)
	 * @return the single result object
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * element has been found in the given Collection
	 * @throws EmptyResultDataAccessException if no element at all
	 * has been found in the given Collection
	 * @since 5.0.2
	 */
	@Nullable
	public static <T> T nullableSingleResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
		// This is identical to the requiredSingleResult implementation but differs in the
		// semantics of the incoming Collection (which we currently can't formally express)
		if (CollectionUtils.isEmpty(results)) {
			throw new EmptyResultDataAccessException(1);
		}
		if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		return results.iterator().next();
	}

	/**
	 * Return a unique result object from the given Collection.
	 * <p>Returns {@code null} if 0 result objects found;
	 * throws an exception if more than 1 instance found.
	 * <p>
	 *     从给定的集合中返回一个唯一结果
	 * <p>
	 *     如果结果数量为0，返回null,如果超过1个结果对象抛错
	 * @param results the result Collection (can be {@code null})<br>Collection列表，可以为null
	 * @return the unique result object, or {@code null} if none<br>唯一结果，没有结果对象返回null
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * result object has been found in the given Collection<br>如果超过一个结果抛错
	 * @see org.springframework.util.CollectionUtils#hasUniqueObject
	 */
	@Nullable
	public static <T> T uniqueResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		//判断集合中是否只有一个唯一元素
		if (!CollectionUtils.hasUniqueObject(results)) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		return results.iterator().next();
	}

	/**
	 * Return a unique result object from the given Collection.
	 * <p>Throws an exception if 0 or more than 1 instance found.
	 * <p>
	 *     较{@link #uniqueResult(Collection)}严格，如果没有结果对象或者超过1个结果抛错
	 * </p>
	 * @param results the result Collection (can be {@code null}
	 * but is not expected to contain {@code null} elements)
	 * @return the unique result object
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * result object has been found in the given Collection
	 * @throws EmptyResultDataAccessException if no result object at all
	 * has been found in the given Collection
	 * @see org.springframework.util.CollectionUtils#hasUniqueObject
	 */
	public static <T> T requiredUniqueResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
		if (CollectionUtils.isEmpty(results)) {
			throw new EmptyResultDataAccessException(1);
		}
		if (!CollectionUtils.hasUniqueObject(results)) {
			throw new IncorrectResultSizeDataAccessException(1, results.size());
		}
		return results.iterator().next();
	}

	/**
	 * Return a unique result object from the given Collection.
	 * Throws an exception if 0 or more than 1 result objects found,
	 * of if the unique result object is not convertible to the
	 * specified required type.
	 * <p>
	 *     根据给定集合中返回唯一的指定类型的结果，如果结果数量为0或者大于1，或者结果不能转换为指定的类型，则抛错
	 * </p>
	 * @param results the result Collection (can be {@code null}
	 * but is not expected to contain {@code null} elements)<br>
	 *                Collection列表，可以为null
	 * @return the unique result object<br>唯一结果对象
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * result object has been found in the given Collection<br> 如果超过1个结果，则抛错
	 * @throws EmptyResultDataAccessException if no result object
	 * at all has been found in the given Collection<br>如果没找到，抛错
	 * @throws TypeMismatchDataAccessException if the unique object does
	 * not match the specified required type<br>如果结果跟指定类型不匹配，抛错
	 */
	@SuppressWarnings("unchecked")
	public static <T> T objectResult(@Nullable Collection<?> results, @Nullable Class<T> requiredType)
			throws IncorrectResultSizeDataAccessException, TypeMismatchDataAccessException {

		//获取唯一的结果
		Object result = requiredUniqueResult(results);
		//结果是否为指定类型的实例，是进行强转，否判断是否为String，或者是否为Number类型
		if (requiredType != null && !requiredType.isInstance(result)) {
			//判断指定的类型是否为String
			if (String.class == requiredType) {
				result = result.toString();
			}
			//判断指定的类型是否为Number或者是Number的超类，并且唯一结果是否为Number的子类
			else if (Number.class.isAssignableFrom(requiredType) && Number.class.isInstance(result)) {
				try {
					result = NumberUtils.convertNumberToTargetClass(((Number) result), (Class<? extends Number>) requiredType);
				}
				catch (IllegalArgumentException ex) {
					throw new TypeMismatchDataAccessException(ex.getMessage());
				}
			}
			else {
				throw new TypeMismatchDataAccessException(
						"Result object is of type [" + result.getClass().getName() +
						"] and could not be converted to required type [" + requiredType.getName() + "]");
			}
		}
		return (T) result;
	}

	/**
	 * Return a unique int result from the given Collection.
	 * Throws an exception if 0 or more than 1 result objects found,
	 * of if the unique result object is not convertible to an int.
	 * <p>
	 *     返回唯一结果的整型，如果没有结果，或者找到的结果数量超过1，或者不能转换为整型，则抛错
	 * </p>
	 * @param results the result Collection (can be {@code null}
	 * but is not expected to contain {@code null} elements)
	 * @return the unique int result
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * result object has been found in the given Collection
	 * @throws EmptyResultDataAccessException if no result object
	 * at all has been found in the given Collection
	 * @throws TypeMismatchDataAccessException if the unique object
	 * in the collection is not convertible to an int
	 */
	public static int intResult(@Nullable Collection<?> results)
			throws IncorrectResultSizeDataAccessException, TypeMismatchDataAccessException {

		return objectResult(results, Number.class).intValue();
	}

	/**
	 * Return a unique long result from the given Collection.
	 * Throws an exception if 0 or more than 1 result objects found,
	 * of if the unique result object is not convertible to a long.
	 * <p>
	 *     返回唯一结果的长整型，如果没有结果，或者找到的结果数量超过1，或者不能转换为长整型，则抛错
	 * </p>
	 * @param results the result Collection (can be {@code null}
	 * but is not expected to contain {@code null} elements)
	 * @return the unique long result
	 * @throws IncorrectResultSizeDataAccessException if more than one
	 * result object has been found in the given Collection
	 * @throws EmptyResultDataAccessException if no result object
	 * at all has been found in the given Collection
	 * @throws TypeMismatchDataAccessException if the unique object
	 * in the collection is not convertible to a long
	 */
	public static long longResult(@Nullable Collection<?> results)
			throws IncorrectResultSizeDataAccessException, TypeMismatchDataAccessException {

		return objectResult(results, Number.class).longValue();
	}


	/**
	 * Return a translated exception if this is appropriate,
	 * otherwise return the given exception as-is.
	 * <p>
	 *     如果可以转换就将原始异常转换为DataAccessException，否则抛出原始异常
	 * </p>
	 * @param rawException an exception that we may wish to translate<br>希望转换的异常
	 * @param pet the PersistenceExceptionTranslator to use to perform the translation<br>用于执行转换的PersistenceExceptionTranslator
	 * @return a translated persistence exception if translation is possible,
	 * or the raw exception if it is not<br>如果转换成功则为转换后的异常，如果没有则为原始异常
	 */
	public static RuntimeException translateIfNecessary(
			RuntimeException rawException, PersistenceExceptionTranslator pet) {

		Assert.notNull(pet, "PersistenceExceptionTranslator must not be null");
		DataAccessException dae = pet.translateExceptionIfPossible(rawException);
		return (dae != null ? dae : rawException);
	}

}

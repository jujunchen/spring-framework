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

package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.lang.Nullable;

/**
 * Interface to discover parameter names for methods and constructors.
 * <br>
 *     用于发现方法和构造函数参数名称的接口。
 *
 * <p>Parameter name discovery is not always possible, but various strategies are
 * available to try, such as looking for debug information that may have been
 * emitted at compile time, and looking for argname annotation values optionally
 * accompanying AspectJ annotated methods.
 * <p>
 *     发现参数名称可以尝试各种策略，比如通过编译时的debug信息，和 带有AspectJ注释的argname注释值
 * </p>
 *
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0
 */
public interface ParameterNameDiscoverer {

	/**
	 * Return parameter names for a method, or {@code null} if they cannot be determined.
	 * <br>
	 * 	 返回方法的参数名称；如果无法确定，则返回null.
	 * <p>Individual entries in the array may be {@code null} if parameter names are only
	 * available for some parameters of the given method but not for others. However,
	 * it is recommended to use stub parameter names instead wherever feasible.
	 * <p>
	 *     如果参数名称仅用于给定方法的某些的参数，而不适用于其他参数，则数组中的各个条目可能为null。
	 *     但是建议在可行的地方改用存根参数名称。
	 * </p>
	 *
	 * @param method the method to find parameter names for<br>需要查找参数的方法
	 * @return an array of parameter names if the names can be resolved,
	 * or {@code null} if they cannot<br>参数名称数组，如果没有找到返回null
	 */
	@Nullable
	String[] getParameterNames(Method method);

	/**
	 * Return parameter names for a constructor, or {@code null} if they cannot be determined.
	 * <br>
	 *     返回构造函数参数名称，如果无法确定返回null
	 * <p>Individual entries in the array may be {@code null} if parameter names are only
	 * available for some parameters of the given constructor but not for others. However,
	 * it is recommended to use stub parameter names instead wherever feasible.
	 * @param ctor the constructor to find parameter names for<br>需要查找参数的构造方法
	 * @return an array of parameter names if the names can be resolved,
	 * or {@code null} if they cannot
	 */
	@Nullable
	String[] getParameterNames(Constructor<?> ctor);

}

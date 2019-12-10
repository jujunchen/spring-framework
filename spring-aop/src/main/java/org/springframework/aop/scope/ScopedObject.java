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

package org.springframework.aop.scope;

import org.springframework.aop.RawTargetAccess;

/**
 * An AOP introduction interface for scoped objects.
 *
 * <p>Objects created from the {@link ScopedProxyFactoryBean} can be cast
 * to this interface, enabling access to the raw target object
 * and programmatic removal of the target object.
 *
 * <p>
 *     可以将从{@link ScopedProxyFactoryBean}创建的对象强制转换为此接口，从而可以访问原始目标对象并以编程方式删除目标对象。
 * </p>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see ScopedProxyFactoryBean
 */
public interface ScopedObject extends RawTargetAccess {

	/**
	 * Return the current target object behind this scoped object proxy,
	 * in its raw form (as stored in the target scope).
	 * <p>The raw target object can for example be passed to persistence
	 * providers which would not be able to handle the scoped proxy object.
	 * <p>
	 *     以原始格式（存储在目标作用域中）返回此作用域对象代理后的目标对象。原始目标对象可以例如传递给持久性提供程序，后者将无法处理作用域代理对象。
	 * </p>
	 * @return the current target object behind this scoped object proxy<br>
	 *     返回目标对象被代理过后的对象
	 */
	Object getTargetObject();

	/**
	 * Remove this object from its target scope, for example from
	 * the backing session.
	 * <p>Note that no further calls may be made to the scoped object
	 * afterwards (at least within the current thread, that is, with
	 * the exact same target object in the target scope).
	 *
	 * <p>
	 *     从生命周期中删除这个对象，比如后台session。删除后，不能再调用该生命周期内的对象。
	 *
	 * </p>
	 */
	void removeFromScope();

}

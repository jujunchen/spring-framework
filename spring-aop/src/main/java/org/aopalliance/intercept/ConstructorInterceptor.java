/*
 * Copyright 2002-2016 the original author or authors.
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

package org.aopalliance.intercept;

/**
 * Intercepts the construction of a new object.
 * <br>
 *     拦截新对象的构造。
 *
 * <p>The user should implement the {@link
 * #construct(ConstructorInvocation)} method to modify the original
 * behavior. E.g. the following class implements a singleton
 * interceptor (allows only one unique instance for the intercepted
 * class):
 *
 * <p>
 * 用户应该实现{@link #construct(ConstructorInvocation)}方法来修改原始行为。
 * 例如,以下类实现了单例拦截器（被拦截的类仅允许一个唯一的实例）：
 * </p>
 *
 * <pre class=code>
 * class DebuggingInterceptor implements ConstructorInterceptor {
 *   Object instance=null;
 *
 *   Object construct(ConstructorInvocation i) throws Throwable {
 *     if(instance==null) {
 *       return instance=i.proceed();
 *     } else {
 *       throw new Exception("singleton does not allow multiple instance");
 *     }
 *   }
 * }
 * </pre>
 *
 * @author Rod Johnson
 */
public interface ConstructorInterceptor extends Interceptor  {

	/**
	 * Implement this method to perform extra treatments before and
	 * after the construction of a new object. Polite implementations
	 * would certainly like to invoke {@link Joinpoint#proceed()}.
	 * <br>
	 *     实现此方法可以在构造新对象之前和之后执行额外的处理。
	 *     友好的实现是调用{@link Joinpoint#proceed()}。
	 *
	 * @param invocation the construction joinpoint <br>构造函数连接点
	 * @return the newly created object, which is also the result of
	 * the call to {@link Joinpoint#proceed()}; might be replaced by
	 * the interceptor
	 * <br>
	 *     新创建的对象，这也是对{@link Joinpoint#proceed()}的调用结果；可能被拦截器取代
	 * @throws Throwable if the interceptors or the target object
	 * throws an exception
	 */
	Object construct(ConstructorInvocation invocation) throws Throwable;

}

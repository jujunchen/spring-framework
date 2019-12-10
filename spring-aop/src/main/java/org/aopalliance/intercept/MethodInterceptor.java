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

package org.aopalliance.intercept;

/**
 * Intercepts calls on an interface on its way to the target. These
 * are nested "on top" of the target.
 *
 * <p>The user should implement the {@link #invoke(MethodInvocation)}
 * method to modify the original behavior. E.g. the following class
 * implements a tracing interceptor (traces all the calls on the
 * intercepted method(s)):
 *
 * <pre class=code>
 * class TracingInterceptor implements MethodInterceptor {
 *   Object invoke(MethodInvocation i) throws Throwable {
 *     System.out.println("method "+i.getMethod()+" is called on "+
 *                        i.getThis()+" with args "+i.getArguments());
 *     Object ret=i.proceed();
 *     System.out.println("method "+i.getMethod()+" returns "+ret);
 *     return ret;
 *   }
 * }
 * </pre>
 *
 * <p>
 *     拦截目标方法，在目标方法之前执行。
 * <p>
 *	实现{@link #invoke(MethodInvocation)}方法能够拦截目标方法，并修改其行为
 *
 * @author Rod Johnson
 */
@FunctionalInterface
public interface MethodInterceptor extends Interceptor {

	/**
	 * Implement this method to perform extra treatments before and
	 * after the invocation. Polite implementations would certainly
	 * like to invoke {@link Joinpoint#proceed()}.
	 * <p>
	 *     实现该方法，能够在目标方法执行{@link Joinpoint#proceed()}前或者后进行额外的处理
	 * </p>
	 * @param invocation the method invocation joinpoint  <br>方法调用切点
	 * @return the result of the call to {@link Joinpoint#proceed()};
	 * might be intercepted by the interceptor  <br>返回{@link Joinpoint#proceed()}调用结果，有可能会被拦截器拦截修改
	 * @throws Throwable if the interceptors or the target object
	 * throws an exception
	 */
	Object invoke(MethodInvocation invocation) throws Throwable;

}

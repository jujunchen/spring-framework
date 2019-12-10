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

package org.springframework.aop.support;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Convenient implementation of the
 * {@link org.springframework.aop.IntroductionInterceptor} interface.
 *
 * <p>Subclasses merely need to extend this class and implement the interfaces
 * to be introduced themselves. In this case the delegate is the subclass
 * instance itself. Alternatively a separate delegate may implement the
 * interface, and be set via the delegate bean property.
 *
 * <p>Delegates or subclasses may implement any number of interfaces.
 * All interfaces except IntroductionInterceptor are picked up from
 * the subclass or delegate by default.
 *
 * <p>The {@code suppressInterface} method can be used to suppress interfaces
 * implemented by the delegate but which should not be introduced to the owning
 * AOP proxy.
 *
 * <p>An instance of this class is serializable if the delegate is.
 *
 * <p>{@link org.springframework.aop.IntroductionInterceptor} 接口的实现
 * 子类只需要扩展该类并实现要引入的接口。在本例中，委托是子类实例本身。另外，一个单独的委托可以实现接口，并通过委托bean属性进行设置。
 *
 * <p>委托或子类可以实现任意数量的接口。默认情况下，除了导入拦截器之外的所有接口都从子类或委托中获取。
 *
 * <p>{@code suppressInterface}方法可以用来抑制由委托实现的接口，但是不应该将其引入到拥有AOP代理中。
 *
 * <p>如果委托是可序列化的，则该类的实例也是可序列化的。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16.11.2003
 * @see #suppressInterface
 * @see DelegatePerTargetObjectIntroductionInterceptor
 */
@SuppressWarnings("serial")
public class DelegatingIntroductionInterceptor extends IntroductionInfoSupport
		implements IntroductionInterceptor {

	/**
	 * Object that actually implements the interfaces.
	 * May be "this" if a subclass implements the introduced interfaces.
	 */
	@Nullable
	private Object delegate;


	/**
	 * Construct a new DelegatingIntroductionInterceptor, providing
	 * a delegate that implements the interfaces to be introduced.
	 * @param delegate the delegate that implements the introduced interfaces
	 */
	public DelegatingIntroductionInterceptor(Object delegate) {
		init(delegate);
	}

	/**
	 * Construct a new DelegatingIntroductionInterceptor.
	 * The delegate will be the subclass, which must implement
	 * additional interfaces.
	 */
	protected DelegatingIntroductionInterceptor() {
		init(this);
	}


	/**
	 * Both constructors use this init method, as it is impossible to pass
	 * a "this" reference from one constructor to another.
	 * @param delegate the delegate object
	 */
	private void init(Object delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
		implementInterfacesOnObject(delegate);

		// We don't want to expose the control interface
		suppressInterface(IntroductionInterceptor.class);
		suppressInterface(DynamicIntroductionAdvice.class);
	}


	/**
	 * Subclasses may need to override this if they want to perform custom
	 * behaviour in around advice. However, subclasses should invoke this
	 * method, which handles introduced interfaces and forwarding to the target.
	 */
	@Override
	@Nullable
	public Object invoke(MethodInvocation mi) throws Throwable {
		if (isMethodOnIntroducedInterface(mi)) {
			// Using the following method rather than direct reflection, we
			// get correct handling of InvocationTargetException
			// if the introduced method throws an exception.
			Object retVal = AopUtils.invokeJoinpointUsingReflection(this.delegate, mi.getMethod(), mi.getArguments());

			// Massage return value if possible: if the delegate returned itself,
			// we really want to return the proxy.
			if (retVal == this.delegate && mi instanceof ProxyMethodInvocation) {
				Object proxy = ((ProxyMethodInvocation) mi).getProxy();
				if (mi.getMethod().getReturnType().isInstance(proxy)) {
					retVal = proxy;
				}
			}
			return retVal;
		}

		return doProceed(mi);
	}

	/**
	 * Proceed with the supplied {@link org.aopalliance.intercept.MethodInterceptor}.
	 * Subclasses can override this method to intercept method invocations on the
	 * target object which is useful when an introduction needs to monitor the object
	 * that it is introduced into. This method is <strong>never</strong> called for
	 * {@link MethodInvocation MethodInvocations} on the introduced interfaces.
	 */
	protected Object doProceed(MethodInvocation mi) throws Throwable {
		// If we get here, just pass the invocation on.
		return mi.proceed();
	}

}

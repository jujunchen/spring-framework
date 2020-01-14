/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.aop.aspectj.annotation;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.lang.Nullable;

/**
 * Interface for factories that can create Spring AOP Advisors from classes
 * annotated with AspectJ annotation syntax.
 *<br>
 *     工厂接口，使用AspectJ的注解创建Spring AOP Advisors
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjTypeSystem
 */
public interface AspectJAdvisorFactory {

	/**
	 * Determine whether or not the given class is an aspect, as reported
	 * by AspectJ's {@link org.aspectj.lang.reflect.AjTypeSystem}.
	 * <br>
	 *     根据AspectJ的{@link org.aspectj.lang.reflect.AjTypeSystem}确定给定的类是否是一个aspect
	 *
	 * <p>Will simply return {@code false} if the supposed aspect is
	 * invalid (such as an extension of a concrete aspect class).
	 * Will return true for some aspects that Spring AOP cannot process,
	 * such as those with unsupported instantiation models.
	 * Use the {@link #validate} method to handle these cases if necessary.
	 * <p>
	 *     如果假定的aspect无效（例如，具体aspect类的扩展），则将仅返回false。
	 *     对于Spring AOP无法处理的某些方面，例如具有不受支持的实例化模型的方面，将返回true。
	 *     如有必要，请使用{@link #validate}方法处理这些情况。
	 * </p>
	 * @param clazz the supposed annotation-style AspectJ class  <br>需要判断的假设支持AspectJ注解风格的类
	 * @return whether or not this class is recognized by AspectJ as an aspect class <br>返回是否是一个aspect类
	 */
	boolean isAspect(Class<?> clazz);

	/**
	 * Is the given class a valid AspectJ aspect class?
	 * <br>给定的Class类是否是一个正确的aspect 类
	 * @param aspectClass the supposed AspectJ annotation-style class to validate <br>需要验证的aspect类
	 * @throws AopConfigException if the class is an invalid aspect
	 * (which can never be legal)
	 * @throws NotAnAtAspectException if the class is not an aspect at all
	 * (which may or may not be legal, depending on the context)
	 */
	void validate(Class<?> aspectClass) throws AopConfigException;

	/**
	 * Build Spring AOP Advisors for all annotated At-AspectJ methods
	 * on the specified aspect instance.
	 * <br>
	 *     为所有提供了AspectJ注解的实例构建Spring AOP 通知器
	 * @param aspectInstanceFactory the aspect instance factory
	 * (not the aspect instance itself in order to avoid eager instantiation)<br>切面实例工厂，不是切面实例本身，为了避免过早实例化
	 * @return a list of advisors for this class
	 */
	List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory);

	/**
	 * Build a Spring AOP Advisor for the given AspectJ advice method.
	 * <br>
	 *     使用给定的AspectJ通知方法构建Spring AOP通知器
	 * @param candidateAdviceMethod the candidate advice method <br>通知方法
	 * @param aspectInstanceFactory the aspect instance factory<br>切面实例工厂
	 * @param declarationOrder the declaration order within the aspect<br>切面声明顺序
	 * @param aspectName the name of the aspect<br>切面名字
	 * @return {@code null} if the method is not an AspectJ advice method
	 * or if it is a pointcut that will be used by other advice but will not
	 * create a Spring advice in its own right<br>
	 *     如果该方法不是AspectJ通知方法，或者它是由其他通知使用但自身不会创建Spring通知的切入点，则为null
	 */
	@Nullable
	Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
			int declarationOrder, String aspectName);

	/**
	 * Build a Spring AOP Advice for the given AspectJ advice method.
	 * <br>
	 *     使用给定的AspectJ建议方法构建Spring AOP通知
	 * @param candidateAdviceMethod the candidate advice method<br>通知方法
	 * @param expressionPointcut the AspectJ expression pointcut<br>切面表达式
	 * @param aspectInstanceFactory the aspect instance factory<br>切面实例工厂
	 * @param declarationOrder the declaration order within the aspect<br>切面声明顺序
	 * @param aspectName the name of the aspect<br>切面名字
	 * @return {@code null} if the method is not an AspectJ advice method
	 * or if it is a pointcut that will be used by other advice but will not
	 * create a Spring advice in its own right
	 * @see org.springframework.aop.aspectj.AspectJAroundAdvice
	 * @see org.springframework.aop.aspectj.AspectJMethodBeforeAdvice
	 * @see org.springframework.aop.aspectj.AspectJAfterAdvice
	 * @see org.springframework.aop.aspectj.AspectJAfterReturningAdvice
	 * @see org.springframework.aop.aspectj.AspectJAfterThrowingAdvice
	 */
	@Nullable
	Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
			MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);

}

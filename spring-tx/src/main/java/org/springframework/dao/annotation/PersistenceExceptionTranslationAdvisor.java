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

package org.springframework.dao.annotation;

import java.lang.annotation.Annotation;

import org.aopalliance.aop.Advice;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.support.PersistenceExceptionTranslationInterceptor;
import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * Spring AOP exception translation aspect for use at Repository or DAO layer level.
 * Translates native persistence exceptions into Spring's DataAccessException hierarchy,
 * based on a given PersistenceExceptionTranslator.
 * <p>
 *     使用在Repository 或者 DAO层的Spring AOP异常转换切面。
 *     根据给定的PersistenceExceptionTranslator将本地持久性异常转换为Spring的DataAccessException
 *     使用编程方式实现的AOP异常拦截
 * </p>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.dao.DataAccessException
 * @see org.springframework.dao.support.PersistenceExceptionTranslator
 */
@SuppressWarnings("serial")
public class PersistenceExceptionTranslationAdvisor extends AbstractPointcutAdvisor {

	/**
	 * 持久层异常拦截器
	 */
	private final PersistenceExceptionTranslationInterceptor advice;

	/**
	 * 注解匹配器
	 */
	private final AnnotationMatchingPointcut pointcut;


	/**
	 * Create a new PersistenceExceptionTranslationAdvisor.
	 * <p>
	 *     创建一个新的PersistenceExceptionTranslationAdvisor
	 * </p>
	 * @param persistenceExceptionTranslator the PersistenceExceptionTranslator to use <br>要使用的PersistenceExceptionTranslator
	 * @param repositoryAnnotationType the annotation type to check for <br>要检查的注解
	 */
	public PersistenceExceptionTranslationAdvisor(
			PersistenceExceptionTranslator persistenceExceptionTranslator,
			Class<? extends Annotation> repositoryAnnotationType) {

		this.advice = new PersistenceExceptionTranslationInterceptor(persistenceExceptionTranslator);
		this.pointcut = new AnnotationMatchingPointcut(repositoryAnnotationType, true);
	}

	/**
	 * Create a new PersistenceExceptionTranslationAdvisor.
	 * <p>
	 *     创建一个新的 PersistenceExceptionTranslationAdvisor
	 * </p>
	 * @param beanFactory the ListableBeanFactory to obtaining all
	 * PersistenceExceptionTranslators from <br>指定需要获取PersistenceExceptionTranslators的BeanFactory
	 * @param repositoryAnnotationType the annotation type to check for<br>要检查的注解
	 */
	PersistenceExceptionTranslationAdvisor(
			ListableBeanFactory beanFactory, Class<? extends Annotation> repositoryAnnotationType) {

		this.advice = new PersistenceExceptionTranslationInterceptor(beanFactory);
		this.pointcut = new AnnotationMatchingPointcut(repositoryAnnotationType, true);
	}


	@Override
	public Advice getAdvice() {
		return this.advice;
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

}

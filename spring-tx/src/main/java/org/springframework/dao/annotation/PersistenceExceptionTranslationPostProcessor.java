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

package org.springframework.dao.annotation;

import java.lang.annotation.Annotation;

import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Bean post-processor that automatically applies persistence exception translation to any
 * bean marked with Spring's @{@link org.springframework.stereotype.Repository Repository}
 * annotation, adding a corresponding {@link PersistenceExceptionTranslationAdvisor} to
 * the exposed proxy (either an existing AOP proxy or a newly generated proxy that
 * implements all of the target's interfaces).
 *
 * <p>Translates native resource exceptions to Spring's
 * {@link org.springframework.dao.DataAccessException DataAccessException} hierarchy.
 * Autodetects beans that implement the
 * {@link org.springframework.dao.support.PersistenceExceptionTranslator
 * PersistenceExceptionTranslator} interface, which are subsequently asked to translate
 * candidate exceptions.
 *

 * <p>All of Spring's applicable resource factories (e.g.
 * {@link org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean})
 * implement the {@code PersistenceExceptionTranslator} interface out of the box.
 * As a consequence, all that is usually needed to enable automatic exception
 * translation is marking all affected beans (such as Repositories or DAOs)
 * with the {@code @Repository} annotation, along with defining this post-processor
 * as a bean in the application context.
 *
 * <p>
 *     Bean后置处理器，该处理器自动将持久性异常转换应用于任何带有 @{@link org.springframework.stereotype.Repository Repository}
 *     注解的Bean, 并向公开的代理(现有的AOP代理 或者 任何新生成的实现了接口的代理)添加{@link PersistenceExceptionTranslationAdvisor}
 * <p>
 *     将本地资源异常转换为Spring的{@link org.springframework.dao.DataAccessException DataAccessException}的结构，能够自动检测实现了
 *     {@code PersistenceExceptionTranslator}接口的Bean.
 * <p>
 *     Spring所有的持久化资源工厂均实现了{@code PersistenceExceptionTranslator}接口。因此，启用自动异常转换通常所需要做的就是用@Repository批注标记所有受影响的bean（例如Repository或DAO），并将此后处理器定义为应用程序上下文中的bean。
 * </p>
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see PersistenceExceptionTranslationAdvisor
 * @see org.springframework.stereotype.Repository
 * @see org.springframework.dao.DataAccessException
 * @see org.springframework.dao.support.PersistenceExceptionTranslator
 */
@SuppressWarnings("serial")
public class PersistenceExceptionTranslationPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

	private Class<? extends Annotation> repositoryAnnotationType = Repository.class;


	/**
	 * Set the 'repository' annotation type.
	 * The default repository annotation type is the {@link Repository} annotation.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a class has a
	 * repository role.
	 * <p>
	 *     设置 'repository'注解类型，默认为 {@link Repository}
	 * <p>
	 *     开发者可以自定义注解类型
	 * @param repositoryAnnotationType the desired annotation type
	 */
	public void setRepositoryAnnotationType(Class<? extends Annotation> repositoryAnnotationType) {
		Assert.notNull(repositoryAnnotationType, "'repositoryAnnotationType' must not be null");
		this.repositoryAnnotationType = repositoryAnnotationType;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);

		if (!(beanFactory instanceof ListableBeanFactory)) {
			throw new IllegalArgumentException(
					"Cannot use PersistenceExceptionTranslator autodetection without ListableBeanFactory");
		}
		this.advisor = new PersistenceExceptionTranslationAdvisor(
				(ListableBeanFactory) beanFactory, this.repositoryAnnotationType);
	}

}

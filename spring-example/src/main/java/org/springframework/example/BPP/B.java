package org.springframework.example.BPP;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 注解方式，加了顺序
 */
@Component
@Order(0)
@Primary
public class B implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		System.out.println("B.postProcessBeforeInitialization, order: 0");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		System.out.println("B.postProcessAfterInitialization, order: 0");
		return bean;
	}
}

package org.springframework.example.BPP;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * 注解方式，实现PriorityOrdered
 */
@Component
public class D implements BeanPostProcessor, PriorityOrdered {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		System.out.println("D.postProcessBeforeInitialization, PriorityOrdered: 2");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		System.out.println("D.postProcessAfterInitialization, PriorityOrdered: 2");
		return bean;
	}

	@Override
	public int getOrder() {
		return 2;
	}
}

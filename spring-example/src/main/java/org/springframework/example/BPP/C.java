package org.springframework.example.BPP;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 注解方式，实现Order
 */
@Component
public class C implements BeanPostProcessor, Ordered {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		System.out.println("C.postProcessBeforeInitialization, order: 1");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		System.out.println("C.postProcessAfterInitialization, order: 1");
		return bean;
	}

	@Override
	public int getOrder() {
		return 1;
	}
}

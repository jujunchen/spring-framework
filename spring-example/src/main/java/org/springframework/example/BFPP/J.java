package org.springframework.example.BFPP;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class J implements BeanDefinitionRegistryPostProcessor  {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("J sub-parent");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("J sub add BFPP I");
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(I.class);
		registry.registerBeanDefinition("i", builder.getBeanDefinition());

		//注册F2
		BeanDefinitionBuilder builder2 = BeanDefinitionBuilder.genericBeanDefinition(F2.class);
		registry.registerBeanDefinition("f2", builder2.getBeanDefinition());
	}
}

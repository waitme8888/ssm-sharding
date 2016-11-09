package com.waitme.sharding.mapper;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class MapperScannerConfigurer extends org.mybatis.spring.mapper.MapperScannerConfigurer {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
		super.postProcessBeanDefinitionRegistry(registry);
		String[] beanNames = registry.getBeanDefinitionNames();
		GenericBeanDefinition definition;
		for (String beanName : beanNames) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
			if (beanDefinition instanceof GenericBeanDefinition) {
				definition = (GenericBeanDefinition) beanDefinition;
				if (definition.getBeanClassName() != null && !"".equals(definition.getBeanClassName())
						&& definition.getBeanClassName().equals("org.mybatis.spring.mapper.MapperFactoryBean")) {
					definition.setBeanClass(com.waitme.sharding.mapper.MapperFactoryBean.class);
				}
			}
		}
	}
}

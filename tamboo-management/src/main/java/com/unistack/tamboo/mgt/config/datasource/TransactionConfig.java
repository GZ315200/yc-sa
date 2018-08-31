package com.unistack.tamboo.mgt.config.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.persistence.EntityManagerFactory;

/**
 * 事务配置类 
 */
@Configuration
public class TransactionConfig implements TransactionManagementConfigurer {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Override
	@Bean
    public JpaTransactionManager annotationDrivenTransactionManager() {
		return new JpaTransactionManager(entityManagerFactory);
    }
}

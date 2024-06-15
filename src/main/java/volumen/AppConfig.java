package volumen;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.logging.log4j.util.PropertiesUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

import jakarta.persistence.EntityManagerFactory;
import volumen.util.HibernateUtil;

@Configuration
public class AppConfig {
	@Bean
	Session getSession() {
		return HibernateUtil.getSession();
	}

	@Bean
	LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		Resource config = new ClassPathResource("hibernate.cfg.xml");
		sessionFactory.setConfigLocation(config);
		sessionFactory.setHibernateProperties(hibernateProperties());
		sessionFactory.setPackagesToScan("volumen.model");
		return sessionFactory;
	}
	
	@Autowired
	@Bean(name = "transactionManager")
	HibernateTransactionManager getTransactionManager(
			SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(
				sessionFactory);

		return transactionManager;
	}
	
//	@Bean
//	LocalEntityManagerFactoryBean entityManagerFactory() {
//		LocalEntityManagerFactoryBean entityManagerFactory = new LocalEntityManagerFactoryBean();
//		entityManagerFactory.setPersistenceUnitName("TestPU");
//		entityManagerFactory.afterPropertiesSet();
//		return entityManagerFactory;
//	}
//
	private Properties hibernateProperties() {
		Properties props = new Properties();

		return props;
	}
}

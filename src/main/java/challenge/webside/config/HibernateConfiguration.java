package challenge.webside.config;


import java.util.Properties;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@ComponentScan({ "challenge" })
@PropertySource(value = { "classpath:application.properties" })
public class HibernateConfiguration {
	
	@Autowired
    private Environment environment;

	
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
    	LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] { "challenge" });
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
     }
	
    //@Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("spring.datasource.url"));
        dataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
        dataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));
        dataSource.setConnectionProperties(hibernateProperties());
        return dataSource;
    }
    
    
    private Properties hibernateProperties() {
    	//throw new RuntimeException("not supported");
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("spring.hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("spring.hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("spring.hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("spring.hibernate.ddl-auto"));
        
        //properties.put("log4j.rootLogger", environment.getRequiredProperty("log4j.rootLogger"));
        return properties;        
    }
    
    
}

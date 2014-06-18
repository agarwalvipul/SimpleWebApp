package com.simplewebapp.config;

import com.simplewebapp.dao.RoleDAO;
import com.simplewebapp.dao.UserDAO;
import com.simplewebapp.dao.impl.RoleDAOImpl;
import com.simplewebapp.dao.impl.UserDAOImpl;
import com.simplewebapp.service.MailSenderService;
import com.simplewebapp.service.UserService;
import com.simplewebapp.service.impl.MailSenderServiceImpl;
import com.simplewebapp.service.impl.UserServiceImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import java.util.Properties;

@Configuration
@Import({WebMvcConfig.class})
@EnableTransactionManagement
@PropertySource("classpath:/application.properties")
@ComponentScan("com.simplewebapp")
public class AppConfig implements TransactionManagementConfigurer {

    private static final  Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    Environment env;

    @Autowired
    WebMvcConfig webMvcConfig;

    @Bean
    public DataSource dataSource() {
        logger.info("Create DataSource bean. JDBC URL " + env.getProperty("database.url") + ", for username: " + env.getProperty("database.user"));
        try {
            PoolProperties poolProperties = new PoolProperties();
            poolProperties.setUsername(env.getProperty("database.user"));
            poolProperties.setPassword(env.getProperty("database.password"));
            poolProperties.setUrl(env.getProperty("database.url"));
            poolProperties.setDriverClassName("com.mysql.jdbc.Driver");
            poolProperties.setMaxActive(50);
            poolProperties.setMaxIdle(50);
            poolProperties.setInitialSize(1);

            return new DataSource(poolProperties);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Bean
    public LocalSessionFactoryBean sessionFactoryBean(){
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        hibernateProperties.setProperty("hibernate.default_schema", env.getProperty("database.user"));
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "auto");
//        hibernateProperties.setProperty("hibernate.transaction.factory_class", "org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory");

        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setPackagesToScan("com.simplewebapp.model");
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setHibernateProperties(hibernateProperties);

        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager txManager(){
        return new HibernateTransactionManager(sessionFactoryBean().getObject());
    }

    @Override
    public HibernateTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }

    @Bean
    public UserDAO userDAO(){
        return new UserDAOImpl();
    }

    @Bean
    public RoleDAO roleDAO(){
        return new RoleDAOImpl();
    }

    @Bean
    public UserService userService(){
        return new UserServiceImpl();
    }

    @Bean
    public MailSenderService mailSenderService(){
        return new MailSenderServiceImpl();
    }


}

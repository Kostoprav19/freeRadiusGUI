package lv.freeradiusgui.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"lv.freeradiusgui"})
@EnableTransactionManagement
public class HybernateConfig {

    @Autowired
    AppConfig appConfig;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws PropertyVetoException {

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(appConfig.getDbDriverClass());
        dataSource.setJdbcUrl(appConfig.getDbUrl());
        dataSource.setUser(appConfig.getDbUser());
        dataSource.setPassword(appConfig.getDbPassword());
        dataSource.setMaxPoolSize(appConfig.getDbMaxPoolSize());
        dataSource.setMinPoolSize(appConfig.getDbMinPoolSize());
        dataSource.setCheckoutTimeout(appConfig.getDbCheckoutTimeout());
        dataSource.setMaxStatements(appConfig.getDbMaxStatements());
        dataSource.setIdleConnectionTestPeriod(appConfig.getDbIdleConnectionTestPeriod());

        return dataSource;
    }

    private Properties hibernateProperties() {

        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");

        return properties;
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) throws Exception {

        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan("lv.freeradiusgui.domain");
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        sessionFactoryBean.afterPropertiesSet();
        return sessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}

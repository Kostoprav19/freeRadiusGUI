package lv.freeradiusgui.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"lv.freeradiusgui"})
@EnableTransactionManagement
public class HybernateConfig {

    @Autowired AppConfig appConfig;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(appConfig.getDbUrl());
        cfg.setUsername(appConfig.getDbUser());
        cfg.setPassword(appConfig.getDbPassword());
        cfg.setDriverClassName(appConfig.getDbDriverClass());
        cfg.setMaximumPoolSize(appConfig.getDbMaxPoolSize());
        cfg.setMinimumIdle(appConfig.getDbMinPoolSize());
        cfg.setConnectionTimeout(appConfig.getDbCheckoutTimeout());
        cfg.setPoolName("freeradiusgui-hikari");
        return new HikariDataSource(cfg);
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

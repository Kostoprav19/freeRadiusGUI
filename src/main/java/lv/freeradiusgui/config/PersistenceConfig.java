package lv.freeradiusgui.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"lv.freeradiusgui"})
@EnableTransactionManagement
@EnableJdbcRepositories(basePackages = "lv.freeradiusgui.repositories")
public class PersistenceConfig extends AbstractJdbcConfiguration {

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

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

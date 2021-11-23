package booster.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:database.properties")
@RequiredArgsConstructor
class DbConfig {

    private final Environment environment;

    @Bean
    DataSource dataSource() {
        var driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(environment.getProperty("dburl"));
        driverManagerDataSource.setUsername(environment.getProperty("dbuser"));
        driverManagerDataSource.setPassword(environment.getProperty("dbpassword"));
        driverManagerDataSource.setDriverClassName(environment.getProperty("dbdriver"));
        return driverManagerDataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new JdbcTransactionManager(dataSource());
    }

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager manager) {
        return new TransactionTemplate(manager);
    }

}

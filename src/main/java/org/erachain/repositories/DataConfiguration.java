package org.erachain.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:db.properties")
public class DataConfiguration {
    @Value("${spring.datasource.driver-class-name}")
    private String className;
    @Value("${spring.datasource.url}")
    private String url;
    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(className);
        dataSource.setUrl(url);
        return dataSource;
    }

}

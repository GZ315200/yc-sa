package com.unistack.tamboo.mgt.config.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.beans.PropertyVetoException;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
@Configuration
public class DataSourceConfig {

    @Autowired
    private Environment env;

    @Bean(name = "mgtDataSource")
    @ConfigurationProperties(prefix = "mgt.dataSource")
    public HikariDataSource systemDataSource() throws PropertyVetoException {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("mgt.dataSource.jdbc.url"));
        config.setUsername(env.getProperty("mgt.dataSource.jdbc.username"));
        config.setPassword(env.getProperty("mgt.dataSource.jdbc.password"));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(500);
        return new HikariDataSource(config);
    }
}

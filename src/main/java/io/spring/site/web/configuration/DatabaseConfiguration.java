package io.spring.site.web.configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

        boolean inMemory = this.environment.acceptsProfiles(this.environment
                .getDefaultProfiles())
                || this.environment.acceptsProfiles("acceptance");

        if (inMemory) {
            dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        } else {
            dataSource.setDriverClassName("org.postgresql.Driver");
        }

        CloudEnvironment cloudEnvironment = new CloudEnvironment();
        if (cloudEnvironment.getServiceDataByName("sagan-db") != null) {
            RdbmsServiceInfo serviceInfo = cloudEnvironment.getServiceInfo(
                    "sagan-db", RdbmsServiceInfo.class);
            dataSource.setUrl(serviceInfo.getUrl());
            dataSource.setUsername(serviceInfo.getUserName());
            dataSource.setPassword(serviceInfo.getPassword());
        } else {
            if (inMemory) {
                dataSource.setUrl("jdbc:hsqldb:mem:sagan-db");
                dataSource.setUsername("sa");
                dataSource.setPassword("");
            } else {
                dataSource.setUrl("jdbc:postgresql://localhost:5432/sagan-db");
                dataSource.setUsername("user");
                dataSource.setPassword("changeme");
            }
        }

        dataSource.setMaxActive(20);
        dataSource.setMaxIdle(8);
        dataSource.setMinIdle(8);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setValidationQuery("SELECT 1");
        return dataSource;
    }

    @Bean
    public SpringLiquibase springLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:liquibase/changeset.yaml");
        return liquibase;
    }
}

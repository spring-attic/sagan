package sagan.util.service.db;

import javax.sql.DataSource;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.googlecode.flyway.core.Flyway;

@Configuration
public class DatabaseConfig {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

        boolean inMemory =
                this.environment.acceptsProfiles(this.environment.getDefaultProfiles())
                        || this.environment.acceptsProfiles("acceptance");

        if (inMemory) {
            dataSource.setDriverClassName("org.h2.Driver");
        } else {
            dataSource.setDriverClassName("org.postgresql.Driver");
        }

        CloudEnvironment cloudEnvironment = new CloudEnvironment();
        if (cloudEnvironment.getServiceDataByName("sagan-db") != null) {
            RdbmsServiceInfo serviceInfo = cloudEnvironment.getServiceInfo("sagan-db", RdbmsServiceInfo.class);
            dataSource.setUrl(serviceInfo.getUrl());
            dataSource.setUsername(serviceInfo.getUserName());
            dataSource.setPassword(serviceInfo.getPassword());
        } else {
            if (inMemory) {
                dataSource.setUrl("jdbc:h2:mem:sagan;MODE=PostgreSQL");
                dataSource.setUsername("sa");
                dataSource.setPassword("");
            } else {
                dataSource.setUrl("jdbc:postgresql://localhost:5432/sagan");
                dataSource.setUsername("carl");
                dataSource.setPassword("");
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
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setLocations("database");
        flyway.setDataSource(dataSource);
        flyway.migrate();
        return flyway;
    }
}

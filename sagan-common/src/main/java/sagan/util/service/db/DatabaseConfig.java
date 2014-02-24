package sagan.util.service.db;

import sagan.SaganProfiles;

import javax.sql.DataSource;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.googlecode.flyway.core.Flyway;

public abstract class DatabaseConfig {

    public static final String CACHE_NAME = "cache.database";
    public static final String CACHE_TTL = "${cache.database.timetolive:60}";

    @Bean
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

        dataSource.setMaxActive(20);
        dataSource.setMaxIdle(8);
        dataSource.setMinIdle(8);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setValidationQuery("SELECT 1");

        this.configureDataSource(dataSource);
        this.migrateSchema(dataSource);

        return dataSource;
    }

    protected abstract void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource);

    protected void migrateSchema(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setLocations("database");
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }
}

@Configuration
@Profile(SaganProfiles.STANDALONE)
class StandaloneDatabaseConfig extends DatabaseConfig {

    @Override
    public void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:sagan;MODE=PostgreSQL");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
    }
}

@Configuration
@Profile(SaganProfiles.CLOUDFOUNDRY)
class CloudFoundryDatabaseConfig extends DatabaseConfig {

    @Override
    public void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
        CloudEnvironment cloudEnvironment = new CloudEnvironment();
        if (cloudEnvironment.getServiceDataByName("sagan-db") == null) {
            throw new IllegalStateException("Could not locate CloudFoundry service 'sagan-db'");
        }

        RdbmsServiceInfo serviceInfo = cloudEnvironment.getServiceInfo("sagan-db", RdbmsServiceInfo.class);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(serviceInfo.getUrl());
        dataSource.setUsername(serviceInfo.getUserName());
        dataSource.setPassword(serviceInfo.getPassword());
    }
}

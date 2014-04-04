package sagan;

import javax.sql.DataSource;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.googlecode.flyway.core.Flyway;

public abstract class DatabaseConfig {

    public static final String CACHE_NAME = "cache.database";
    public static final String CACHE_TTL = "${cache.database.timetolive:60}";

    protected void configureDataSource(DataSource dataSource) {
        org.apache.tomcat.jdbc.pool.DataSource pooledDataSource = getPooledDataSource(dataSource);

        pooledDataSource.setMaxActive(20);
        pooledDataSource.setMaxIdle(8);
        pooledDataSource.setMinIdle(8);
        pooledDataSource.setTestOnBorrow(false);
        pooledDataSource.setTestOnReturn(false);

        this.migrateSchema(dataSource);
    }

    protected void migrateSchema(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setLocations("database");
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

    private org.apache.tomcat.jdbc.pool.DataSource getPooledDataSource(DataSource dataSource) {
        if (!org.apache.tomcat.jdbc.pool.DataSource.class.isInstance(dataSource)) {
            throw new IllegalStateException("DataSource must be of type " +
                    org.apache.tomcat.jdbc.pool.DataSource.class.getName());
        }

        return (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
    }
}

@Configuration
@Profile(SaganProfiles.STANDALONE)
class StandaloneDatabaseConfig extends DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:sagan;MODE=PostgreSQL");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setValidationQuery("SELECT 1");

        configureDataSource(dataSource);

        return dataSource;
    }
}

@Configuration
@Profile(SaganProfiles.CLOUDFOUNDRY)
class CloudFoundryDatabaseConfig extends DatabaseConfig {

    @Bean
    public Cloud cloud() {
        CloudFactory cloudFactory = new CloudFactory();
        return cloudFactory.getCloud();
    }

    @Bean
    public DataSource dataSource(Cloud cloud) {
        DataSource dataSource = cloud.getServiceConnector("sagan-db", DataSource.class, null);
        configureDataSource(dataSource);
        return dataSource;
    }
}

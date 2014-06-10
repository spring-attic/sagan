package sagan;

import javax.sql.DataSource;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

public abstract class DatabaseConfig {

    public static final String CACHE_NAME = "cache.database";
    public static final String CACHE_TTL = "${cache.database.timetolive:60}";

    @Bean
    public abstract DataSource dataSource();

    protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
        dataSource.setMaxActive(20);
        dataSource.setMaxIdle(8);
        dataSource.setMinIdle(8);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
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
        return new CloudFactory().getCloud();
    }

    @Bean
    public DataSource dataSource() {
        DataSource dataSource = cloud().getServiceConnector("sagan-db", DataSource.class, null);
        Assert.isInstanceOf(org.apache.tomcat.jdbc.pool.DataSource.class, dataSource);
        configureDataSource((org.apache.tomcat.jdbc.pool.DataSource) dataSource);
        return dataSource;
    }
}

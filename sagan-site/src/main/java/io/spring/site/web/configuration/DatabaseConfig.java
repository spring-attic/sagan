package io.spring.site.web.configuration;

import com.googlecode.flyway.core.Flyway;
import io.spring.common.config.DataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DataSourceConfig.class)
public class DatabaseConfig {

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Bean
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setLocations("database");
        flyway.setDataSource(dataSourceConfig.dataSource());
        flyway.migrate();
        return flyway;
    }
}

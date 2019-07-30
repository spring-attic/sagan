package sagan;

import org.apache.http.impl.client.HttpClientBuilder;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * The entry point for the Sagan web application.
 * <p>
 * Main configuration resource for the Sagan web application. The use of @ComponentScan
 * here ensures that other @Configuration classes such as {@link MvcConfig} and
 * {@link SecurityConfig} are included as well.
 */
@SpringBootApplication
@EnableConfigurationProperties(SiteProperties.class)
public class SiteApplication {

    public static void main(String[] args) {
        new SaganApplication(SiteApplication.class).run(args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean
    public FlywayMigrationStrategy repairStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}

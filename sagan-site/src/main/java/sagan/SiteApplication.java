package sagan;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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

}

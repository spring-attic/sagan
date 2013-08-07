package org.springframework.redirector;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class })
@Configuration
@ComponentScan(basePackages = "org.springframework.redirector")
public class RedirectorConfiguration {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(
				RedirectorConfiguration.class);
		application.run(args);
	}

	@Bean
	public RedirectMappingService redirectMappingService() throws IOException {
		InputStream yaml = new ClassPathResource("/redirect_mappings.yml", getClass())
				.getInputStream();
		return new RedirectMappingService(new RedirectMappingParser().parseMappings(yaml));
	}

}

package org.springframework.redirector;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@EnableAutoConfiguration
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

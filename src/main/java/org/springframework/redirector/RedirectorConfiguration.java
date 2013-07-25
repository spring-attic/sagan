package org.springframework.redirector;

import org.springframework.autoconfigure.EnableAutoConfiguration;
import org.springframework.bootstrap.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "org.springframework.redirector")
public class RedirectorConfiguration {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(RedirectorConfiguration.class);
		application.run(args);
	}
}

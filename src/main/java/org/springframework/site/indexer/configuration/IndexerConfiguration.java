package org.springframework.site.indexer.configuration;

import org.springframework.bootstrap.SpringApplication;
import org.springframework.bootstrap.context.annotation.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.site.search.configuration.SearchClientConfiguration;
import org.springframework.site.web.configuration.DocumentationConfiguration;
import org.springframework.site.web.configuration.GitHubConfiguration;
import org.springframework.site.web.configuration.SecurityConfiguration;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = {"org.springframework.site.indexer", "org.springframework.site.search"})
@EnableScheduling
@Import({SearchClientConfiguration.class, DocumentationConfiguration.class, GitHubConfiguration.class, SecurityConfiguration.class})
public class IndexerConfiguration {

	public static void main(String[] args) {
		build(IndexerConfiguration.class).run(args);
	}

	public static SpringApplication build(Class<?>... config) {
		SpringApplication application = new SpringApplication(config);
		application.setDefaultCommandLineArgs(
				"--server.port=9000");
		return application;
	}

	@Bean
	public TaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		return scheduler;
	}

}

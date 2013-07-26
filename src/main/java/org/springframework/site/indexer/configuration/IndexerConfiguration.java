package org.springframework.site.indexer.configuration;

import org.springframework.autoconfigure.EnableAutoConfiguration;
import org.springframework.bootstrap.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.site.domain.StaticPageMapper;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.site.domain.documentation.DocumentationYamlParser;
import org.springframework.site.search.configuration.SearchClientConfiguration;
import org.springframework.site.web.configuration.GitHubConfiguration;
import org.springframework.site.web.configuration.SecurityConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "org.springframework.site.indexer",
		"org.springframework.site.search",
		"org.springframework.site.domain.documentation" })
@EnableScheduling
@Import({ SearchClientConfiguration.class, GitHubConfiguration.class,
		SecurityConfiguration.class })
public class IndexerConfiguration {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(IndexerConfiguration.class);
		application.run(args);
	}

	@Bean
	public TaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		return scheduler;
	}

	@Bean
	public ExecutorService executorService() {
		return Executors.newFixedThreadPool(10);
	}

	@Bean
	public StaticPageMapper staticPageMapper(ResourcePatternResolver resourceResolver) {
		return new StaticPageMapper(resourceResolver);
	}

	@Bean
	public DocumentationService documentationService() throws IOException {
		InputStream yaml = new ClassPathResource("/documentation.yml", getClass()).getInputStream();
		return new DocumentationService(new DocumentationYamlParser().parse(yaml));
	}

}

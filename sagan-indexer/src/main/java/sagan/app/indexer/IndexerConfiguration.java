package sagan.app.indexer;

import sagan.util.service.db.DatabaseConfig;
import sagan.util.service.github.GitHubConfiguration;
import sagan.search.SearchClientConfiguration;
import sagan.util.web.StaticPagePathFinder;
import sagan.projects.service.ProjectMetadataService;
import sagan.projects.service.ProjectMetadataYamlParser;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAutoConfiguration
@Configuration
@EnableScheduling
@Import({DatabaseConfig.class, SearchClientConfiguration.class, GitHubConfiguration.class})
@ComponentScan({ "io.spring.site.indexer", "io.spring.site.domain", "io.spring.site.search" })
public class IndexerConfiguration {

    @Value("classpath:/project-metadata.yml")
    private Resource projectMetadata;

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
    public StaticPagePathFinder staticPageMapper(ResourcePatternResolver resourceResolver) {
        return new StaticPagePathFinder(resourceResolver);
    }

    @Bean
    public ProjectMetadataService projectMetadataService() throws IOException {
        return new ProjectMetadataYamlParser().createServiceFromYaml(this.projectMetadata
                .getInputStream());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Serializer simpleXmlSerializer() {
        return new Persister();
    }

}

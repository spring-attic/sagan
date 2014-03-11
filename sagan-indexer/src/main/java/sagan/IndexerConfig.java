package sagan;

import sagan.support.StaticPagePathFinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@Configuration
@EnableScheduling
@ComponentScan
@EntityScan
@EnableJpaRepositories
class IndexerConfig {

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
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Serializer simpleXmlSerializer() {
        return new Persister();
    }

}

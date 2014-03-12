package integration;

import sagan.SiteConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import sagan.support.github.StubGithubRestClient;

import static org.mockito.Mockito.mock;

@Configuration
@Import(SiteConfig.class)
class IntegrationTestsConfig {

    @Primary
    @Bean
    public RestTemplate mockRestTemplate() {
        return mock(RestTemplate.class);
    }

    @Primary
    @Bean
    public StubGithubRestClient stubGithubRestClient() {
        return new StubGithubRestClient();
    }
}

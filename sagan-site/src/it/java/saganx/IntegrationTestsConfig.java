package saganx;

import sagan.SiteApplication;
import sagan.support.github.StubGithubRestClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@Configuration
@Import(SiteApplication.class)
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

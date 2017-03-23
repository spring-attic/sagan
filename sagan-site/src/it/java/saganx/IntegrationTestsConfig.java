package saganx;

import sagan.support.github.StubGithubRestClient;

import javax.servlet.Filter;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static sagan.support.SecurityRequestPostProcessors.*;

@Configuration
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

    @Bean
    public MockMvcBuilderCustomizer mockMvcBuilderCustomizer(ListableBeanFactory beanFactory) {
        Filter springSecurityFilterChain = beanFactory.getBean("springSecurityFilterChain", Filter.class);
        return builder -> builder.addFilters(springSecurityFilterChain)
                .defaultRequest(get("/").with(csrf()).with(user(123L).roles("ADMIN")))
                .build();

    }
}

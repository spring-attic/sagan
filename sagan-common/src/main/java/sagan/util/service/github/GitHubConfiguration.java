package sagan.util.service.github;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;

@Configuration
public class GitHubConfiguration {

    @Value("${GITHUB_CLIENT_ID:${github.client.id:none}}")
    private String githubClientId;

    @Value("${GITHUB_CLIENT_SECRET:${github.client.secret:none}}")
    private String githubClientSecret;

    @Bean
    public GitHubConnectionFactory gitHubConnectionFactory() {
        GitHubConnectionFactory factory = new GitHubConnectionFactory(githubClientId, githubClientSecret);
        factory.setScope("user");
        return factory;
    }

    @Bean
    public GitHub gitHubTemplate() {
        return new GuideGitHubTemplate();
    }

    private static class GuideGitHubTemplate extends GitHubTemplate {

        @Override
        protected List<HttpMessageConverter<?>> getMessageConverters() {
            List<HttpMessageConverter<?>> converters = new ArrayList<>();
            converters.add(new JsonStringConverter());
            converters.add(new MarkdownHtmlConverter());
            converters.add(new DownloadConverter());
            converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
            return converters;
        }
    }
}

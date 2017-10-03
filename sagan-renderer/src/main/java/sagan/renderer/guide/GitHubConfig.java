package sagan.renderer.guide;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.util.StringUtils;

/**
 * Configuration for authentication against and access to GitHub's API. See
 * application.yml for details on each of the <code>@Value</code>-annotated fields.
 */
@Configuration
public class GitHubConfig {

    public static final Log logger = LogFactory.getLog(GitHubConfig.class);

    @Value("${github.access.token}")
    private String githubAccessToken;

    @Bean
    public GitHub gitHubTemplate() {
        if (StringUtils.isEmpty(githubAccessToken)) {
            return new GuideGitHubTemplate();
        }
        return new GuideGitHubTemplate(githubAccessToken);
    }

    private static class GuideGitHubTemplate extends GitHubTemplate {

        private GuideGitHubTemplate() {
            super();
            logger.warn("GitHub API access will be rate-limited at 60 req/hour");
        }

        private GuideGitHubTemplate(String githubAccessToken) {
            super(githubAccessToken);
        }

        @Override
        protected List<HttpMessageConverter<?>> getMessageConverters() {
            List<HttpMessageConverter<?>> converters = new ArrayList<>();
            converters.add(new DownloadConverter());
            return converters;
        }
    }
}

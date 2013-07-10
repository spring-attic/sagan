package org.springframework.site.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.site.domain.guides.GuideHtmlConverter;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan({"org.springframework.site.domain.guides", "org.springframework.site.domain.services"})
public class GitHubConfiguration {

	@Value("${GITHUB_CLIENT_ID:${github.client.id:none}}")
	private String githubClientId;

	@Value("${GITHUB_CLIENT_SECRET:${github.client.secret:none}}")
	private String githubClientSecret;

	@Value("${GITHUB_ACCESS_TOKEN:${github.access.token:5a0e089d267693b45926d7f620d85a2eb6a85da6}}")
	private String accessToken;

	@Bean
	public GitHubConnectionFactory gitHubConnectionFactory() {
		GitHubConnectionFactory factory = new GitHubConnectionFactory(githubClientId,
				githubClientSecret);
		factory.setScope("user");
		return factory;
	}

	@Bean
	public GitHub gitHubTemplate() {
		return new GuideGitHubTemplate(accessToken);
	}

	private static class GuideGitHubTemplate extends GitHubTemplate {

		private GuideGitHubTemplate(String accessToken) {
			super(accessToken);
		}

		@Override
		protected List<HttpMessageConverter<?>> getMessageConverters() {
			List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
			converters.add(new GuideHtmlConverter());
			converters.addAll(super.getMessageConverters());
			return converters;
		}
	}
}

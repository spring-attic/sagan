package org.springframework.site.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;

@Configuration
@ComponentScan({"org.springframework.site.guides", "org.springframework.site.services"})
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
		return new GitHubTemplate(accessToken);
	}

}

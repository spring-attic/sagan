package sagan.site;

import sagan.SiteProperties;
import sagan.site.support.security.GitHubAuthenticationManager;
import sagan.site.support.security.GitHubMemberOAuth2UserService;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class GitHubSecurityConfig {

	@Bean
	public GitHubAuthenticationManager gitHubAuthenticationManager(ClientRegistrationRepository oauthClients,
			GitHubMemberOAuth2UserService userService) {
		return new GitHubAuthenticationManager(oauthClients, userService);
	}

	@Bean
	public GitHubMemberOAuth2UserService gitHubMemberOAuth2UserService(RestTemplateBuilder builder, SiteProperties properties) {
		return new GitHubMemberOAuth2UserService(builder, properties.getGithub().getOrg(), properties.getGithub().getTeam());
	}
}

package sagan.site.support.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import sagan.SiteProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(SiteProperties.class)
@TestPropertySource(properties = { "sagan.site.github.org=spring-io", "sagan.site.github.team=spring-team" })
class GitHubMemberOAuth2UserServiceTests {

	@Autowired
	private GitHubMemberOAuth2UserService userService;

	@Autowired
	private MockRestServiceServer server;

	@Test
	void shouldRejectUserIfNotMember() {
		this.server.expect(requestTo("https://api.github.com/user"))
				.andRespond(withSuccess(getClassPathResource("user.json"), MediaType.APPLICATION_JSON));
		this.server.expect(requestTo("https://api.github.com/orgs/spring-io/teams/spring-team/memberships/octocat"))
				.andRespond(withStatus(HttpStatus.NOT_FOUND));
		OAuth2User user = this.userService.loadUser(userRequest());
		assertThat(hasRoleAdmin(user)).isFalse();
	}

	@Test
	void shouldRejectUserIfPendingMembership() {
		this.server.expect(requestTo("https://api.github.com/user"))
				.andRespond(withSuccess(getClassPathResource("user.json"), MediaType.APPLICATION_JSON));
		this.server.expect(requestTo("https://api.github.com/orgs/spring-io/teams/spring-team/memberships/octocat"))
				.andRespond(withSuccess(getClassPathResource("pending_membership.json"), MediaType.APPLICATION_JSON));
		OAuth2User user = this.userService.loadUser(userRequest());
		assertThat(hasRoleAdmin(user)).isFalse();
	}

	@Test
	void shouldAcceptUserIfMember() {
		this.server.expect(requestTo("https://api.github.com/user"))
				.andRespond(withSuccess(getClassPathResource("user.json"), MediaType.APPLICATION_JSON));
		this.server.expect(requestTo("https://api.github.com/orgs/spring-io/teams/spring-team/memberships/octocat"))
				.andRespond(withSuccess(getClassPathResource("active_membership.json"), MediaType.APPLICATION_JSON));
		OAuth2User user = this.userService.loadUser(userRequest());
		assertThat(hasRoleAdmin(user)).isTrue();
	}

	private OAuth2UserRequest userRequest() {
		return new OAuth2UserRequest(clientRegistration().build(), scopes("user"));
	}

	private ClientRegistration.Builder clientRegistration() {
		return TestClientRegistrations.clientRegistration()
				.userInfoUri("https://api.github.com/user");
	}

	private static OAuth2AccessToken scopes(String... scopes) {
		return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				"scopes",
				Instant.now(),
				Instant.now().plus(Duration.ofDays(1)),
				new HashSet<>(Arrays.asList(scopes)));
	}

	private boolean hasRoleAdmin(OAuth2User user) {
		return user.getAuthorities().stream().anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, GitHubMemberOAuth2UserService.class);
	}

	@TestConfiguration
	static class SecurityConfig {

		@Bean
		public GitHubMemberOAuth2UserService userService(RestTemplateBuilder builder, SiteProperties properties) {
			GitHubMemberOAuth2UserService userService = new GitHubMemberOAuth2UserService(builder, properties.getGithub().getOrg(),
					properties.getGithub().getTeam());
			return userService;
		}
	}


}
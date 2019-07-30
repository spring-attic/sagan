package sagan.security;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rob Winch
 */
public class GithubMemberOAuth2UserServiceTests {
	@Rule
	public WireMockRule wiremock = new WireMockRule(0);

	private String gitHubTeamId = "123";

	private GithubMemberOAuth2UserService members;

	@Before
	public void setup() {
		this.members = new GithubMemberOAuth2UserService(this.gitHubTeamId);
		this.members.setMemberHost(this.wiremock.baseUrl());
	}

	@Test
	public void loadUserWhenMemberThenRoleAdmin() {
		addUserStub();
		this.wiremock.addStubMapping(stubFor(get(urlEqualTo("/teams/123/members/octocat")).willReturn(aResponse().withStatus(
				HttpStatus.NO_CONTENT.value()))));

		assertThat(this.members.loadUser(userRequest()).getAuthorities()).extracting(GrantedAuthority::getAuthority).contains("ROLE_ADMIN");
	}

	@Test
	public void loadUserWhenNotMemberThenNoRoleAdmin() {
		addUserStub();
		this.wiremock.addStubMapping(stubFor(get(urlEqualTo("/teams/123/members/octocat")).willReturn(aResponse().withStatus(
				HttpStatus.NOT_FOUND.value()))));

		assertThat(this.members.loadUser(userRequest()).getAuthorities()).extracting(GrantedAuthority::getAuthority).doesNotContain("ROLE_ADMIN");
	}

	private OAuth2UserRequest userRequest() {
		return new OAuth2UserRequest(clientRegistration().build(), scopes("user"));
	}

	private void addUserStub() {
		String json = "{\n"
				+ "  \"login\": \"octocat\",\n"
				+ "  \"id\": 1,\n"
				+ "  \"node_id\": \"MDQ6VXNlcjE=\",\n"
				+ "  \"avatar_url\": \"https://github.com/images/error/octocat_happy.gif\",\n"
				+ "  \"gravatar_id\": \"\",\n"
				+ "  \"url\": \"https://api.github.com/users/octocat\",\n"
				+ "  \"html_url\": \"https://github.com/octocat\",\n"
				+ "  \"followers_url\": \"https://api.github.com/users/octocat/followers\",\n"
				+ "  \"following_url\": \"https://api.github.com/users/octocat/following{/other_user}\",\n"
				+ "  \"gists_url\": \"https://api.github.com/users/octocat/gists{/gist_id}\",\n"
				+ "  \"starred_url\": \"https://api.github.com/users/octocat/starred{/owner}{/repo}\",\n"
				+ "  \"subscriptions_url\": \"https://api.github.com/users/octocat/subscriptions\",\n"
				+ "  \"organizations_url\": \"https://api.github.com/users/octocat/orgs\",\n"
				+ "  \"repos_url\": \"https://api.github.com/users/octocat/repos\",\n"
				+ "  \"events_url\": \"https://api.github.com/users/octocat/events{/privacy}\",\n"
				+ "  \"received_events_url\": \"https://api.github.com/users/octocat/received_events\",\n"
				+ "  \"type\": \"User\",\n"
				+ "  \"site_admin\": false,\n"
				+ "  \"name\": \"monalisa octocat\",\n"
				+ "  \"company\": \"GitHub\",\n"
				+ "  \"blog\": \"https://github.com/blog\",\n"
				+ "  \"location\": \"San Francisco\",\n"
				+ "  \"email\": \"octocat@github.com\",\n"
				+ "  \"hireable\": false,\n"
				+ "  \"bio\": \"There once was...\",\n"
				+ "  \"public_repos\": 2,\n"
				+ "  \"public_gists\": 1,\n"
				+ "  \"followers\": 20,\n"
				+ "  \"following\": 0,\n"
				+ "  \"created_at\": \"2008-01-14T04:33:35Z\",\n"
				+ "  \"updated_at\": \"2008-01-14T04:33:35Z\",\n"
				+ "  \"private_gists\": 81,\n"
				+ "  \"total_private_repos\": 100,\n"
				+ "  \"owned_private_repos\": 100,\n"
				+ "  \"disk_usage\": 10000,\n"
				+ "  \"collaborators\": 8,\n"
				+ "  \"two_factor_authentication\": true,\n"
				+ "  \"plan\": {\n"
				+ "    \"name\": \"Medium\",\n"
				+ "    \"space\": 400,\n"
				+ "    \"private_repos\": 20,\n"
				+ "    \"collaborators\": 0\n"
				+ "  }\n"
				+ "}";
		ResponseDefinitionBuilder response = aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody(json);
		this.wiremock.addStubMapping(stubFor(get(urlEqualTo("/user")).willReturn(response)));
	}

	private ClientRegistration.Builder clientRegistration() {
		return TestClientRegistrations.clientRegistration()
				.userInfoUri(this.wiremock.url("/user"));
	}

	public static OAuth2AccessToken scopes(String... scopes) {
		return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				"scopes",
				Instant.now(),
				Instant.now().plus(Duration.ofDays(1)),
				new HashSet<>(Arrays.asList(scopes)));
	}

}
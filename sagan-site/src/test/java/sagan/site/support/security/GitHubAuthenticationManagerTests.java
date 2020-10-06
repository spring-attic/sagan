package sagan.site.support.security;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.TestClientRegistrations;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubAuthenticationManagerTests {

	@Mock
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> githubMembers;

	@Mock
	private ClientRegistrationRepository clients;

	@Captor
	ArgumentCaptor<OAuth2UserRequest> userRequestArg;

	private GitHubAuthenticationManager manager;

	@BeforeEach
	public void setup() {
		this.manager = new GitHubAuthenticationManager(this.clients, this.githubMembers);
	}

	@Test
	public void authenticateWhenLoadUserThenSuccess() {
		ClientRegistration clientRegistration = TestClientRegistrations.clientRegistration()
				.build();
		String expectedOAuthTokenValue = "oauthtoken";
		when(this.clients.findByRegistrationId(any())).thenReturn(clientRegistration);
		DefaultOAuth2User user = new DefaultOAuth2User(
				AuthorityUtils.createAuthorityList("ROLE_USER"),
				Collections.singletonMap("user", "octocat"),
				"user");
		when(this.githubMembers.loadUser(any())).thenReturn(user);

		Authentication result = this.manager
				.authenticate(new UsernamePasswordAuthenticationToken("octocat", expectedOAuthTokenValue));

		assertThat(result.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsOnly("ROLE_API", "ROLE_USER");
		verify(this.clients).findByRegistrationId("github");
		verify(this.githubMembers).loadUser(this.userRequestArg.capture());
		OAuth2UserRequest userRequest = this.userRequestArg.getValue();
		assertThat(userRequest.getAccessToken().getTokenValue()).isEqualTo(expectedOAuthTokenValue);
		assertThat(userRequest.getClientRegistration()).isEqualTo(clientRegistration);
	}

	@Test
	public void authenticateWhenLoadUserErrorThenException() {
		ClientRegistration clientRegistration = TestClientRegistrations.clientRegistration()
				.build();
		when(this.clients.findByRegistrationId(any())).thenReturn(clientRegistration);
		when(this.githubMembers.loadUser(any())).thenThrow(new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));

		assertThatCode(() -> this.manager
				.authenticate(new UsernamePasswordAuthenticationToken("octocat", "invalid"))).isInstanceOf(OAuth2AuthenticationException.class);
	}
}
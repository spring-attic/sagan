package sagan;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Rob Winch
 */
@RunWith(SpringRunner.class)
@SecurityTest
public class SecurityConfigAuthenticationTests {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> responseClient;

	@MockBean
	OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

	@MockBean
	ClientRegistrationRepository oauthClients;

	@Test
	public void projectMetadataWhenAuthenticateThenOk() throws Exception {
		when(this.oauthClients.findByRegistrationId(any())).thenReturn(TestClientRegistrations.clientRegistration().build());
		DefaultOAuth2User user = new DefaultOAuth2User(
				AuthorityUtils.createAuthorityList("ROLE_ADMIN"),
				Collections.singletonMap("user", "octocat"),
				"user");
		when(this.userService.loadUser(any())).thenReturn(user);

		this.mockMvc.perform(post("/project_metadata/spring-security").with(httpBasic("token","")))
			.andExpect(status().isOk());
	}

	@Test
	public void projectMetadataWhenFailAuthenticateThenForbidden() throws Exception {
		when(this.oauthClients.findByRegistrationId(any())).thenReturn(TestClientRegistrations.clientRegistration().build());
		when(this.userService.loadUser(any())).thenThrow(new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));
		this.mockMvc.perform(post("/project_metadata/spring-security").with(httpBasic("token","")))
			// we do not want to trigger basic authentication prompt as this can enable CSRF attacks
			.andExpect(status().isForbidden());
	}

	@Test
	public void oauth2LoginWhenSuccessThenAuthenticated() throws Exception {
		ClientRegistration registration = TestClientRegistrations.clientRegistration()
				.registrationId("github")
				.redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}")
				.build();
		when(this.oauthClients.findByRegistrationId(any())).thenReturn(registration);
		DefaultOAuth2User user = new DefaultOAuth2User(
				AuthorityUtils.createAuthorityList("ROLE_ADMIN"),
				Collections.singletonMap("user", "octocat"),
				"user");
		when(this.userService.loadUser(any())).thenReturn(user);
		OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse
				.withToken("123")
				.tokenType(OAuth2AccessToken.TokenType.BEARER)
				.build();
		when(this.responseClient.getTokenResponse(any())).thenReturn(tokenResponse);
		MockHttpSession session = new MockHttpSession();

		MvcResult oauth2RequestResult = this.mockMvc
				.perform(get("/oauth2/authorization/github").session(session))
				.andReturn();

		String redirectUrl = oauth2RequestResult.getResponse().getRedirectedUrl();

		String state = URLDecoder.decode(UriComponentsBuilder.fromHttpUrl(redirectUrl).build(true).getQueryParams().getFirst("state"),
				"UTF-8");
		this.mockMvc.perform(get("/login/oauth2/code/github?code=zzbb3fRaa44&state={state}", state).session(session))
			.andExpect(authenticated())
			.andExpect(status().is3xxRedirection());
	}
}
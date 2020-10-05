package sagan.site;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
public class TestSecurityConfig {

	@Bean
	public AuthenticationManager testAuthenticationManager() {
		return new TestAuthenticationManager();
	}

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> testOAuth2UserService() {
		return new TestOAuth2UserService();
	}

	static class TestAuthenticationManager implements AuthenticationManager {
		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			throw new AuthenticationServiceException("Cannot authenticate " + authentication);
		}
	}

	static class TestOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

		@Override
		public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
			throw new OAuth2AuthenticationException(new OAuth2Error("test error"));
		}
	}

}
